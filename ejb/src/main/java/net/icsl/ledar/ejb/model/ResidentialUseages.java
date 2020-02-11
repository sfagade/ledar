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
@Table(name = "ref_residential_useages")
@AttributeOverride(name = "id", column = @Column(name = "residential_use_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ResidentialUseages.findAll", query = "SELECT r FROM ResidentialUseages r"),
    @NamedQuery(name = "ResidentialUseages.findByResidentialUseId", query = "SELECT r FROM ResidentialUseages r WHERE r.id = :residentialUseId"),
    @NamedQuery(name = "ResidentialUseages.findByResidentialUseName", query = "SELECT r FROM ResidentialUseages r WHERE r.residentialUseName = :residentialUseName")})
public class ResidentialUseages extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 75)
    @Column(name = "residential_use_name")
    private String residentialUseName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    @Column(name = "inits_mapping")
    private String initsMapping;
    

    public ResidentialUseages() {
    }

    public ResidentialUseages(Long residentialUseId) {
        this.id = residentialUseId;
    }

    public ResidentialUseages(Long residentialUseId, String residentialUseName, Date created_) {
        this.id = residentialUseId;
        this.residentialUseName = residentialUseName;
        this.created = created_;
    }
    
    public ResidentialUseages(Long residentialUseId, String residentialUseName, Date created_, String inits_mapping) {
        this.id = residentialUseId;
        this.residentialUseName = residentialUseName;
        this.created = created_;
        this.initsMapping = inits_mapping;
    }

    public String getResidentialUseName() {
        return residentialUseName;
    }

    public void setResidentialUseName(String residentialUseName) {
        this.residentialUseName = residentialUseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof ResidentialUseages)) {
            return false;
        }
        ResidentialUseages other = (ResidentialUseages) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ResidentialUseages[ residentialUseId=" + id + " ]";
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
