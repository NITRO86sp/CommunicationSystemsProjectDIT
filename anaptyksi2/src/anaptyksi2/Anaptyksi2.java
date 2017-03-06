package anaptyksi2;

import java.io.*;
import java.util.*;
import javax.xml.ws.Endpoint;

public class Anaptyksi2 {

    public static void main(String[] args) {
        String port, dir, webserviceIP;

        CPC myChannel = new CPC("ChannelProperties.txt");

        Properties prop = new Properties();
        try {
            //Reading from channel property file
            prop.load(new FileInputStream("ServiceProperties.txt"));
            webserviceIP = prop.getProperty("webserviceIP");
            port = prop.getProperty("servicePort");
            dir = prop.getProperty("serviceDir");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;/*fatal error*/ }
        System.out.println("http://" + webserviceIP + ":" + port + "/" + dir);
        Endpoint.publish("http://" + webserviceIP + ":" + port + "/" + dir, myChannel); //publish tou webservice
    }
}
