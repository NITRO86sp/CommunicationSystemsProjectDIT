package anaptyksi;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.DefaultListModel;

public class Stopper {
    
    public Stopper(DefaultListModel l, String IMEI, int port, String ipAddress){ //thread stopper
        try{
            Socket connection = new Socket(InetAddress.getByName(ipAddress), port);

            InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream();

            BSMessage msgout = new BSMessage("DISCONNECT#"+IMEI+"%");
            out.write( msgout.toString().getBytes() );

            l.addElement("[STOPPER SENT]:"+msgout.toString());

            String s = "";
            char x = ' ';
            while( (x=(char)in.read())!= '%'){
                s+=x;
            }
            BSResponse msgin = BSResponse.fromString(s+"%");
 
            l.addElement("[STOPPER RECEIVED]:"+msgin.toString());
            in.close();
            out.close();                    

            connection.close(); 
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }   
    
    public static void stopTerminal(DefaultListModel l, String IMEI, int port){
        new Stopper(l, IMEI, port,"127.0.0.1");
    } 

}
