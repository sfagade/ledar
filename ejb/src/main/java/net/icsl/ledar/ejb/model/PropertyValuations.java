
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
 

/**
 * @author sfagade
 * @date Feb 9, 2016
 */
@Entity
@Table(name = "property_valuations")
@AttributeOverride(name = "id", column = @Column(name = "property_valuation_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyValuations.findAll", query = "SELECT p FROM PropertyValuations p"),
    @NamedQuery(name = "PropertyValuations.findByPropertyValuationId", query = "SELECT p FROM PropertyValuations p WHERE p.id = :propertyValuationId"),
    @NamedQuery(name = "PropertyValuations.findByValuationAmount", query = "SELECT p FROM PropertyValuations p WHERE p.lucCharge = :valuationAmount"),
    @NamedQuery(name = "PropertyValuations.findByValueDate", query = "SELECT p FROM PropertyValuations p WHERE p.valuationDate = :valueDate")})
public class PropertyValuations extends IcslLedarModelBase implements Serializable {

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    //@NotNull
    @Column(name = "accessed_value")
    private BigDecimal accessedValue;
    @Column(name = "valuation_date")
    @Temporal(TemporalType.DATE)
    private Date valuationDate;
    
    @JoinColumn(name = "accessed_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails accessedById;
    private static final long serialVersionUID = 1L;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    //@NotNull
    @Column(name = "luc_charge")
    private BigDecimal lucCharge;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "land_value")
    private BigDecimal landValue;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "building_value")
    private BigDecimal buildingValue;
    @Column(name = "valuation_remarks")
    private String valuationRemarks;
    
    @JoinColumn(name = "ward_land_property_id", referencedColumnName = "ward_land_property_id")
    @ManyToOne(optional = false)
    private WardLandProperties wardLandPropertyId;

    public PropertyValuations() {
    }

    public PropertyValuations(Long propertyValuationId) {
        this.id = propertyValuationId;
    }

    public PropertyValuations(Long propertyValuationId, BigDecimal valuationAmount, Date created_) {
        this.id = propertyValuationId;
        this.lucCharge = valuationAmount;
        this.created = created_;
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
        if (!(object instanceof PropertyValuations)) {
            return false;
        }
        PropertyValuations other = (PropertyValuations) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyValuations[ propertyValuationId=" + id + " ]";
    }

    /**
     * @return the lucCharge
     */
    public BigDecimal getLucCharge() {
        return lucCharge;
    }

    /**
     * @param lucCharge the lucCharge to set
     */
    public void setLucCharge(BigDecimal lucCharge) {
        this.lucCharge = lucCharge;
    }

    public BigDecimal getAccessedValue() {
        return accessedValue;
    }

    public void setAccessedValue(BigDecimal accessedValue) {
        this.accessedValue = accessedValue;
    }

    public Date getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(Date valuationDate) {
        this.valuationDate = valuationDate;
    }

    public Logindetails getAccessedById() {
        return accessedById;
    }

    public void setAccessedById(Logindetails accessedById) {
        this.accessedById = accessedById;
    }

    /**
     * @return the landValue
     */
    public BigDecimal getLandValue() {
        return landValue;
    }

    /**
     * @param landValue the landValue to set
     */
    public void setLandValue(BigDecimal landValue) {
        this.landValue = landValue;
    }

    /**
     * @return the buildingValue
     */
    public BigDecimal getBuildingValue() {
        return buildingValue;
    }

    /**
     * @param buildingValue the buildingValue to set
     */
    public void setBuildingValue(BigDecimal buildingValue) {
        this.buildingValue = buildingValue;
    }

	public String getValuationRemarks() {
		return valuationRemarks;
	}

	public void setValuationRemarks(String valuationRemarks) {
		this.valuationRemarks = valuationRemarks;
	}
}
