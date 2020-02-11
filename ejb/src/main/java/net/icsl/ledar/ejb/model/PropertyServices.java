package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "property_services")
@AttributeOverride(name = "id", column = @Column(name = "property_service_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyServiceTypes.findAll", query = "SELECT p FROM PropertyServices p"),
    @NamedQuery(name = "PropertyServiceTypes.findByPropertyServiceId", query = "SELECT p FROM PropertyServices p WHERE p.id = :propertyServiceId"),
    @NamedQuery(name = "PropertyServiceTypes.findByDescription", query = "SELECT p FROM PropertyServices p WHERE p.description = :description")})
public class PropertyServices extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "service_type_id", referencedColumnName = "service_type_id")
    @ManyToOne(optional = false)
    private ServiceTypes serviceTypeId;
    @JoinColumn(name = "ward_land_property_id", referencedColumnName = "ward_land_property_id")
    @ManyToOne(optional = false)
    private WardLandProperties wardLandPropertyId;

    public PropertyServices(Long propertyServiceId, String description, ServiceTypes serviceTypeId, WardLandProperties wardLandPropertyId, Date created_) {
        this.id = propertyServiceId;
        this.description = description;
        this.serviceTypeId = serviceTypeId;
        this.wardLandPropertyId = wardLandPropertyId;
        this.created = created_;
    }

    public PropertyServices() {
    }

    public PropertyServices(Long propertyServiceId) {
        this.id = propertyServiceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceTypes getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(ServiceTypes serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public WardLandProperties getWardLandPropertyId() {
        return wardLandPropertyId;
    }

    public void setWardLandPropertyId(WardLandProperties wardLandPropertyId) {
        this.wardLandPropertyId = wardLandPropertyId;
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
        if (!(object instanceof PropertyServices)) {
            return false;
        }
        PropertyServices other = (PropertyServices) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyServiceTypes[ propertyServiceId=" + id + " ]";
    }

}
