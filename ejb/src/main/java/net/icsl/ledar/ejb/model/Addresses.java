
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "addresses")
@AttributeOverride(name = "id", column = @Column(name = "address_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
@NamedQueries({
    @NamedQuery(name = "Addresses.findAll", query = "SELECT a FROM Addresses a"),
    @NamedQuery(name = "Addresses.findByAddressId", query = "SELECT a FROM Addresses a WHERE a.id = :addressId"),
    @NamedQuery(name = "Addresses.findByAddress", query = "SELECT a FROM Addresses a WHERE a.address = :address"),
    @NamedQuery(name = "Addresses.findByCity", query = "SELECT a FROM Addresses a WHERE a.city = :city"),
    @NamedQuery(name = "Addresses.findByCreatedBy", query = "SELECT a FROM Addresses a WHERE a.createdBy = :createdBy")})
public class Addresses extends IcslLedarModelBase implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "locationAddressId")
    private List<OfficeLocations> officeLocationsList;
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "address")
    private String address;
    @Size(max = 70)
    @Column(name = "address2")
    private String address2;
    @Size(max = 100)
    @Column(name = "directions")
    private String directions;
    @Size(max = 100)
    @Column(name = "city")
    private String city;
    @Size(max = 8)
    @Column(name = "area_code")
    private String areaCode;
    @Size(max = 30)
    @NotNull
    @Column(name = "created_by")
    private String createdBy;
    
    @JoinColumn(name = "geographical_state_id", referencedColumnName = "geographical_boundary_id")
    @ManyToOne(optional = false)
    private GeographicalBoundaries geographicalStateId;
    @JoinColumn(name = "geographical_country_id", referencedColumnName = "geographical_boundary_id")
    @ManyToOne(optional = false)
    private GeographicalBoundaries geographicalCountryId;
    @OneToMany(mappedBy = "addressId")
    private List<ContactInformations> contactInformationsList;
    @OneToMany(mappedBy = "addressId")
    private List<People> peopleList;
    @OneToMany(mappedBy = "addressId")
    private List<Organizations> organizationsList;

    public Addresses() {
    }

    public Addresses(Long addressId) {
        this.id = addressId;
    }

    public Addresses(Long addressId, String address, Date created) {
        this.id = addressId;
        this.address = address;
        this.created = created;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public GeographicalBoundaries getGeographicalStateId() {
        return geographicalStateId;
    }

    public void setGeographicalStateId(GeographicalBoundaries geographicalStateId) {
        this.geographicalStateId = geographicalStateId;
    }

    public GeographicalBoundaries getGeographicalCountryId() {
        return geographicalCountryId;
    }

    public void setGeographicalCountryId(GeographicalBoundaries geographicalCountryId) {
        this.geographicalCountryId = geographicalCountryId;
    }

    public List<ContactInformations> getContactInformationsList() {
        return contactInformationsList;
    }

    public void setContactInformationsList(List<ContactInformations> contactInformationsList) {
        this.contactInformationsList = contactInformationsList;
    }

    public List<People> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(List<People> peopleList) {
        this.peopleList = peopleList;
    }

    public List<Organizations> getOrganizationsList() {
        return organizationsList;
    }

    public void setOrganizationsList(List<Organizations> organizationsList) {
        this.organizationsList = organizationsList;
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
        if (!(object instanceof Addresses)) {
            return false;
        }
        Addresses other = (Addresses) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Addresses[ addressId=" + address + " ]";
    }

    public List<OfficeLocations> getOfficeLocationsList() {
        return officeLocationsList;
    }

    public void setOfficeLocationsList(List<OfficeLocations> officeLocationsList) {
        this.officeLocationsList = officeLocationsList;
    }
}
