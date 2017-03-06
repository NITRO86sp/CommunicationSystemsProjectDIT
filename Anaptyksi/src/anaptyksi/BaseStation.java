package anaptyksi;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import sun.awt.Mutex;

enum NetworkType {

    GSM, UMTS, WLAN, WIMAX
}

enum ChargeType {

    FIXED, METERED, PACKET, EXPECTED, EDGE, PARIS, AUCTION
}

class WebServicesHandler extends Thread {
    //to be continued...
}

class TerminalHandler extends Thread {

    private boolean toStop = false; //used to terminate the thread we created
    Socket clientSocket = null;
    String IMEI;     //is considered a unique identifier for every terminal
    Vector<BSMessage> messages;

    public TerminalHandler(Socket cS, String tIMEI) {
        clientSocket = cS;
        IMEI = tIMEI;
        messages = new Vector<BSMessage>();
    }

    synchronized public boolean mustStop() { //epistrefei an prepei na kleisei h diergasia h oxi
        return toStop;
    }

    synchronized public void setStop() { //vazei flag kleisimou diergasias se true
        toStop = true;
    }

    synchronized public void syncAppend(BSMessage x) { //safely appends new message to the messages vector
        messages.add(x);
    }

    synchronized public BSMessage syncPop() { //epistrefei to prwto mhnuma ston queue vector kai to diagrafei apo thn oura.
        BSMessage result = messages.elementAt(0);
        messages.remove(0);
        return result;
    }

    @Override
    public void run() {          //thread's operation
        while (!mustStop()) {
            try {
                sleep(100);
                while (messages.size() > 0) {
                    BSMessage mes = syncPop();
                    //process messages other than connect and disconnect / for future use!!!

                }
            } catch (InterruptedException e) {
            }
        }

    }
}

class ConnectToCPC extends Thread {

    private BaseStation bStation;

    public ConnectToCPC(BaseStation bStation) {
        this.bStation = bStation;
    }

    @Override
    public void run() {
        while (true) {
            try{
                bStation.connectToCPC();
                sleep(bStation.timeInterval);
            }catch (InterruptedException ex) {}
        }
    }
}

class ProccesTerminalMessage extends Thread {

    private BaseStation bStation;
    private Vector<TerminalHandler> v;
    private Mutex vlock;
    private Socket clientSocket;
    private InputStream in;
    private OutputStream out;

    public ProccesTerminalMessage(BaseStation bStation, Vector<TerminalHandler> v, Mutex vlock, Socket clientSocket) {
        this.vlock = vlock;
        this.bStation = bStation;
        this.v = v;
        this.clientSocket = clientSocket;
    }

    int sqr(int x) {//tetragwno akeraiou arithmou
        return x * x;
    }

    boolean AcceptDistance(Coords a) {
        return (sqr(bStation.coverRange) > (sqr(bStation.position.x - a.x) + sqr(bStation.position.y - a.y)));
    }

    @Override
    public void run() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();

            String s = "";
            char x = ' ';
            while ((x = (char) in.read()) != '%') {
                s += x;
            }
            BSMessage msg = BSMessage.fromString(s + '%');

            if (msg.type.equals("CONNECT")) {
                Coords p = new Coords();
                p.x = msg.x;
                p.y = msg.y;

                if (bStation.loadP >= 90) {
                    out.write(("OVERLOADED#" + msg.IMEI + "%").getBytes());

                } else if (!AcceptDistance(p)) {
                    out.write(("NO_COVERAGE#" + msg.IMEI + "%").getBytes());
                } else {
                    bStation.loadP += 5;
                    bStation.prop.setProperty("loadP", bStation.loadP.toString());
                    //bStation.prop.store(new FileOutputStream(bStation.fileName), bStation.loadP.toString());
                    TerminalHandler thandler = new TerminalHandler(clientSocket, msg.IMEI); //giati pairnei socket edw?
                    vlock.lock();
                    v.add(thandler);
                    vlock.unlock();
                    //refreshload();//bf.loadind.setText(Integer.toString(bStation.loadP)); //updates load
                    thandler.start();
                    BSResponse r = BSResponse.fromString("OK#" + msg.IMEI + "%");
                    out.write(r.toString().getBytes());
                }

            } else {
                boolean found = false;
                int i;
                vlock.lock();
                for (i = 0; i < v.size(); i++) {
                    if ((v.get(i)).IMEI.equals(msg.IMEI)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    if (msg.type.equals("DISCONNECT")) {
                        v.get(i).setStop();
                        v.remove(i);
                        vlock.unlock();
                        BSResponse r = BSResponse.fromString("OK#" + msg.IMEI + "%");
                        out.write(r.toString().getBytes());
                        bStation.loadP -= 5;
                        bStation.prop.setProperty("loadP", bStation.loadP.toString());
                        //bStation.prop.store(new FileOutputStream(bStation.fileName), bStation.loadP.toString());
                        //command to save loadP to fileName for beta testing
                    } else {
                        v.get(i).syncAppend(msg);
                        vlock.unlock();
                    }
                } else {
                    vlock.unlock();
                    BSResponse r = BSResponse.fromString("INVALID_IMEI#" + msg.IMEI + "%");
                    out.write(r.toString().getBytes());
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ex) {
        }
    }
}

class BSServer extends Thread {  //kentrikh klash pou periexei anafores stathmwn tous se vectors

