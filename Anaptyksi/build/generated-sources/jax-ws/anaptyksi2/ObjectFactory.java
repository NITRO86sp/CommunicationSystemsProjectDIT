
package anaptyksi2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the anaptyksi2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DisconnectResponse_QNAME = new QName("http://anaptyksi2/", "DisconnectResponse");
    private final static QName _Connect_QNAME = new QName("http://anaptyksi2/", "Connect");
    private final static QName _ConnectResponse_QNAME = new QName("http://anaptyksi2/", "ConnectResponse");
    private final static QName _ImHere_QNAME = new QName("http://anaptyksi2/", "ImHere");
    private final static QName _Disconnect_QNAME = new QName("http://anaptyksi2/", "Disconnect");
    private final static QName _ImHereResponse_QNAME = new QName("http://anaptyksi2/", "ImHereResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: anaptyksi2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConnectResponse }
     * 
     */
    public ConnectResponse createConnectResponse() {
        return new ConnectResponse();
    }

    /**
     * Create an instance of {@link ImHereResponse }
     * 
     */
    public ImHereResponse createImHereResponse() {
        return new ImHereResponse();
    }

    /**
     * Create an instance of {@link Disconnect }
     * 
     */
    public Disconnect createDisconnect() {
        return new Disconnect();
    }

    /**
     * Create an instance of {@link ImHere }
     * 
     */
    public ImHere createImHere() {
        return new ImHere();
    }

    /**
     * Create an instance of {@link DisconnectResponse }
     * 
     */
    public DisconnectResponse createDisconnectResponse() {
        return new DisconnectResponse();
    }

    /**
     * Create an instance of {@link Connect }
     * 
     */
    public Connect createConnect() {
        return new Connect();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DisconnectResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://anaptyksi2/", name = "DisconnectResponse")
    public JAXBElement<DisconnectResponse> createDisconnectResponse(DisconnectResponse value) {
        return new JAXBElement<DisconnectResponse>(_DisconnectResponse_QNAME, DisconnectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Connect }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://anaptyksi2/", name = "Connect")
    public JAXBElement<Connect> createConnect(Connect value) {
        return new JAXBElement<Connect>(_Connect_QNAME, Connect.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConnectResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://anaptyksi2/", name = "ConnectResponse")
    public JAXBElement<ConnectResponse> createConnectResponse(ConnectResponse value) {
        return new JAXBElement<ConnectResponse>(_ConnectResponse_QNAME, ConnectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImHere }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://anaptyksi2/", name = "ImHere")
    public JAXBElement<ImHere> createImHere(ImHere value) {
        return new JAXBElement<ImHere>(_ImHere_QNAME, ImHere.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Disconnect }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://anaptyksi2/", name = "Disconnect")
    public JAXBElement<Disconnect> createDisconnect(Disconnect value) {
        return new JAXBElement<Disconnect>(_Disconnect_QNAME, Disconnect.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImHereResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://anaptyksi2/", name = "ImHereResponse")
    public JAXBElement<ImHereResponse> createImHereResponse(ImHereResponse value) {
        return new JAXBElement<ImHereResponse>(_ImHereResponse_QNAME, ImHereResponse.class, null, value);
    }

}
