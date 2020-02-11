/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in
 * Templates/Classes/Class.java.
 */
@Entity
@Table(name = "property_complaints")
@AttributeOverride(name = "id", column = @Column(name = "property_complaint_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyComplaints.findAll", query = "SELECT p FROM PropertyComplaints p")
    ,
    @NamedQuery(name = "PropertyComplaints.findByPropertyComplaintId", query = "SELECT p FROM PropertyComplaints p WHERE p.id = :propertyComplaintId")
    ,
    @NamedQuery(name = "PropertyComplaints.findByComplaintDate", query = "SELECT p FROM PropertyComplaints p WHERE p.complaintDate = :complaintDate")
    ,
    @NamedQuery(name = "PropertyComplaints.findByPropertyId", query = "SELECT p FROM PropertyComplaints p WHERE p.propertyId = :propertyId")
    ,
    @NamedQuery(name = "PropertyComplaints.findByQuestionaire", query = "SELECT p FROM PropertyComplaints p WHERE p.questionaire = :questionaire")})
public class PropertyComplaints extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Column(name = "complaint_date")
    @Temporal(TemporalType.DATE)
    private Date complaintDate;
    @Size(max = 25)
    @Column(name = "property_id")
    private String propertyId;
    @Size(min = 1, max = 5)
    @Column(name = "questionaire")
    private String questionaire;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "complaint_number")
    private String complaintNumber;
    @Size(max = 150)
    @Column(name = "contact_fullname")
    private String contactFullname;

    @Size(max = 20)
    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;
    @Size(max = 25)
    @Column(name = "contact_designation")
    private String contactDesignation;
    @Size(max = 20)
    @Column(name = "contact_other_number")
    private String contactOtherNumber;
    @Size(max = 45)
    @Column(name = "contact_email_address")
    private String contactEmailAddress;
    @Size(max = 200)
    @Column(name = "complaint_owner_address")
    private String complaintOwnerAddress;
    @Column(name = "property_owner_fname")
    private String propertyOwnerFname;
    @Column(name = "property_owner_lname")
    private String propertyOwnerLname;
    @Column(name = "property_owner_mname")
    private String propertyOwnerMname;
    @Column(name = "property_owner_email")
    private String propertyOwnerEmail;
    @Column(name = "property_owner_phone_no")
    private String propertyOwnerPhoneNo;
    @Column(name = "property_owner_address")
    private String propertyOwnerAddress;
    @Column(name = "property_owner_dob")
    @Temporal(TemporalType.DATE)
    private Date propertyOwnerDob;
    @Column(name = "property_company_name")
    private String propertyCompanyName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyComplaintId")
    private List<ComlaintDocuments> comlaintDocumentsList;
    @JoinColumn(name = "complaint_source_id", referencedColumnName = "complaint_source_id")
    @ManyToOne(optional = false)
    private ComplaintSources complaintSourceId;
    @JoinColumn(name = "ward_land_property_id", referencedColumnName = "ward_land_property_id")
    @ManyToOne()
    private WardLandProperties wardLandPropertyId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyComplaintId")
    private List<ComplaintDetails> complaintDetailsList;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails createdById;

    @JoinColumn(name = "owner_title_id", referencedColumnName = "person_title_id")
    @ManyToOne
    private PersonTitles ownerTitleId;
    @JoinColumn(name = "lcda_ward_id", referencedColumnName = "lcda_ward_id")
    @ManyToOne(optional = false)
    private LcdaWards lcdaWardId;
    @JoinColumn(name = "local_council_dev_area_id", referencedColumnName = "local_council_dev_area_id")
    @ManyToOne(optional = false)
    private LocalCouncilDevArea localCouncilDevAreaId;
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
    @ManyToOne(optional = false)
    private Organizations organization;
    @JoinColumn(name = "relationship_id", referencedColumnName = "relationship_id")
    @ManyToOne(optional = false)
    private ComplainerRelationship ComplainerRelationship;

    public PropertyComplaints() {
    }

    public PropertyComplaints(Long propertyComplaintId) {
        this.id = propertyComplaintId;
    }

    public PropertyComplaints(Long propertyComplaintId, Date complaintDate, String propertyId, String questionaire) {
        this.id = propertyComplaintId;
        this.complaintDate = complaintDate;
        this.propertyId = propertyId;
        this.questionaire = questionaire;
    }

    public Date getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(Date complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getQuestionaire() {
        return questionaire;
    }

    public void setQuestionaire(String questionaire) {
        this.questionaire = questionaire;
    }

    public String getContactFullname() {
        return contactFullname;
    }

    public void setContactFullname(String contactFirstname) {
        this.contactFullname = contactFirstname;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactDesignation() {
        return contactDesignation;
    }

    public void setContactDesignation(String contactDesignation) {
        this.contactDesignation = contactDesignation;
    }

    public String getContactOtherNumber() {
        return contactOtherNumber;
    }

    public void setContactOtherNumber(String contactOtherNumber) {
        this.contactOtherNumber = contactOtherNumber;
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
    }

     
    public List<ComlaintDocuments> getComlaintDocumentsList() {
        return comlaintDocumentsList;
    }

    public void setComlaintDocumentsList(List<ComlaintDocuments> comlaintDocumentsList) {
        this.comlaintDocumentsList = comlaintDocumentsList;
    }

    public ComplaintSources getComplaintSourceId() {
        return complaintSourceId;
    }

    public void setComplaintSourceId(ComplaintSources complaintSourceId) {
        this.complaintSourceId = complaintSourceId;
    }

    public WardLandProperties getWardLandPropertyId() {
        return wardLandPropertyId;
    }

    public void setWardLandPropertyId(WardLandProperties wardLandPropertyId) {
        this.wardLandPropertyId = wardLandPropertyId;
    }

     
    public List<ComplaintDetails> getComplaintDetailsList() {
        return complaintDetailsList;
    }

    public void setComplaintDetailsList(List<ComplaintDetails> complaintDetailsList) {
        this.complaintDetailsList = complaintDetailsList;
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
        if (!(object instanceof PropertyComplaints)) {
            return false;
        }
        PropertyComplaints other = (PropertyComplaints) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyComplaints[ propertyComplaintId=" + id + " ]";
    }

    /**
     * @return the createdById
     */
    public Logindetails getCreatedById() {
        return createdById;
    }

    /**
     * @param createdById the createdById to set
     */
    public void setCreatedById(Logindetails createdById) {
        this.createdById = createdById;
    }

    public String getComplaintOwnerAddress() {
        return complaintOwnerAddress;
    }

    public void setComplaintOwnerAddress(String addressNo) {
        this.complaintOwnerAddress = addressNo;
    }

    public PersonTitles getOwnerTitleId() {
        return ownerTitleId;
    }

    public void setOwnerTitleId(PersonTitles ownerTitleId) {
        this.ownerTitleId = ownerTitleId;
    }

    public String getPropertyOwnerFname() {
        return propertyOwnerFname;
    }

    public void setPropertyOwnerFname(String propertyOwnerFname) {
        this.propertyOwnerFname = propertyOwnerFname;
    }

    public String getPropertyOwnerLname() {
        return propertyOwnerLname;
    }

    public void setPropertyOwnerLname(String propertyOwnerLname) {
        this.propertyOwnerLname = propertyOwnerLname;
    }

    public String getPropertyOwnerMname() {
        return propertyOwnerMname;
    }

    public void setPropertyOwnerMname(String propertyOwnerMname) {
        this.propertyOwnerMname = propertyOwnerMname;
    }

    public String getPropertyOwnerEmail() {
        return propertyOwnerEmail;
    }

    public void setPropertyOwnerEmail(String propertyOwnerEmail) {
        this.propertyOwnerEmail = propertyOwnerEmail;
    }

    public String getPropertyOwnerPhoneNo() {
        return propertyOwnerPhoneNo;
    }

    public void setPropertyOwnerPhoneNo(String propertyOwnerPhoneNo) {
        this.propertyOwnerPhoneNo = propertyOwnerPhoneNo;
    }

    public String getPropertyOwnerAddress() {
        return propertyOwnerAddress;
    }

    public void setPropertyOwnerAddress(String propertyOwnerAddress) {
        this.propertyOwnerAddress = propertyOwnerAddress;
    }

    public Date getPropertyOwnerDob() {
        return propertyOwnerDob;
    }

    public void setPropertyOwnerDob(Date propertyOwnerDob) {
        this.propertyOwnerDob = propertyOwnerDob;
    }

    public String getPropertyCompanyName() {
        return propertyCompanyName;
    }

    public void setPropertyCompanyName(String propertyCompanyName) {
        this.propertyCompanyName = propertyCompanyName;
    }

    public LcdaWards getLcdaWardId() {
        return lcdaWardId;
    }

    public void setLcdaWardId(LcdaWards lcdaWardId) {
        this.lcdaWardId = lcdaWardId;
    }

    public LocalCouncilDevArea getLocalCouncilDevAreaId() {
        return localCouncilDevAreaId;
    }

    public void setLocalCouncilDevAreaId(LocalCouncilDevArea localCouncilDevAreaId) {
        this.localCouncilDevAreaId = localCouncilDevAreaId;
    }

    public Organizations getOrganization() {
        return organization;
    }

    public void setOrganization(Organizations organization) {
        this.organization = organization;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    /**
     * @return the ComplainerRelationship
     */
    public ComplainerRelationship getComplainerRelationship() {
        return ComplainerRelationship;
    }

    /**
     * @param ComplainerRelationship the ComplainerRelationship to set
     */
    public void setComplainerRelationship(ComplainerRelationship ComplainerRelationship) {
        this.ComplainerRelationship = ComplainerRelationship;
    }

}