    BaseStation bStation;
    Vector<TerminalHandler> v;
    private Mutex vlock;

    public BSServer(BaseStation bs) {
        bStation = bs;
        v = new Vector<TerminalHandler>();
        vlock = new Mutex();
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(bStation.port, 1, null);
        } catch (IOException ex) {
            System.err.println("Could not listen on port " + bStation.port);
            return;
        }

        Socket clientSocket = null;
        int counter = 0;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                //communicating with the clients we just created

                ProccesTerminalMessage p = new ProccesTerminalMessage(bStation, v, vlock, clientSocket);
                p.start();
            } catch (IOException ex) {
                System.err.println("Accept failed.");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Can not close server port.");
                    System.exit(1);
                }

                //System.exit(1);
            }

        }
    }
}

public class BaseStation {

    public Radiotech R;
    public String ip;
    public Integer loadP;
    public Integer coverRange;
    public Integer timeInterval;
    public Coords position;
    public Integer port;
    public BSServer server;
    public WebServicesHandler Web;
    public Properties prop;
    public String fileName;
    public ConnectToCPC connector;
    //Methods

    private NetworkType StrToNetworkType(String s) {
        if (s.equals("GSM")) {
            return NetworkType.GSM;
        } else if (s.equals("UMTS")) {
            return NetworkType.UMTS;
        } else if (s.equals("WLAN")) {
            return NetworkType.WLAN;
        } else {
            return NetworkType.WIMAX;
        }
    }

    private ChargeType StrToChargeType(String s) {
        if (s.equals("FIXED")) {
            return ChargeType.FIXED;
        } else if (s.equals("METERED")) {
            return ChargeType.METERED;
        } else if (s.equals("PACKET")) {
            return ChargeType.PACKET;
        } else if (s.equals("PARIS")) {
            return ChargeType.PARIS;
        } else if (s.equals("EDGE")) {
            return ChargeType.EDGE;
        } else {
            return ChargeType.AUCTION;
        }
    }

    //String baseStation_id, String network_id, Double signalStrength, Double frequency, Integer networkType, Double maxBitRate, 
    //Double guaranteedBitRate, Double net_load, String provider, String ip, String port, Integer x, Integer y, Integer r, Integer charging
    public void connectToCPC() {
        connect(R.basestationId, R.networkId, new Double(R.signalPower.doubleValue()), new Double(R.frequency.doubleValue()), new Integer(R.netType.ordinal()), new Double(R.maxBitrate.doubleValue()), new Double(R.guarBitrate) , new Double(loadP.doubleValue()), R.provider, ip,port.toString(), position.x, position.y, coverRange, R.cType.ordinal());
    }

    public void disconnectFromCPC(){
        disconnect(R.basestationId);
    }
    
    public BaseStation(String fName) {
        prop = new Properties();
        fileName = fName;
        try {
            //Reading members of BaseStation
            prop.load(new FileInputStream(fileName));
            loadP = new Integer(0);
            prop.setProperty("loadP", "0");
            timeInterval = new Integer(prop.getProperty("timeInterval"));
            ip = prop.getProperty("ip");
            coverRange = new Integer(prop.getProperty("coverRange"));
            position = new Coords();
            position.x = Integer.parseInt(prop.getProperty("x"));
            position.y = Integer.parseInt(prop.getProperty("y"));
            port = new Integer(prop.getProperty("port"));
            //Reading members of RadioTech
            R = new Radiotech();
            R.networkId = prop.getProperty("networdId");
            R.basestationId = prop.getProperty("basestationId");
            R.signalPower = new Integer(prop.getProperty("signalPower"));
            R.netType = StrToNetworkType(prop.getProperty("netType"));
            R.frequency = new Float(prop.getProperty("frequency"));
            R.maxBitrate = new Integer(prop.getProperty("maxBitrate"));
            R.guarBitrate = new Integer(prop.getProperty("guarBitrate"));
            R.provider = prop.getProperty("provider");
            R.cType = StrToChargeType(prop.getProperty("cType"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        connector = new ConnectToCPC(this);
        connector.start();
        server = new BSServer(this);  //creation of the thread that listens to the port -- it must be started in an outer level
        Web = new WebServicesHandler();
        Web.start(); //an empty thread that will be handling the information exchanges between BaseStation and Information Channel
    }

    public static void main(String[] args) {
        BaseStation B = new BaseStation("Base1.txt");
        BsFrame bsWindow = new BsFrame(B);
        bsWindow.setVisible(true);
        bsWindow.setResizable(false);
        B.server.start();
    }

    private static void connect(java.lang.String arg0, java.lang.String arg1, java.lang.Double arg2, java.lang.Double arg3, java.lang.Integer arg4, java.lang.Double arg5, java.lang.Double arg6, java.lang.Double arg7, java.lang.String arg8, java.lang.String arg9, java.lang.String arg10, java.lang.Integer arg11, java.lang.Integer arg12, java.lang.Integer arg13, java.lang.Integer arg14) {
        anaptyksi2.CPCService service = new anaptyksi2.CPCService();
        anaptyksi2.CPC port = service.getCPCPort();
        port.connect(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
    }

    private static void disconnect(java.lang.String arg0) {
        anaptyksi2.CPCService service = new anaptyksi2.CPCService();
        anaptyksi2.CPC port = service.getCPCPort();
        port.disconnect(arg0);
    }
}
