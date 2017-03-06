package anaptyksi2;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.jws.WebService;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import sun.awt.Mutex;

enum NetworkType {

    GSM, UMTS, WLAN, WIMAX
}

class Checker extends Thread { //elegkths gia to 3t

    protected CPC c;
    protected Mutex removeLock;

    Checker(CPC c, Mutex removeLock) {
        this.c = c;
        this.removeLock = removeLock;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(2 * c.timeInterval);  //to interval pou pistevoume einai kalutero gia logous apodotikothtas kai arketa on-time gia deletion twn offline bs
                System.out.println("I slept for " + 2 * c.timeInterval + " milliseconds.");

            } catch (InterruptedException ex) {
                System.out.println("Error during sleep with code " + ex);
            }
            java.util.Date date = new java.util.Date();
            Timestamp nowTime = new Timestamp(date.getTime()); //kratame thn twrinh wra
            Set set = c.baseStations.entrySet();
            Iterator i = set.iterator();
            BaseStationShort b; //metavlith temp gia na apothikevoume ton upo elegxo bs
            //System.out.println("Bs in hashmaps.");
            while (i.hasNext()) { //elegxoume olo to hashmap mas gia 3t removes
                //System.out.println("[[" + i.hashCode());
                b = (BaseStationShort) ((Map.Entry) i.next()).getValue();
                System.out.println("Alive: " + b.baseStation_id);
                //System.out.println("]]" + i.hashCode());
                if ((nowTime.getTime() - b.lastUpdate.getTime()) > 3 * c.timeInterval) { //o stathmos den ektelese ImHere se 3t ara prepei na vgei
                    System.out.println("Disconnecting basestation with id: " + b.baseStation_id);
                    removeLock.lock();
                    c.baseStations.remove(b.baseStation_id);  //vgainei apo to hashmap
                    removeLock.unlock();
                    try {
                        Statement techsQuery = c.dbConnection.createStatement();
                        techsQuery.executeUpdate("DELETE FROM BaseStations WHERE baseStation_id='" + b.baseStation_id + "'"); //vgainei kai apo thn mysql db

                        techsQuery.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

            }
            //System.out.println("End of bs in hashmaps.");
        }
    }
}

class MessageThread extends Thread {

    protected CPC c;

    MessageThread(CPC c) {
        this.c = c;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(c.serverMobilePort, 1, null);
        } catch (IOException ex) {
            System.err.println("Could not listen on port " + c.serverMobilePort);
            return;
        }

        Socket clientSocket = null;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                String s = "";
                char x = ' ';
                while ((x = (char) in.read()) != '%') {
                    s += x;
                }
                s += '%';

                MessageHandler msgp = new MessageHandler(c, s, out); //neo thread pou tha epeksergastei to mhnuma
                msgp.start();
            } catch (IOException ex) {
                System.err.println("Accept failed.");

            }

        }
    }
}

class BaseStationShort {
    //Required BaseStation Properties

    protected String baseStation_id;
    protected String network_id;
    protected Double signalStrength;
    protected Integer networkType;
    protected Double frequency;
    protected Double maxBitRate;
    protected Double guaranteedBitRate;
    protected Double net_load;
    protected String provider;
    protected Integer x;
    protected Integer y;
    protected Integer r;
    protected Integer charging;
    //Properties needed for emulation purposes
    protected String ip;
    protected String port;
    //Properties for managing the "3t" rule
    protected Timestamp entranceTime;
    protected Timestamp lastUpdate;

    BaseStationShort(String baseStation_id, String network_id, Double signalStrength, Double frequency, Integer networkType, Double maxBitRate, Double guaranteedBitRate, Double net_load, String provider, String ip, String port, Integer x, Integer y, Integer r, Integer charging) {
        java.util.Date date = new java.util.Date();
        long time = date.getTime();

        this.baseStation_id = baseStation_id;
        this.network_id = network_id;
        this.signalStrength = signalStrength;
        this.networkType = networkType;
        this.frequency = frequency;
        this.maxBitRate = maxBitRate;
        this.guaranteedBitRate = guaranteedBitRate;
        this.net_load = net_load;
        this.provider = provider;
        this.x = x;
        this.y = y;
        this.r = r;
        this.charging = charging;

        this.ip = ip;
        this.port = port;

        this.entranceTime = new Timestamp(time);
        this.lastUpdate = new Timestamp(time);
    }

    protected void UpdateTime() {  //update bs time with current
        java.util.Date date = new java.util.Date();
        lastUpdate.setTime(date.getTime());
    }
}

@WebService
public class CPC {

    protected String dbusername;
    protected String dbpassword;
    protected String dbType;
    protected String dbJavaDriver;
    protected String serverIP;
    protected String serverDbPort;
    protected Integer serverMobilePort;
    protected String dbName;
    protected Integer timeInterval;
    static protected Connection dbConnection;
    //protected Vector<String> technologies;
    protected java.util.concurrent.ConcurrentHashMap<String, BaseStationShort> baseStations; //synchronized hash map
    protected MessageThread messageThread;
    protected Checker checkBaseStationsThread;
    protected Mutex  removeLock;
    
    private String getServerUrl() { //dhmiourgia String tou url tou server apo ta properties
        return dbJavaDriver + ":" + dbType + "://" + serverIP + ":" + serverDbPort + "/" + dbName;
    }

