/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.icsl.ledar.ejb.model;

import java.io.Serializable;
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
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in Templates/Classes/Class.java.
 */
@Entity
@Table(name = "office_locations")
@AttributeOverride(name = "id", column = @Column(name = "location_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "OfficeLocations.findAll", query = "SELECT o FROM OfficeLocations o"),
    @NamedQuery(name = "OfficeLocations.findByLocationId", query = "SELECT o FROM OfficeLocations o WHERE o.id = :locationId"),
    @NamedQuery(name = "OfficeLocations.findByLocationName", query = "SELECT o FROM OfficeLocations o WHERE o.locationName = :locationName")})
public class OfficeLocations extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 55)
    @Column(name = "location_name")
    private String locationName;
    @Size(max = 1000)
    @Column(name = "remarks")
    private String remarks;
    
    @JoinColumn(name = "location_address_id", referencedColumnName = "address_id")
    @ManyToOne(optional = false)
    private Addresses locationAddressId;
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
    @ManyToOne(optional = false)
    private Organizations organizationId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "locationId")
    private List<Departments> departmentsList;

    public OfficeLocations() {
    }

    public OfficeLocations(Long locationId) {
        this.id = locationId;
    }

    public OfficeLocations(Long locationId, String locationName) {
        this.id = locationId;
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Addresses getLocationAddressId() {
        return locationAddressId;
    }

    public void setLocationAddressId(Addresses locationAddressId) {
        this.locationAddressId = locationAddressId;
    }

    public Organizations getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Organizations organizationId) {
        this.organizationId = organizationId;
    }

     
    public List<Departments> getDepartmentsList() {
        return departmentsList;
    }

    public void setDepartmentsList(List<Departments> departmentsList) {
        this.departmentsList = departmentsList;
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
        if (!(object instanceof OfficeLocations)) {
            return false;
        }
        OfficeLocations other = (OfficeLocations) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.OfficeLocations[ locationId=" + id + " ]";
    }

}
