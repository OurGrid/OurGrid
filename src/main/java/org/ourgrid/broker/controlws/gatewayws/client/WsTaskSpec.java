
package org.ourgrid.broker.controlws.gatewayws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsTaskSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsTaskSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="finalBlock" type="{http://gatewayws.controlws.broker.ourgrid.org/}wsioEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="initBlock" type="{http://gatewayws.controlws.broker.ourgrid.org/}wsioEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="remoteExec" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sabotageCheck" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceParentDir" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taskSequenceNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsTaskSpec", propOrder = {
    "finalBlock",
    "initBlock",
    "remoteExec",
    "sabotageCheck",
    "sourceParentDir",
    "taskSequenceNumber"
})
public class WsTaskSpec {

    @XmlElement(nillable = true)
    protected List<WsioEntry> finalBlock;
    @XmlElement(nillable = true)
    protected List<WsioEntry> initBlock;
    protected String remoteExec;
    protected String sabotageCheck;
    protected String sourceParentDir;
    protected int taskSequenceNumber;

    /**
     * Gets the value of the finalBlock property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the finalBlock property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFinalBlock().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WsioEntry }
     * 
     * 
     */
    public List<WsioEntry> getFinalBlock() {
        if (finalBlock == null) {
            finalBlock = new ArrayList<WsioEntry>();
        }
        return this.finalBlock;
    }

    /**
     * Gets the value of the initBlock property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the initBlock property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInitBlock().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WsioEntry }
     * 
     * 
     */
    public List<WsioEntry> getInitBlock() {
        if (initBlock == null) {
            initBlock = new ArrayList<WsioEntry>();
        }
        return this.initBlock;
    }

    /**
     * Gets the value of the remoteExec property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteExec() {
        return remoteExec;
    }

    /**
     * Sets the value of the remoteExec property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteExec(String value) {
        this.remoteExec = value;
    }

    /**
     * Gets the value of the sabotageCheck property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSabotageCheck() {
        return sabotageCheck;
    }

    /**
     * Sets the value of the sabotageCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSabotageCheck(String value) {
        this.sabotageCheck = value;
    }

    /**
     * Gets the value of the sourceParentDir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceParentDir() {
        return sourceParentDir;
    }

    /**
     * Sets the value of the sourceParentDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceParentDir(String value) {
        this.sourceParentDir = value;
    }

    /**
     * Gets the value of the taskSequenceNumber property.
     * 
     */
    public int getTaskSequenceNumber() {
        return taskSequenceNumber;
    }

    /**
     * Sets the value of the taskSequenceNumber property.
     * 
     */
    public void setTaskSequenceNumber(int value) {
        this.taskSequenceNumber = value;
    }

}
