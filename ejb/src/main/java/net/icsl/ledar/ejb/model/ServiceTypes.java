
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
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_service_types")
@AttributeOverride(name = "id", column = @Column(name = "service_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ServiceTypes.findAll", query = "SELECT s FROM ServiceTypes s"),
    @NamedQuery(name = "ServiceTypes.findByServiceTypeId", query = "SELECT s FROM ServiceTypes s WHERE s.id = :serviceTypeId"),
    @NamedQuery(name = "ServiceTypes.findByServiceTypeName", query = "SELECT s FROM ServiceTypes s WHERE s.serviceTypeName = :serviceTypeName")})
public class ServiceTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "service_type_name")
    private String serviceTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceTypeId")
    private List<PropertyServices> propertyServiceTypesList;

    public ServiceTypes() {
    }

    public ServiceTypes(long serviceTypeId) {
        this.id = serviceTypeId;
    }

    public ServiceTypes(Long serviceTypeId, String serviceTypeName, Date created_) {
        this.id = serviceTypeId;
        this.serviceTypeName = serviceTypeName;
        this.created = created_;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<PropertyServices> getPropertyServiceTypesList() {
        return propertyServiceTypesList;
    }

    public void setPropertyServiceTypesList(List<PropertyServices> propertyServiceTypesList) {
        this.propertyServiceTypesList = propertyServiceTypesList;
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
        if (!(object instanceof ServiceTypes)) {
            return false;
        }
        ServiceTypes other = (ServiceTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ServiceTypes[ serviceTypeId=" + id + " ]";
    }

}
