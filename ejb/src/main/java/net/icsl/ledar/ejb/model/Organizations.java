
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "organizations")
@AttributeOverride(name = "id", column = @Column(name = "organization_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Organizations.findAll", query = "SELECT o FROM Organizations o"),
    @NamedQuery(name = "Organizations.findByOrganizationId", query = "SELECT o FROM Organizations o WHERE o.id = :organizationId"),
    @NamedQuery(name = "Organizations.findByOrganizationNm", query = "SELECT o FROM Organizations o WHERE o.organizationNm = :organizationNm"),
    @NamedQuery(name = "Organizations.findByOrganizationNumber", query = "SELECT o FROM Organizations o WHERE o.organizationNumber = :organizationNumber"),
    @NamedQuery(name = "Organizations.findByOrganzaitionCode", query = "SELECT o FROM Organizations o WHERE o.organzaitionCode = :organzaitionCode")})
public class Organizations extends IcslLedarModelBase implements Serializable {

    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organizationId")
    private List<OfficeLocations> officeLocationsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ownerOrganizationId")
    private List<WardLandProperties> landProperties;
    @OneToMany(mappedBy = "contractorId")
    private Collection<LocalCouncilDevArea> localCouncilDevAreaCollection;
    @OneToMany(mappedBy = "contractorId")
    private Collection<LcdaWards> lcdaWardsCollection;
    private static final long serialVersionUID = 1L;
    
    //@Size(min = 1, max = 200)
    @Column(name = "organization_nm")
    private String organizationNm;
    
    //@Size(min = 1, max = 20)
    @Column(name = "organization_number")
    private String organizationNumber;
    
    //@Size(min = 1, max = 12)
    @Column(name = "organzaition_code")
    private String organzaitionCode;
    @Size(max = 9)
    @Column(name = "rcno")
    private String rcno;
    @Column(name = "enabled")
    private Boolean enabled;
    @Column(name = "owner_person_id")
    private BigInteger ownerPersonId;
    @Size(max = 45)
    @Column(name = "email_address")
    private String emailAddress;
    @Size(max = 45)
    @Column(name = "web_url")
    private String webUrl;
    
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    @ManyToOne
    private Addresses addressId;
    @JoinColumn(name = "organization_logo_id", referencedColumnName = "file_upload_id")
    @ManyToOne
    private FileUploads organizationLogoId;
    @JoinColumn(name = "organization_type_id", referencedColumnName = "organization_type_id")
    @ManyToOne(optional = false)
    private OrganizationTypes organizationTypeId;
    
    @JoinColumn(name = "senatorial_district_id", referencedColumnName = "senatorial_district_id")
    @ManyToOne
    private SenatorialDistricts senatorialDistrictId;

    public Organizations() {
    }

    public Organizations(Long organizationId) {
        this.id = organizationId;
    }

    public Organizations(Long organizationId, String organizationNm, String organizationNumber, String organzaitionCode) {
        this.id = organizationId;
        this.organizationNm = organizationNm;
        this.organizationNumber = organizationNumber;
        this.organzaitionCode = organzaitionCode;
    }

    public Organizations(Long org_id, String organizationNm, String organzaitionCode, Date created) {
        this.organizationNm = organizationNm;
        this.organzaitionCode = organzaitionCode;
        this.id = org_id;
        this.created = created;
    }
    
    public Organizations(Long org_id, String organizationNm, String organzaitionCode, SenatorialDistricts senateDist, Date created) {
        this.organizationNm = organizationNm;
        this.organzaitionCode = organzaitionCode;
        this.id = org_id;
        this.created = created;
        this.senatorialDistrictId = senateDist;
    }

    public String getOrganizationNm() {
        return organizationNm;
    }

    public void setOrganizationNm(String organizationNm) {
        this.organizationNm = organizationNm;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }

    public String getOrganzaitionCode() {
        return organzaitionCode;
    }

    public void setOrganzaitionCode(String organzaitionCode) {
        this.organzaitionCode = organzaitionCode;
    }

    public String getRcno() {
        return rcno;
    }

    public void setRcno(String rcno) {
        this.rcno = rcno;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public BigInteger getOwnerPersonId() {
        return ownerPersonId;
    }

    public void setOwnerPersonId(BigInteger ownerPersonId) {
        this.ownerPersonId = ownerPersonId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    public FileUploads getOrganizationLogoId() {
        return organizationLogoId;
    }

    public void setOrganizationLogoId(FileUploads organizationLogoId) {
        this.organizationLogoId = organizationLogoId;
    }

    public OrganizationTypes getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(OrganizationTypes organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
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
        if (!(object instanceof Organizations)) {
            return false;
        }
        Organizations other = (Organizations) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Organizations[ organizationId=" + id + " ]";
    }

    /**
     * @return the senatorialDistrictId
     */
    public SenatorialDistricts getSenatorialDistrictId() {
        return senatorialDistrictId;
    }

    /**
     * @param senatorialDistrictId the senatorialDistrictId to set
     */
    public void setSenatorialDistrictId(SenatorialDistricts senatorialDistrictId) {
        this.senatorialDistrictId = senatorialDistrictId;
    }

     
    public Collection<LocalCouncilDevArea> getLocalCouncilDevAreaCollection() {
        return localCouncilDevAreaCollection;
    }

    public void setLocalCouncilDevAreaCollection(Collection<LocalCouncilDevArea> localCouncilDevAreaCollection) {
        this.localCouncilDevAreaCollection = localCouncilDevAreaCollection;
    }

     
    public Collection<LcdaWards> getLcdaWardsCollection() {
        return lcdaWardsCollection;
    }

    public void setLcdaWardsCollection(Collection<LcdaWards> lcdaWardsCollection) {
        this.lcdaWardsCollection = lcdaWardsCollection;
    }

     
    public List<OfficeLocations> getOfficeLocationsList() {
        return officeLocationsList;
    }

    public void setOfficeLocationsList(List<OfficeLocations> officeLocationsList) {
        this.officeLocationsList = officeLocationsList;
    }

    /**
     * @return the landProperties
     */
    public List<WardLandProperties> getLandProperties() {
        return landProperties;
    }

    /**
     * @param landProperties the landProperties to set
     */
    public void setLandProperties(List<WardLandProperties> landProperties) {
        this.landProperties = landProperties;
    }
}
