package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 
import org.hibernate.annotations.Formula;

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "people")
@AttributeOverride(name = "id", column = @Column(name = "person_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "People.findAll", query = "SELECT p FROM People p"),
    @NamedQuery(name = "People.findByPersonId", query = "SELECT p FROM People p WHERE p.id = :personId"),
    @NamedQuery(name = "People.findByLastName", query = "SELECT p FROM People p WHERE p.lastName = :lastName"),
    @NamedQuery(name = "People.findByFirstName", query = "SELECT p FROM People p WHERE p.firstName = :firstName"),
    @NamedQuery(name = "People.findByMiddleName", query = "SELECT p FROM People p WHERE p.middleName = :middleName"),
    @NamedQuery(name = "People.findByDob", query = "SELECT p FROM People p WHERE p.dob = :dob"),
    @NamedQuery(name = "People.findByMaidenName", query = "SELECT p FROM People p WHERE p.maidenName = :maidenName"),
    @NamedQuery(name = "People.findByGender", query = "SELECT p FROM People p WHERE p.gender = :gender")})
public class People extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "last_name")
    private String lastName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 40)
    @Column(name = "middle_name")
    private String middleName;
    @NotNull
    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dob;
    @Column(name = "next_of_kin")
    private Integer nextOfKin;
    @Size(max = 45)
    @Column(name = "maiden_name")
    private String maidenName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "gender")
    private String gender;
    @NotNull
    @Size(max = 20)
    @Column(name = "marital_status")
    private String maritalStatus;
    @Size(max = 45)
    @NotNull
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "update_required")
    private Boolean updateRequired;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "personId")
    private List<ContactInformations> contactInformationsList;
    @JoinColumn(name = "state_of_origin_id", referencedColumnName = "geographical_boundary_id")
    @ManyToOne(optional = false)
    private GeographicalBoundaries stateOfOriginId;
    @JoinColumn(name = "nationality_id", referencedColumnName = "geographical_boundary_id")
    @ManyToOne(optional = false)
    private GeographicalBoundaries nationalityId;
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    @ManyToOne(optional = false)
    private Addresses addressId;
    @JoinColumn(name = "picture_upload_id", referencedColumnName = "file_upload_id")
    @ManyToOne
    private FileUploads pictureUploadId;
    @JoinColumn(name = "logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails logindetailId;
    @JoinColumn(name = "person_title_id", referencedColumnName = "person_title_id")
    @ManyToOne
    private PersonTitles personTitleId;
    @JoinColumn(name = "religion_id", referencedColumnName = "religion_id")
    @ManyToOne
    private Religions religionId;
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations organization;
    
    @Formula("Concat_ws(' ',first_name, last_name)")
    private String fullName;

    @Transient
    private String serviceMessage;

    public People() {
    }

    public People(Long personId) {
        this.id = personId;
    }

    public People(Long personId, String lastName, String firstName, String gender, String mname) {
        this.id = personId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.middleName = mname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Integer getNextOfKin() {
        return nextOfKin;
    }

    public void setNextOfKin(Integer nextOfKin) {
        this.nextOfKin = nextOfKin;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getUpdateRequired() {
        return updateRequired;
    }

    public void setUpdateRequired(Boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

     
    public List<ContactInformations> getContactInformationsList() {
        return contactInformationsList;
    }

    public void setContactInformationsList(List<ContactInformations> contactInformationsList) {
        this.contactInformationsList = contactInformationsList;
    }

    public GeographicalBoundaries getStateOfOriginId() {
        return stateOfOriginId;
    }

    public void setStateOfOriginId(GeographicalBoundaries stateOfOriginId) {
        this.stateOfOriginId = stateOfOriginId;
    }

    public String getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(String serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public GeographicalBoundaries getNationalityId() {
        return nationalityId;
    }

    public void setNationalityId(GeographicalBoundaries nationalityId) {
        this.nationalityId = nationalityId;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    public FileUploads getPictureUploadId() {
        return pictureUploadId;
    }

    public void setPictureUploadId(FileUploads pictureUploadId) {
        this.pictureUploadId = pictureUploadId;
    }

    public Logindetails getLogindetailId() {
        return logindetailId;
    }

    public void setLogindetailId(Logindetails logindetailId) {
        this.logindetailId = logindetailId;
    }

    public PersonTitles getPersonTitleId() {
        return personTitleId;
    }

    public void setPersonTitleId(PersonTitles personTitleId) {
        this.personTitleId = personTitleId;
    }

    public Religions getReligionId() {
        return religionId;
    }

    public void setReligionId(Religions religionId) {
        this.religionId = religionId;
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
        if (!(object instanceof People)) {
            return false;
        }
        People other = (People) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.People[ personId=" + id + " ]";
    }

    /**
     * @return the organization
     */
    public Organizations getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organizations organization) {
        this.organization = organization;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
