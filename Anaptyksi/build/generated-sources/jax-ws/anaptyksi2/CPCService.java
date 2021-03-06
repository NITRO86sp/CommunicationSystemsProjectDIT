
package anaptyksi2;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2-hudson-752-
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "CPCService", targetNamespace = "http://anaptyksi2/", wsdlLocation = "http://192.168.1.2:3308/CPCServices?wsdl")
public class CPCService
    extends Service
{

    private final static URL CPCSERVICE_WSDL_LOCATION;
    private final static WebServiceException CPCSERVICE_EXCEPTION;
    private final static QName CPCSERVICE_QNAME = new QName("http://anaptyksi2/", "CPCService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://192.168.1.2:3308/CPCServices?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        CPCSERVICE_WSDL_LOCATION = url;
        CPCSERVICE_EXCEPTION = e;
    }

    public CPCService() {
        super(__getWsdlLocation(), CPCSERVICE_QNAME);
    }

    public CPCService(WebServiceFeature... features) {
        super(__getWsdlLocation(), CPCSERVICE_QNAME, features);
    }

    public CPCService(URL wsdlLocation) {
        super(wsdlLocation, CPCSERVICE_QNAME);
    }

    public CPCService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, CPCSERVICE_QNAME, features);
    }

    public CPCService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CPCService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns CPC
     */
    @WebEndpoint(name = "CPCPort")
    public CPC getCPCPort() {
        return super.getPort(new QName("http://anaptyksi2/", "CPCPort"), CPC.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CPC
     */
    @WebEndpoint(name = "CPCPort")
    public CPC getCPCPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://anaptyksi2/", "CPCPort"), CPC.class, features);
    }

    private static URL __getWsdlLocation() {
        if (CPCSERVICE_EXCEPTION!= null) {
            throw CPCSERVICE_EXCEPTION;
        }
        return CPCSERVICE_WSDL_LOCATION;
    }

}
