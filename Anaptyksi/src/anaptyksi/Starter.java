package anaptyksi;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.DefaultListModel;
/**
 * Tester 3. Validates that all connect messages reach the BaseStation and a proper answer is received
 * Validates disconnect messages.
 */
public class Starter {
    
    public Starter(DefaultListModel l, String IMEI, int port, String ipAddress, int[] coord){ //thread starter

        try {
                Socket connection = new Socket(InetAddress.getByName(ipAddress), port);

                InputStream in = connection.getInputStream();
                OutputStream out = connection.getOutputStream();

                BSMessage msgout = new BSMessage("CONNECT#"+IMEI+"#1#"+coord[0]+"#"+coord[1]+"%");
                out.write( msgout.toString().getBytes() );
                
                l.addElement("[STARTER SENT]:"+msgout.toString());

                String s = "";
                char x = ' ';
                while( (x=(char)in.read())!= '%'){
                    s+=x;
                }
                BSResponse msgin = BSResponse.fromString(s+"%");
                
                l.addElement("[STARTER RECEIVED]:"+s);
                
                in.close();
                out.close();                    

                connection.close();            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void startTerminal(DefaultListModel l, String IMEI, int port, int px, int py){
        int[] x = new int[2];
        x[0] = px;
        x[1] = py;     
        
        new Starter(l ,IMEI, port,"127.0.0.1",x);
    }
    
}
