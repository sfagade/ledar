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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 */
@Entity
@Table(name = "ref_property_usage_categories")
@AttributeOverride(name = "id", column = @Column(name = "usage_category_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
public class PropertyUsageCategories extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "inits_mapping")
    private Integer initsMapping;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    
    @JoinColumn(name = "property_classification_id", referencedColumnName = "property_classification_id")
    @ManyToOne(optional = false)
    private PropertyClassifications propertyClassificationId;

    public PropertyUsageCategories() {
    }

    public PropertyUsageCategories(Long usageCategoryId) {
        this.id = usageCategoryId;
    }

    public PropertyUsageCategories(Long usageCategoryId, String categoryName) {
        this.id = usageCategoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getInitsMapping() {
        return initsMapping;
    }

    public void setInitsMapping(Integer initsMapping) {
        this.initsMapping = initsMapping;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PropertyClassifications getPropertyClassificationId() {
        return propertyClassificationId;
    }

    public void setPropertyClassificationId(PropertyClassifications propertyClassificationId) {
        this.propertyClassificationId = propertyClassificationId;
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
        if (!(object instanceof PropertyUsageCategories)) {
            return false;
        }
        PropertyUsageCategories other = (PropertyUsageCategories) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyUsageCategories[ usageCategoryId=" + id + " ]";
    }
    
}
