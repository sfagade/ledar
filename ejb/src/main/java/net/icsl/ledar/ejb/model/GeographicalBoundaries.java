
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
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
@Table(name = "ref_geographical_boundaries")
@AttributeOverride(name = "id", column = @Column(name = "geographical_boundary_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "GeographicalBoundaries.findAll", query = "SELECT g FROM GeographicalBoundaries g"),
    @NamedQuery(name = "GeographicalBoundaries.findByGeographicalBoundaryId", query = "SELECT g FROM GeographicalBoundaries g WHERE g.id = :geographicalBoundaryId"),
    @NamedQuery(name = "GeographicalBoundaries.findByBoundaryName", query = "SELECT g FROM GeographicalBoundaries g WHERE g.boundaryName = :boundaryName"),
    @NamedQuery(name = "GeographicalBoundaries.findByOriginName", query = "SELECT g FROM GeographicalBoundaries g WHERE g.originName = :originName")})
public class GeographicalBoundaries extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Size(max = 35)
    @Column(name = "boundary_name")
    private String boundaryName;
    @Size(max = 7)
    @Column(name = "short_name_code")
    private String shortNameCode;
    @Size(max = 75)
    @Column(name = "origin_name")
    private String originName;
    @Size(max = 5)
    @Column(name = "country_calling_code")
    private String countryCallingCode;
    @Size(max = 5)
    @Column(name = "locale_code")
    private String localeCode;
    @Column(name = "inits_mapping")
    private String initsMapping;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geographicalStateId")
    private List<Addresses> addressesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geographicalCountryId")
    private List<Addresses> addressesList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geographicalStateId")
    private List<SenatorialDistricts> senatorialDistrictsList;
    @OneToMany(mappedBy = "boundaryParentId")
    private List<GeographicalBoundaries> geographicalBoundariesList;
    @JoinColumn(name = "boundary_parent_id", referencedColumnName = "geographical_boundary_id")
    @ManyToOne
    private GeographicalBoundaries boundaryParentId;
    @JoinColumn(name = "geographical_boundary_type_id", referencedColumnName = "geographical_boundary_type_id")
    @ManyToOne(optional = false)
    private GeographicalBoundaryTypes geographicalBoundaryTypeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stateOfOriginId")
    private List<People> peopleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nationalityId")
    private List<People> peopleList1;

    public GeographicalBoundaries() {
    }

    public GeographicalBoundaries(Long geographicalBoundaryId) {
        this.id = geographicalBoundaryId;
    }
    
    public GeographicalBoundaries(Long geographicalBoundaryId, String boundary_name) {
        this.id = geographicalBoundaryId;
        this.boundaryName = boundary_name;
    }

    public String getBoundaryName() {
        return boundaryName;
    }

    public void setBoundaryName(String boundaryName) {
        this.boundaryName = boundaryName;
    }

    public String getShortNameCode() {
        return shortNameCode;
    }

    public void setShortNameCode(String shortNameCode) {
        this.shortNameCode = shortNameCode;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

     
    public List<Addresses> getAddressesList() {
        return addressesList;
    }

    public void setAddressesList(List<Addresses> addressesList) {
        this.addressesList = addressesList;
    }

     
    public List<Addresses> getAddressesList1() {
        return addressesList1;
    }

    public void setAddressesList1(List<Addresses> addressesList1) {
        this.addressesList1 = addressesList1;
    }

     
    public List<SenatorialDistricts> getSenatorialDistrictsList() {
        return senatorialDistrictsList;
    }

    public void setSenatorialDistrictsList(List<SenatorialDistricts> senatorialDistrictsList) {
        this.senatorialDistrictsList = senatorialDistrictsList;
    }

     
    public List<GeographicalBoundaries> getGeographicalBoundariesList() {
        return geographicalBoundariesList;
    }

    public void setGeographicalBoundariesList(List<GeographicalBoundaries> geographicalBoundariesList) {
        this.geographicalBoundariesList = geographicalBoundariesList;
    }

    public GeographicalBoundaries getBoundaryParentId() {
        return boundaryParentId;
    }

    public void setBoundaryParentId(GeographicalBoundaries boundaryParentId) {
        this.boundaryParentId = boundaryParentId;
    }

    public GeographicalBoundaryTypes getGeographicalBoundaryTypeId() {
        return geographicalBoundaryTypeId;
    }

    public void setGeographicalBoundaryTypeId(GeographicalBoundaryTypes geographicalBoundaryTypeId) {
        this.geographicalBoundaryTypeId = geographicalBoundaryTypeId;
    }

     
    public List<People> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(List<People> peopleList) {
        this.peopleList = peopleList;
    }

     
    public List<People> getPeopleList1() {
        return peopleList1;
    }

    public void setPeopleList1(List<People> peopleList1) {
        this.peopleList1 = peopleList1;
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
        if (!(object instanceof GeographicalBoundaries)) {
            return false;
        }
        GeographicalBoundaries other = (GeographicalBoundaries) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.GeographicalBoundaries[ geographicalBoundaryId=" + id + " ]";
    }

    /**
     * @return the initsMapping
     */
    public String getInitsMapping() {
        return initsMapping;
    }

    /**
     * @param initsMapping the initsMapping to set
     */
    public void setInitsMapping(String initsMapping) {
        this.initsMapping = initsMapping;
    }

}
