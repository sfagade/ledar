package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
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
@Table(name = "ref_commercial_types")
@AttributeOverride(name = "id", column = @Column(name = "commercial_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "CommercialTypes.findAll", query = "SELECT c FROM CommercialTypes c"),
    @NamedQuery(name = "CommercialTypes.findByCommercialTypeId", query = "SELECT c FROM CommercialTypes c WHERE c.id = :commercialTypeId"),
    @NamedQuery(name = "CommercialTypes.findByCommercialTypeName", query = "SELECT c FROM CommercialTypes c WHERE c.commercialTypeName = :commercialTypeName")})
public class CommercialTypes extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "commercial_type_name")
    private String commercialTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    @Column(name = "inits_mapping")
    private String initsMapping;

    /** @OneToMany(mappedBy = "commercialTypeId")
    private List<PropertyClassificationDetails> propertyClassificationDetailsList; */

    public CommercialTypes() {
    }

    public CommercialTypes(Long commercialTypeId) {
        this.id = commercialTypeId;
    }

    public CommercialTypes(Long commercialTypeId, String commercialTypeName, Date created_) {
        this.id = commercialTypeId;
        this.commercialTypeName = commercialTypeName;
        this.created = created_;
    }

    public String getCommercialTypeName() {
        return commercialTypeName;
    }

    public void setCommercialTypeName(String commercialTypeName) {
        this.commercialTypeName = commercialTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**  
    public List<PropertyClassificationDetails> getPropertyClassificationDetailsList() {
        return propertyClassificationDetailsList;
    }

    public void setPropertyClassificationDetailsList(List<PropertyClassificationDetails> propertyClassificationDetailsList) {
        this.propertyClassificationDetailsList = propertyClassificationDetailsList;
    } */

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CommercialTypes)) {
            return false;
        }
        CommercialTypes other = (CommercialTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.CommercialTypes[ commercialTypeId=" + id + " ]";
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
