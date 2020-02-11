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
@Table(name = "property_qualities")
@AttributeOverride(name = "id", column = @Column(name = "property_quality_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyQualities.findAll", query = "SELECT p FROM PropertyQualities p"),
    @NamedQuery(name = "PropertyQualities.findByPropertyQualityId", query = "SELECT p FROM PropertyQualities p WHERE p.id = :propertyQualityId"),
    @NamedQuery(name = "PropertyQualities.findByQualityName", query = "SELECT p FROM PropertyQualities p WHERE p.qualityName = :qualityName"),
    @NamedQuery(name = "PropertyQualities.findByPercentageValue", query = "SELECT p FROM PropertyQualities p WHERE p.percentageValue = :percentageValue")})
public class PropertyQualities extends IcslLedarModelBase implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyQualities")
    private List<WardLandProperties> wardLandPropertiesList;

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "quality_name")
    private String qualityName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "percentage_value")
    private Float percentageValue;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    @Column(name = "inits_mapping")
    private String initsMapping;
    

    public PropertyQualities() {
    }

    public PropertyQualities(Long propertyQualityId) {
        this.id = propertyQualityId;
    }

    public PropertyQualities(Long propertyQualityId, String qualityName, Float percentage, Date created_) {
        this.id = propertyQualityId;
        this.qualityName = qualityName;
        this.created = created_;
        this.percentageValue = percentage;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public Float getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(Float percentageValue) {
        this.percentageValue = percentageValue;
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
        if (!(object instanceof PropertyQualities)) {
            return false;
        }
        PropertyQualities other = (PropertyQualities) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyQualities[ propertyQualityId=" + id + " ]";
    }

     
    public List<WardLandProperties> getWardLandPropertiesList() {
        return wardLandPropertiesList;
    }

    public void setWardLandPropertiesList(List<WardLandProperties> wardLandPropertiesList) {
        this.wardLandPropertiesList = wardLandPropertiesList;
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
