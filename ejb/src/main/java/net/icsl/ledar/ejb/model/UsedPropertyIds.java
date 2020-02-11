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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 

/**
 *
 * @author sfagade
 * @created Sep 28, 2016
 */
@Entity
@Table(name = "used_property_ids", uniqueConstraints = @UniqueConstraint(columnNames = {"property_id"}))
@AttributeOverride(name = "id", column = @Column(name = "used_property_id_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "UsedPropertyIds.findAll", query = "SELECT u FROM UsedPropertyIds u"),
    @NamedQuery(name = "UsedPropertyIds.findByUsedPropertyIdId", query = "SELECT u FROM UsedPropertyIds u WHERE u.id = :usedPropertyIdId"),
    @NamedQuery(name = "UsedPropertyIds.findByPropertyId", query = "SELECT u FROM UsedPropertyIds u WHERE u.propertyId = :propertyId")})
public class UsedPropertyIds extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "property_id", unique = true)
    private String propertyId;
    @Column(name = "use_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date useDate;

    public UsedPropertyIds() {
    }

    public UsedPropertyIds(Long usedPropertyIdId) {
        this.id = usedPropertyIdId;
    }

    public UsedPropertyIds(Long usedPropertyIdId, String propertyId) {
        this.id = usedPropertyIdId;
        this.propertyId = propertyId;
    }

    public UsedPropertyIds(Long usedPropertyIdId, String propertyId, Date use_date) {
        this.id = usedPropertyIdId;
        this.propertyId = propertyId;
        this.useDate = use_date;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public Date getUseDate() {
        return useDate;
    }

    public void setUseDate(Date useDate) {
        this.useDate = useDate;
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
        if (!(object instanceof UsedPropertyIds)) {
            return false;
        }
        UsedPropertyIds other = (UsedPropertyIds) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.UsedPropertyIds[ id=" + id + " ]";
    }

}
