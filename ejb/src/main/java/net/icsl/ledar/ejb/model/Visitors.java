/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 * @date Mar 30, 2017
 */
@Entity
@Table(name = "visitors")
@AttributeOverride(name = "id", column = @Column(name = "visitor_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Visitors.findAll", query = "SELECT v FROM Visitors v")
    , @NamedQuery(name = "Visitors.findByVisitorId", query = "SELECT v FROM Visitors v WHERE v.id = :visitorId")
    , @NamedQuery(name = "Visitors.findByFirstName", query = "SELECT v FROM Visitors v WHERE v.firstName = :firstName")
    , @NamedQuery(name = "Visitors.findByLastName", query = "SELECT v FROM Visitors v WHERE v.lastName = :lastName")
    , @NamedQuery(name = "Visitors.findByEmailAddress", query = "SELECT v FROM Visitors v WHERE v.emailAddress = :emailAddress")})
public class Visitors extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "property_id")
    private String propertyId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 150)
    @Column(name = "email_address")
    private String emailAddress;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "property_number")
    private String propertyNumber;
    @Size(max = 200)
    @Column(name = "property_address")
    private String propertyAddress;
    @Column(name = "time_in")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeIn;
    @Column(name = "time_out")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOut;

    @JoinColumn(name = "receptionist_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails receptionistId;
    @JoinColumn(name = "visit_purpose_id", referencedColumnName = "visit_purpose_id")
    @ManyToOne(optional = false)
    private VisitPurpose visitPurposeId;
    @JoinColumn(name = "consultant_id", referencedColumnName = "organization_id")
    @ManyToOne(optional = false)
    private Organizations consultant;
    @JoinColumn(name = "lcda_ward_id", referencedColumnName = "lcda_ward_id")
    @ManyToOne(optional = false)
    private LcdaWards lcdaWardId;
    @Column(name = "owner_last_name")
    private String ownerLastName;
    @Column(name = "owner_first_name")
    private String ownerFirstName;
    @Column(name = "owner_phone_number")
    private String ownerPhoneNumber;
    @Column(name = "owner_email_address")
    private String ownerEmailAddress;
    @Column(name = "owner_dob")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ownerDob;
    @JoinColumn(name = "relationship_id", referencedColumnName = "relationship_id")
    @ManyToOne(optional = false)
    private ComplainerRelationship complainerRelationship;

    public Visitors() {
    }

    public Visitors(Long visitorId) {
        this.id = visitorId;
    }

    public Visitors(Long visitorId, String firstName, String lastName, String propertyNumber, Date created_) {
        this.id = visitorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.propertyNumber = propertyNumber;
        this.created = created_;
    }

    public Visitors(Long visitor_id, String firstName, String propertyId, String lastName, String emailAddress, String propertyNumber, String propertyAddress, Date timeIn, Date timeOut,
            Logindetails receptionistId, VisitPurpose visitPurposeId, Organizations consultant, LcdaWards lcdaWardId, String ownerLastName, String ownerFirstName, String ownerPhoneNumber,
            String ownerEmailAddress, Date ownerDob, ComplainerRelationship complainerRelationship, Date created_, Date modified_) {
        this.firstName = firstName;
        this.propertyId = propertyId;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.propertyNumber = propertyNumber;
        this.propertyAddress = propertyAddress;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.receptionistId = receptionistId;
        this.visitPurposeId = visitPurposeId;
        this.consultant = consultant;
        this.lcdaWardId = lcdaWardId;
        this.ownerLastName = ownerLastName;
        this.ownerFirstName = ownerFirstName;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.ownerEmailAddress = ownerEmailAddress;
        this.ownerDob = ownerDob;
        this.complainerRelationship = complainerRelationship;
        this.id = visitor_id;
        this.created = created_;
        this.modified = modified_;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPropertyNumber() {
        return propertyNumber;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Logindetails getReceptionistId() {
        return receptionistId;
    }

    public void setReceptionistId(Logindetails receptionistId) {
        this.receptionistId = receptionistId;
    }

    public VisitPurpose getVisitPurposeId() {
        return visitPurposeId;
    }

    public void setVisitPurposeId(VisitPurpose visitPurposeId) {
        this.visitPurposeId = visitPurposeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Visitors)) {
            return false;
        }
        Visitors other = (Visitors) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Visitors[ id=" + id + " ]";
    }

    /**
     * @return the consultant
     */
    public Organizations getConsultant() {
        return consultant;
    }

    /**
     * @param consultant the consultant to set
     */
    public void setConsultant(Organizations consultant) {
        this.consultant = consultant;
    }

    /**
     * @return the lcdaWardId
     */
    public LcdaWards getLcdaWardId() {
        return lcdaWardId;
    }

    /**
     * @param lcdaWardId the lcdaWardId to set
     */
    public void setLcdaWardId(LcdaWards lcdaWardId) {
        this.lcdaWardId = lcdaWardId;
    }

    /**
     * @return the propertyId
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * @param propertyId the propertyId to set
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * @return the ownerLastName
     */
    public String getOwnerLastName() {
        return ownerLastName;
    }

    /**
     * @param ownerLastName the ownerLastName to set
     */
    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    /**
     * @return the ownerFirstName
     */
    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    /**
     * @param ownerFirstName the ownerFirstName to set
     */
    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    /**
     * @return the ownerPhoneNumber
     */
    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    /**
     * @param ownerPhoneNumber the ownerPhoneNumber to set
     */
    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    /**
     * @return the ownerEmailAddress
     */
    public String getOwnerEmailAddress() {
        return ownerEmailAddress;
    }

    /**
     * @param ownerEmailAddress the ownerEmailAddress to set
     */
    public void setOwnerEmailAddress(String ownerEmailAddress) {
        this.ownerEmailAddress = ownerEmailAddress;
    }

    /**
     * @return the ownerDob
     */
    public Date getOwnerDob() {
        return ownerDob;
    }

    /**
     * @param ownerDob the ownerDob to set
     */
    public void setOwnerDob(Date ownerDob) {
        this.ownerDob = ownerDob;
    }

    /**
     * @return the ComplainerRelationship
     */
    public ComplainerRelationship getComplainerRelationship() {
        return complainerRelationship;
    }

    /**
     * @param ComplainerRelationship the ComplainerRelationship to set
     */
    public void setComplainerRelationship(ComplainerRelationship complainerRelationship) {
        this.complainerRelationship = complainerRelationship;
    }

}
