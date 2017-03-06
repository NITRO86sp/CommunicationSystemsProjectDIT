package anaptyksi2;

import java.util.*;

public class TerminalMessage {

    public String type;
    public String IMEI;
    public Integer lx;
    public Integer ly;
    public Vector availableBaseStations;

    public TerminalMessage(String str) {   //parsing the string

        int i = 0;

        type = "";
        if (str.indexOf("#") != -1) {
            while (str.charAt(i) != '#') { //getting type
                type += str.charAt(i);
                i++;
            }
        } else {
            type = str;
            availableBaseStations = new Vector();
            return;
        }
        
        if (type.equals("DISCOVER")) { //DISCOVER#IMEI#X#Y%

            IMEI = "";
            i++;
            while ((str.charAt(i) != '#') && (str.charAt(i) != '%')) { //getting IMEI
                IMEI += str.charAt(i);
                i++;
            }

            String xs = "";
            i++;
            while (str.charAt(i) != '#') {
                xs += str.charAt(i);
                i++;
            }
            lx = new Integer(Integer.parseInt(xs));

            String ys = "";
            i++;
            while ((i < str.length())&&(str.charAt(i)!='%')) {
                ys += str.charAt(i);
                i++;
            }
            ly = new Integer(Integer.parseInt(ys));
        } else if (type.equals("PROFILES")) {//leitourgia gia to trito meros ths askshs pou metatrepei to String
            //PROFILES#BS1#BS2...% opou BS1=bsatt1$bsatt2$.... se basestations ston vector
            availableBaseStations = new Vector();

            while (i < str.length()) {

                String baseStation_id = "";
                i++;
                while (str.charAt(i) != '$') {
                    baseStation_id += str.charAt(i);
                    i++;
                }

                String network_id = "";
                i++;
                while (str.charAt(i) != '$') {
                    network_id += str.charAt(i);
                    i++;
                }

                String signalStrengthStr = "";
                i++;
                while (str.charAt(i) != '$') {
                    signalStrengthStr += str.charAt(i);
                    i++;
                }
                Double signalStrength = new Double(Double.parseDouble(signalStrengthStr));

                String networkTypeStr = "";
                i++;
                while (str.charAt(i) != '$') {
                    networkTypeStr += str.charAt(i);
                    i++;
                }
                Integer networkType = Integer.valueOf(networkTypeStr);

                String frequencyStr = "";
                i++;
                while (str.charAt(i) != '$') {
                    frequencyStr += str.charAt(i);
                    i++;
                }
                Double frequency = new Double(Double.parseDouble(frequencyStr));

                String maxBitRateStr = "";
                i++;
                while (str.charAt(i) != '$') {
                    maxBitRateStr += str.charAt(i);
                    i++;
                }
                Double maxBitRate = new Double(Double.parseDouble(maxBitRateStr));

                String guaranteedBitRateStr = "";
                i++;
                while (str.charAt(i) != '$') {
                    guaranteedBitRateStr += str.charAt(i);
                    i++;
                }
                Double guaranteedBitRate = new Double(Double.parseDouble(guaranteedBitRateStr));

                String net_loadStr = "";
                i++;
                while (str.charAt(i) != '$') {
                    net_loadStr += str.charAt(i);
                    i++;
                }
                Double net_load = new Double(Double.parseDouble(net_loadStr));

                String provider = "";
                i++;
                while (str.charAt(i) != '$') {
                    provider += str.charAt(i);
                    i++;
                }

                String ip = "";
                i++;
                while (str.charAt(i) != '$') {
                    ip += str.charAt(i);
                    i++;
                }

                String port = "";
                i++;
                while (str.charAt(i) != '$') {
                    port += str.charAt(i);
                    i++;
                }
                
                String xs = "";
                i++;
                while (str.charAt(i) != '$') {
                    xs += str.charAt(i);
                    i++;
                }
                Integer x = new Integer(Integer.parseInt(xs));

                String ys = "";
                i++;
                while (str.charAt(i) != '$') {
                    ys += str.charAt(i);
                    i++;
                }
                Integer y = new Integer(Integer.parseInt(ys));

                String rs = "";
                i++;
                while (str.charAt(i) != '$') {
                    rs += str.charAt(i);
                    i++;
                }
                Integer r = new Integer(Integer.parseInt(rs));
                
                String chargingStr = "";
                i++;
                while (str.charAt(i) != '#') {
                    chargingStr += str.charAt(i);
                    i++;
                    if (i==str.length())
                        break;
                }
                Integer charging = new Integer(Integer.parseInt(chargingStr));
                
                availableBaseStations.addElement(new BaseStationShort(baseStation_id, network_id, signalStrength, frequency, networkType, maxBitRate, guaranteedBitRate, net_load, provider, ip, port, x, y, r, charging));
            }
        }
    }

    public static TerminalMessage fromString(String s) {   //dhmiourgei apo string to mhnuma (ws klash TerminalMessage)
        TerminalMessage msg = new TerminalMessage(s);
        return msg;
    }
}
