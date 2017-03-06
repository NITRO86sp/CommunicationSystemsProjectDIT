
import javax.microedition.io.*;
import java.util.*;
import java.io.*;

public class TerminalActions {

    public String cpcip;
    public String cpcport;
    public String IMEI;
    public String IMSI;
    public String networks;
    public String os;
    public String cpu;
    public String ram;
    private Boolean receiving;
    private String name;
    private String surname;
    private String address;
    private String chargingType;
    private String favouriteServices;
    private Integer X;
    private Integer Y;

    private String[] split(String original, String separator) { //sunarthsh pou moirazei to string me vash ton separator
        Vector nodes = new Vector();
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index - 1));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }

        nodes.addElement(original);

        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }

        }

        return result;
    }

    public TerminalActions() { //function pou fortwnei ta stoixeia suskevhs apo to properties

        InputStream is = getClass().getResourceAsStream("Properties.txt");
        String inpstr = new String();
        try {
            int chars = 0;
            while ((chars = is.read()) != -1) {
                inpstr += (char) chars;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] tokens = split(inpstr, "\n");

        if (tokens.length != 9) {
            for (int i = 0; i < tokens.length; i++) {
                System.out.println(tokens[i]);
            }
            System.out.println(tokens.length + " arguments found.");
            System.out.println("Wrong arguments in Properties.txt!");
            System.exit(-1);
        }

        Random rn = new Random();

        X = new Integer(/*
                 * rn.nextInt() % 100
                 */1);
        Y = new Integer(/*
                 * rn.nextInt() % 100
                 */1);

        IMEI = tokens[0];
        IMSI = tokens[1];
        networks = tokens[2];
        os = tokens[3];
        cpu = tokens[4];
        ram = tokens[5];
        receiving = new Boolean(tokens[6].equals("TRUE"));
        cpcip = tokens[7];
        cpcport = tokens[8];
    }

    public Vector Discover() {//function pou epistrefei vector me ola ta bs pou phre apo to CPC
        TerminalMessage msg;
        
        /* debugging
        cpcip = "127.0.0.1";
        cpcport = "3307";
        */
        try {
            System.out.println("socket://" + cpcip + ":" + cpcport);
            StreamConnection sc = (SocketConnection) Connector.open("socket://" + cpcip + ":" + cpcport);

            OutputStream os = sc.openOutputStream();

            byte[] message = ("DISCOVER#" + IMEI + "#" + X.toString() + "#" + Y.toString() + "%").getBytes();
            os.write(message);

            InputStream is = sc.openInputStream();
            char x;
            String inStr = "";
            while ((x = (char) is.read()) != '%') {
                inStr += x;
            }

            System.out.println("inStr=" + inStr);
            msg = TerminalMessage.fromString(inStr);
            System.out.println("Discover Completed");
            for (int i = 0; i < msg.availableBaseStations.size(); i++) {
                System.out.println(((BaseStationShort) (msg.availableBaseStations.elementAt(i))).baseStation_id);
            }

            sc.close();
            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //msg = TerminalMessage.fromString("PROFILES#Base1$Network1$1$2$1$1000$200$0.7$wind$127.0.0.1$3307$1$1$10$1%");

        return msg.availableBaseStations;
    }

    public boolean ConnectTo(BaseStationShort bs) { //function sundeshs me bs
        boolean result = false;

        try {
            System.out.println("sdf----" + "socket://" + bs.ip + ":" + bs.port); //debugging
            StreamConnection sc = (SocketConnection) Connector.open("socket://" + bs.ip + ":" + bs.port);

            OutputStream os = sc.openOutputStream();

            byte[] message = ("CONNECT#" + IMEI + "#" + IMSI + "#" + X.toString() + "#" + Y.toString() + "%").getBytes();
            os.write(message);


            InputStream is = sc.openInputStream();
            char x;
            String inStr = "";
            while ((x = (char) is.read()) != '%') {
                inStr += x;
            }

            System.out.println("BSResponded " + inStr);
            result = (inStr.equals("OK#" + IMEI));

            sc.close();
            is.close();
            os.close();
        } catch (IOException e) {
            return false;
        }

        return result;
    }

    public boolean DisconnectFrom(BaseStationShort bs) { //function pou aposundeei to kinhto apo to bs
        boolean result = false;

        try {
            StreamConnection sc = (SocketConnection) Connector.open("socket://" + bs.ip + ":" + bs.port);

            OutputStream os = sc.openOutputStream();

            byte[] message = ("DISCONNECT#" + IMEI + "%").getBytes();
            os.write(message);


            InputStream is = sc.openInputStream();
            char x;
            String inStr = "";
            while ((x = (char) is.read()) != '%') {
                inStr += x;
            }

            result = (inStr.equals("OK#" + IMEI));

            sc.close();
            is.close();
            os.close();

        } catch (IOException e) {
            return false;
        }

        return result;
    }
}
