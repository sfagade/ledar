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
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "property_biodatas")
@AttributeOverride(name = "id", column = @Column(name = "property_biodata_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyBiodatas.findAll", query = "SELECT p FROM PropertyBiodatas p")
    ,@NamedQuery(name = "PropertyBiodatas.findByPropertyBiodataId", query = "SELECT p FROM PropertyBiodatas p WHERE p.id = :propertyBiodataId")
    ,@NamedQuery(name = "PropertyBiodatas.findByFirstName", query = "SELECT p FROM PropertyBiodatas p WHERE p.firstName = :firstName")
    ,@NamedQuery(name = "PropertyBiodatas.findByLastName", query = "SELECT p FROM PropertyBiodatas p WHERE p.lastName = :lastName")
    ,@NamedQuery(name = "PropertyBiodatas.findByGender", query = "SELECT p FROM PropertyBiodatas p WHERE p.gender = :gender")
    ,@NamedQuery(name = "PropertyBiodatas.findByMaritalStatus", query = "SELECT p FROM PropertyBiodatas p WHERE p.maritalStatus = :maritalStatus")})
public class PropertyBiodatas extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "first_name")
    private String firstName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 45)
    @Column(name = "middle_name")
    private String middleName;

    @Size(max = 45)
    @Column(name = "gender")
    private String gender;

    @Size(max = 45)
    @Column(name = "marital_status")
    private String maritalStatus;
    /**
     * @Basic(optional = false)
     * @NotNull
     */
    @Size(max = 45)
    @Column(name = "highest_education")
    private String highestEducation;
    @Size(max = 15)
    @Column(name = "home_phone_number")
    private String homePhoneNumber;
    @Size(max = 150)
    @Column(name = "email_address")
    private String emailAddress;
    @Size(max = 15)
    @Column(name = "mobile_phone_number")
    private String mobilePhoneNumber;
    /**
     * @Basic(optional = false)
     * @NotNull
     */
    @Size(max = 25)
    @Column(name = "employment_status")
    private String employmentStatus;
    @Column(name = "web_address")
    private String webAddress;
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @JoinColumn(name = "person_title_id", referencedColumnName = "person_title_id")
    @ManyToOne
    private PersonTitles personTitleId;
    @JoinColumn(name = "biodata_person_id", referencedColumnName = "biodata_person_id")
    @ManyToOne(optional = false)
    private BiodataPersonTypes biodataPersonId;

    @JoinColumn(name = "occupation_id", referencedColumnName = "occupation_id")
    @ManyToOne
    private Occupations occupationId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyBiodataId")
    private List<WardLandProperties> wardLandPropertiesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyBiodataId")
    private List<BareLands> bareLandsList;
    @JoinColumn(name = "last_updated_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails lastUpdatedById;
    @Column(name = "push_status")
    private String pushStatus;
    @Column(name = "inits_id")
    private Long initsId;

    public PropertyBiodatas() {
    }

    public PropertyBiodatas(Long propertyBiodataId) {
        this.id = propertyBiodataId;
    }

    public PropertyBiodatas(Long propertyBiodataId, String firstName, String lastName, String gender, String maritalStatus, String highestEducation) {
        this.id = propertyBiodataId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.highestEducation = highestEducation;
    }

    public PropertyBiodatas(Long bio_id, String firstName, String lastName, String middleName, String gender, String maritalStatus, String highestEducation, String homePhoneNumber, String emailAddress,
            String mobilePhoneNumber, String employmentStatus, PersonTitles personTitleId, BiodataPersonTypes biodataPersonId, Occupations occupationId, Date created_, Date mdified) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.highestEducation = highestEducation;
        this.homePhoneNumber = homePhoneNumber;
        this.emailAddress = emailAddress;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.employmentStatus = employmentStatus;
        this.personTitleId = personTitleId;
        this.biodataPersonId = biodataPersonId;
        this.occupationId = occupationId;
        this.id = bio_id;
        this.created = created_;
        this.modified = mdified;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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

    public String getHighestEducation() {
        return highestEducation;
    }

    public void setHighestEducation(String highestEducation) {
        this.highestEducation = highestEducation;
    }

    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        this.homePhoneNumber = homePhoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public PersonTitles getPersonTitleId() {
        return personTitleId;
    }

    public void setPersonTitleId(PersonTitles personTitleId) {
        this.personTitleId = personTitleId;
    }

    public BiodataPersonTypes getBiodataPersonId() {
        return biodataPersonId;
    }

    public void setBiodataPersonId(BiodataPersonTypes biodataPersonId) {
        this.biodataPersonId = biodataPersonId;
    }

    public Occupations getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Occupations occupationId) {
        this.occupationId = occupationId;
    }

     
    public List<WardLandProperties> getWardLandPropertiesList() {
        return wardLandPropertiesList;
    }

    public void setWardLandPropertiesList(List<WardLandProperties> wardLandPropertiesList) {
        this.wardLandPropertiesList = wardLandPropertiesList;
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
        if (!(object instanceof PropertyBiodatas)) {
            return false;
        }
        PropertyBiodatas other = (PropertyBiodatas) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyBiodatas[ propertyBiodataId=" + id + " ]";
    }

    /**
     * @return the employmentStatus
     */
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * @param employmentStatus the employmentStatus to set
     */
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    /**
     * @return the webAddress
     */
    public String getWebAddress() {
        return webAddress;
    }

    /**
     * @param webAddress the webAddress to set
     */
    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public Logindetails getLastUpdatedById() {
        return lastUpdatedById;
    }

    public void setLastUpdatedById(Logindetails lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
    }

    /**
     * @return the dateOfBirth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the pushStatus
     */
    public String getPushStatus() {
        return pushStatus;
    }

    /**
     * @param pushStatus the pushStatus to set
     */
    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    public Long getInitsId() {
        return initsId;
    }

    public void setInitsId(Long initsId) {
        this.initsId = initsId;
    }

    /**
     * @return the bareLandsList
     */
    public List<BareLands> getBareLandsList() {
        return bareLandsList;
    }

    /**
     * @param bareLandsList the bareLandsList to set
     */
    public void setBareLandsList(List<BareLands> bareLandsList) {
        this.bareLandsList = bareLandsList;
    }

}