    protected CPC(String channelProperties) {
        baseStations = new java.util.concurrent.ConcurrentHashMap<String, BaseStationShort>();
        removeLock = new Mutex();
        
        Properties prop = new Properties();

        try {
            //Reading from channel property file
            prop.load(new FileInputStream(channelProperties));

            dbusername = prop.getProperty("dbusername");
            dbpassword = prop.getProperty("dbpassword");
            dbType = prop.getProperty("dbType");
            dbJavaDriver = prop.getProperty("dbJavaDriver");
            serverIP = prop.getProperty("serverIP");
            serverDbPort = prop.getProperty("serverDbPort");
            serverMobilePort = new Integer(prop.getProperty("serverMobilePort"));
            dbName = prop.getProperty("dbName");
            timeInterval = new Integer(prop.getProperty("timeInterval"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            Class.forName("com." + dbType + "." + dbJavaDriver + ".Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        System.out.println(getServerUrl()+" "+dbusername+" "+dbpassword);
        try {
            dbConnection = DriverManager.getConnection(getServerUrl(), dbusername, dbpassword);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            Statement techsQuery = dbConnection.createStatement();
            techsQuery.executeUpdate("DROP TABLE IF EXISTS BaseStations"); //diagrafei ton hdh uparxonta pinaka kai apo katw dhmiourgei enan neo
            techsQuery.executeUpdate("CREATE TABLE BaseStations(baseStation_id varchar(45) KEY NOT NULL, network_id varchar(45), signalStrength double, networkType int, frequency double, maxBitRate double, guaranteedBitRate double, net_load double, provider varchar(45),ip varchar(45),port varchar(45), x int, y int, r int, charging int)");

            //debugging
            //techsQuery.executeUpdate("INSERT INTO BaseStations VALUES ('Base3','network',5,3,20,10,5,80,'wind','121.45.23.23','6878',1,2,300,5)");
            //baseStations.put("Base3", new BaseStationShort("BS1", "NET1", new Double(3.5), new Double(1), new Integer(5), new Double(0.5), new Double(10.5), new Double(8.5), "COSMOTE", "9999", "111.111.11.11", new Integer(100), new Integer(0), new Integer(10), new Integer(2)));

            techsQuery.close();
        } catch (SQLException ex) {
            System.err.println("Failed to create BaseStations table");
        }

        messageThread = new MessageThread(this); //mobile message handler
        messageThread.start();

        checkBaseStationsThread = new Checker(this, removeLock); //3t checker
        checkBaseStationsThread.start();
    }

    //akolouthoun oi 3  sunarthseis twn webservices: connect, disconnect, ImHere(pou kanei update)
    public void Connect(String baseStation_id, String network_id, Double signalStrength, Double frequency, Integer networkType, Double maxBitRate, Double guaranteedBitRate, Double net_load, String provider, String ip, String port, Integer x, Integer y, Integer r, Integer charging) {
        BaseStationShort b = new BaseStationShort(baseStation_id, network_id, signalStrength, frequency, networkType, maxBitRate, guaranteedBitRate, net_load, provider, ip, port, x, y, r, charging);

        try {
            Statement techsQuery = dbConnection.createStatement();

            //an uparxei hdh to pairnoume gia na elegksoume ta stats tou
            //gia thn periptwsh pou ekane restart anamesa se 3t diasthma twn elegxwn.
            //techsQuery.executeUpdate("INSERT INTO BaseStations VALUES ('Base3','network',5,3,20,10,5,80,'wind',1,2,300,5)");
            if (baseStations.containsKey(baseStation_id)) { //an uparxei, gia taxuthta diagrafei thn hdh uparxousa eggrafh tou bs kai ton ksanaprosthetei
                System.out.println("Basestation already present (probably restart).");
                baseStations.remove(baseStation_id);
                baseStations.put(baseStation_id, b);
                techsQuery.executeUpdate("UPDATE BaseStations SET network_id='" + network_id + "', signalStrength=" + signalStrength.toString() + ", networkType=" + networkType.toString() + ", frequency=" + frequency.toString() + ", maxBitRate=" + maxBitRate.toString() + ", guaranteedBitRate=" + guaranteedBitRate.toString() + ", net_load=" + net_load.toString() + ", provider='" + provider + "', x=" + x.toString() + ", y=" + y.toString() + ", r=" + r.toString() + ", charging=" + charging.toString() + " WHERE baseStation_id='" + baseStation_id + "'");
            } else { //alliws ton prosthetei
                System.out.println("New basestation arrived.");
                baseStations.put(baseStation_id, b);
                techsQuery.executeUpdate("INSERT INTO BaseStations VALUES ('" + baseStation_id + "','" + network_id + "'," + signalStrength.toString() + "," + networkType.toString() + "," + frequency.toString() + "," + maxBitRate.toString() + "," + guaranteedBitRate.toString() + "," + net_load.toString() + ",'" + provider + "','" + ip + "','" + port + "'," + x.toString() + "," + y.toString() + "," + r.toString() + "," + charging.toString() + ")");
            }
            techsQuery.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void Disconnect(String baseStation_id) {
        baseStations.remove(baseStation_id);
        try {
            Statement techsQuery = dbConnection.createStatement();//diagrafei apo hash map
            techsQuery.executeUpdate("DELETE FROM BaseStations WHERE baseStation_id='" + baseStation_id + "'");//diagrafei apo mysql db

            techsQuery.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println(baseStation_id + " disconnected.");
    }

    /*
    public void ImHere(String baseStation_id) {
        BaseStationShort b = baseStations.get(baseStation_id);
        b.UpdateTime();//refresh time sto hashmap kai sto mysqldb
        System.out.println(baseStation_id + " says:I am here.");
    }*/
}