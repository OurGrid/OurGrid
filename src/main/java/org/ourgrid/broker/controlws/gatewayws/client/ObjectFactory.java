
package org.ourgrid.broker.controlws.gatewayws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.ourgrid.broker.controlws.gatewayws.client package. 
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

    private final static QName _GetStatus_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "getStatus");
    private final static QName _CancelJob_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "cancelJob");
    private final static QName _GetStatusResponse_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "getStatusResponse");
    private final static QName _CancelJobResponse_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "cancelJobResponse");
    private final static QName _SubmitJobResponse_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "submitJobResponse");
    private final static QName _CleanJob_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "cleanJob");
    private final static QName _SubmitJob_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "submitJob");
    private final static QName _CleanJobResponse_QNAME = new QName("http://gatewayws.controlws.broker.ourgrid.org/", "cleanJobResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.ourgrid.broker.controlws.gatewayws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CleanJob }
     * 
     */
    public CleanJob createCleanJob() {
        return new CleanJob();
    }

    /**
     * Create an instance of {@link CancelJobResponse }
     * 
     */
    public CancelJobResponse createCancelJobResponse() {
        return new CancelJobResponse();
    }

    /**
     * Create an instance of {@link CancelJob }
     * 
     */
    public CancelJob createCancelJob() {
        return new CancelJob();
    }

    /**
     * Create an instance of {@link SubmitJobResponse }
     * 
     */
    public SubmitJobResponse createSubmitJobResponse() {
        return new SubmitJobResponse();
    }

    /**
     * Create an instance of {@link CleanJobResponse }
     * 
     */
    public CleanJobResponse createCleanJobResponse() {
        return new CleanJobResponse();
    }

    /**
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link WsTaskSpec }
     * 
     */
    public WsTaskSpec createWsTaskSpec() {
        return new WsTaskSpec();
    }

    /**
     * Create an instance of {@link WsioEntry }
     * 
     */
    public WsioEntry createWsioEntry() {
        return new WsioEntry();
    }

    /**
     * Create an instance of {@link WsJobSpec }
     * 
     */
    public WsJobSpec createWsJobSpec() {
        return new WsJobSpec();
    }

    /**
     * Create an instance of {@link SubmitJob }
     * 
     */
    public SubmitJob createSubmitJob() {
        return new SubmitJob();
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "getStatus")
    public JAXBElement<GetStatus> createGetStatus(GetStatus value) {
        return new JAXBElement<GetStatus>(_GetStatus_QNAME, GetStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "cancelJob")
    public JAXBElement<CancelJob> createCancelJob(CancelJob value) {
        return new JAXBElement<CancelJob>(_CancelJob_QNAME, CancelJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "getStatusResponse")
    public JAXBElement<GetStatusResponse> createGetStatusResponse(GetStatusResponse value) {
        return new JAXBElement<GetStatusResponse>(_GetStatusResponse_QNAME, GetStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "cancelJobResponse")
    public JAXBElement<CancelJobResponse> createCancelJobResponse(CancelJobResponse value) {
        return new JAXBElement<CancelJobResponse>(_CancelJobResponse_QNAME, CancelJobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "submitJobResponse")
    public JAXBElement<SubmitJobResponse> createSubmitJobResponse(SubmitJobResponse value) {
        return new JAXBElement<SubmitJobResponse>(_SubmitJobResponse_QNAME, SubmitJobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CleanJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "cleanJob")
    public JAXBElement<CleanJob> createCleanJob(CleanJob value) {
        return new JAXBElement<CleanJob>(_CleanJob_QNAME, CleanJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "submitJob")
    public JAXBElement<SubmitJob> createSubmitJob(SubmitJob value) {
        return new JAXBElement<SubmitJob>(_SubmitJob_QNAME, SubmitJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CleanJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gatewayws.controlws.broker.ourgrid.org/", name = "cleanJobResponse")
    public JAXBElement<CleanJobResponse> createCleanJobResponse(CleanJobResponse value) {
        return new JAXBElement<CleanJobResponse>(_CleanJobResponse_QNAME, CleanJobResponse.class, null, value);
    }

}
