package anaptyksi2;

import java.io.*;
import java.util.*;

class MessageHandler extends Thread { // h klash pou diaxeirizetai to socket thread kai dhmiourgei threads gia na apanthsei sta kinhta

    protected CPC c;
    protected String msg;
    OutputStream out;

    int sqr(int x) {//tetragwno akeraiou arithmou
        return x * x;
    }

    boolean AcceptDistance(Integer x1, Integer y1, Integer x2, Integer y2, Integer coverRange) { //epistrefei true an vrisketai entos emveleias ena kinhto
        return (sqr(coverRange) >= (sqr(x2 - x1) + sqr(y2 - y1)));
    }

    MessageHandler(CPC c, String msg, OutputStream out) {
        this.c = c;
        this.msg = msg;
        this.out = out;
    }

    @Override
    public void run() {//thread to opoio an dexthke message type DISCOVER apantaei me PROFILES kai bs in range
        TerminalMessage message = TerminalMessage.fromString(msg);
        System.out.println("Received message type " + message.type);
        if (message.type.equals("DISCOVER")) {
            String responseStr = "PROFILES";
            // debugging purposes
            /*
            c.baseStations.put("BS1", new BaseStationShort("BS1", "NET1", new Double(3.5), new Double(1), new Integer(5), new Double(0.5), new Double(10.5), new Double(8.5), "COSMOTE", "9999", "111.111.11.11", new Integer(100), new Integer(0), new Integer(10), new Integer(2)));
            c.baseStations.put("BS2", new BaseStationShort("BS2", "NET2", new Double(3.5), new Double(1), new Integer(5), new Double(0.5), new Double(10.5), new Double(8.5), "vo", "9999", "111.111.11.11", new Integer(0), new Integer(0), new Integer(100), new Integer(2)));
            c.baseStations.put("BS3", new BaseStationShort("BS3", "NET3", new Double(3.5), new Double(1), new Integer(5), new Double(0.5), new Double(10.5), new Double(8.5), "wi", "9999", "111.111.11.11", new Integer(0), new Integer(0), new Integer(100), new Integer(2)));
             */
            Set set = c.baseStations.entrySet();

            Iterator i = set.iterator();
            BaseStationShort bi; //aplh anafora
            while (i.hasNext()) { //pairnei to vector availableBaseStations ths TerminalMessage kai ftiaxnei gia apostolh to PROFILES
                bi = (BaseStationShort) ((Map.Entry) i.next()).getValue();
                if (AcceptDistance(bi.x, bi.y, message.lx, message.ly, bi.r)) { //an vrisketai entos emveleias prostithetai sto mhnuma PROFILES
                    responseStr += "#" + bi.baseStation_id + "$" + bi.network_id + "$" + bi.signalStrength.toString() + "$" + bi.networkType.toString() + "$" + bi.frequency.toString() + "$" + bi.maxBitRate.toString() + "$" + bi.guaranteedBitRate.toString() + "$" + bi.net_load.toString() + "$" + bi.provider + "$" + bi.ip + "$" + bi.port + "$" + bi.x.toString() + "$" + bi.y.toString() + "$" + bi.r.toString() + "$" + bi.charging.toString();
                }
            }
            responseStr += "%";

            try {
                out.write(responseStr.getBytes());

            } catch (IOException ex) {
                System.err.println("Error sending message with type " + message.type + " to terminal with IMEI " + message.IMEI);
            }

        } else {
            System.out.print("Mobile sent unrecognisable message type:" + message.type);
        }
    }
}