/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 * @created Expression created is undefined on line 13, column 15 in Templates/Classes/Class.java.
 */
@Entity
@Table(name = "ref_departments")
@AttributeOverride(name = "id", column = @Column(name = "department_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "Departments.findAll", query = "SELECT d FROM Departments d"),
    @NamedQuery(name = "Departments.findByDepartmentId", query = "SELECT d FROM Departments d WHERE d.id = :departmentId"),
    @NamedQuery(name = "Departments.findByDepartmentName", query = "SELECT d FROM Departments d WHERE d.departmentName = :departmentName"),
    @NamedQuery(name = "Departments.findByGroupEmail", query = "SELECT d FROM Departments d WHERE d.groupEmail = :groupEmail")})
public class Departments extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "department_name")
    private String departmentName;
    @Size(max = 75)
    @Column(name = "group_email")
    private String groupEmail;
    @Size(max = 1000)
    @Column(name = "remarks")
    private String remarks;
    
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    @ManyToOne(optional = false)
    private OfficeLocations locationId;

    public Departments() {
    }

    public Departments(Long departmentId) {
        this.id = departmentId;
    }

    public Departments(Long departmentId, String departmentName) {
        this.id = departmentId;
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getGroupEmail() {
        return groupEmail;
    }

    public void setGroupEmail(String groupEmail) {
        this.groupEmail = groupEmail;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public OfficeLocations getLocationId() {
        return locationId;
    }

    public void setLocationId(OfficeLocations locationId) {
        this.locationId = locationId;
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
        if (!(object instanceof Departments)) {
            return false;
        }
        Departments other = (Departments) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.Departments[ departmentId=" + id + " ]";
    }

}
