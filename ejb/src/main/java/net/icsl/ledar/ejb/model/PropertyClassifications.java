package net.icsl.ledar.ejb.model;

import java.io.Serializable;
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
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_property_use")
@AttributeOverride(name = "id", column = @Column(name = "property_classification_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyClassifications.findAll", query = "SELECT p FROM PropertyClassifications p"),
    @NamedQuery(name = "PropertyClassifications.findByPropertyClassificationId", query = "SELECT p FROM PropertyClassifications p WHERE p.id = :propertyClassificationId"),
    @NamedQuery(name = "PropertyClassifications.findByClassificationName", query = "SELECT p FROM PropertyClassifications p WHERE p.classificationName = :classificationName")})
public class PropertyClassifications extends IcslLedarModelBase implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyClassificationId")
    private List<PropertyUsageCategories> propertyUsageCategoriesList;
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "classification_name")
    private String classificationName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyClassificationId")
    private List<PropertyClassificationDetails> propertyClassificationDetailsList;

    public PropertyClassifications() {
    }

    public PropertyClassifications(Long propertyClassificationId) {
        this.id = propertyClassificationId;
    }

    public PropertyClassifications(Long propertyClassificationId, String classificationName) {
        this.id = propertyClassificationId;
        this.classificationName = classificationName;
    }

    public PropertyClassifications(Long propertyClassificationId, String classificationName, String description) {
        this.id = propertyClassificationId;
        this.classificationName = classificationName;
        this.description = description;

    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<PropertyClassificationDetails> getPropertyClassificationDetailsList() {
        return propertyClassificationDetailsList;
    }

    public void setPropertyClassificationDetailsList(List<PropertyClassificationDetails> wardLandPropertiesList) {
        this.propertyClassificationDetailsList = wardLandPropertiesList;
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
        if (!(object instanceof PropertyClassifications)) {
            return false;
        }
        PropertyClassifications other = (PropertyClassifications) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyClassifications[ propertyClassificationId=" + id + " ]";
    }

     
    public List<PropertyUsageCategories> getPropertyUsageCategoriesList() {
        return propertyUsageCategoriesList;
    }

    public void setPropertyUsageCategoriesList(List<PropertyUsageCategories> propertyUsageCategoriesList) {
        this.propertyUsageCategoriesList = propertyUsageCategoriesList;
    }

}
