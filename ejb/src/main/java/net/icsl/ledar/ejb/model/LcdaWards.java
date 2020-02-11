package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Cacheable(true)
@Table(name = "ref_lcda_wards",uniqueConstraints={
	    @UniqueConstraint(columnNames = {"ward_name", "local_council_dev_area_id"})
	})
@AttributeOverride(name = "id", column = @Column(name = "lcda_ward_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "LcdaWards.findAll", query = "SELECT l FROM LcdaWards l")
    ,@NamedQuery(name = "LcdaWards.findByLcdaWardId", query = "SELECT l FROM LcdaWards l WHERE l.id = :lcdaWardId")
    ,@NamedQuery(name = "LcdaWards.findByWardName", query = "SELECT l FROM LcdaWards l WHERE l.wardName = :wardName")})
public class LcdaWards extends IcslLedarModelBase implements Serializable {

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "building_value")
    private BigDecimal buildingValue;
    @Column(name = "land_value")
    private BigDecimal landValue;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "districtId")
    private Collection<StreetGaps> streetGapsCollection;

    @JoinColumn(name = "contractor_id", referencedColumnName = "organization_id")
    @ManyToOne
    private Organizations contractorId;
    @Size(max = 25)
    @Column(name = "ward_code")
    private String wardCode;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lcdaWardId")
    private List<WardTowns> wardTownsList;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails createdById;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lcdaWardId")
    private List<EnumeratorWards> enumeratorWardsList;
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ward_name")
    private String wardName;
    @Size(max = 500)
    @Column(name = "remarks")
    private String remarks;

    @JoinColumn(name = "local_council_dev_area_id", referencedColumnName = "local_council_dev_area_id")
    @ManyToOne(optional = false)
    private LocalCouncilDevArea localCouncilDevAreaId;
    @JoinColumn(name = "ward_supervisor_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails wardSupervisorById;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lcdaWardId")
    private List<WardStreets> wardStreetsList;

    @Transient
    private Long localGovId;

    public LcdaWards() {
    }

    public LcdaWards(Long lcdaWardId) {
        this.id = lcdaWardId;
    }

    public LcdaWards(Long lcdaWardId, String wardName) {
        this.id = lcdaWardId;
        this.wardName = wardName;
    }

    public LcdaWards(Long lcdaWardId, String wardName, Long lga_id) {
        this.id = lcdaWardId;
        this.wardName = wardName;
        this.localGovId = lga_id;
    }

    public LcdaWards(Long lcdaWardId, String wardName, LocalCouncilDevArea localCouncilDevAreaId, Date created_) {
        this.id = lcdaWardId;
        this.wardName = wardName;
        this.localCouncilDevAreaId = localCouncilDevAreaId;
        this.created = created_;
    }

    public LcdaWards(long ward_id, String wardName, LocalCouncilDevArea localCouncilDevAreaId, Logindetails wardSupervisorById, Date created_) {
        this.wardName = wardName;
        this.localCouncilDevAreaId = localCouncilDevAreaId;
        this.wardSupervisorById = wardSupervisorById;
        this.created = created_;
        this.id = ward_id;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalCouncilDevArea getLocalCouncilDevAreaId() {
        return localCouncilDevAreaId;
    }

    public void setLocalCouncilDevAreaId(LocalCouncilDevArea localCouncilDevAreaId) {
        this.localCouncilDevAreaId = localCouncilDevAreaId;
    }

    public Logindetails getWardSupervisorById() {
        return wardSupervisorById;
    }

    public void setWardSupervisorById(Logindetails wardSupervisorById) {
        this.wardSupervisorById = wardSupervisorById;
    }

     
    public List<WardStreets> getWardStreetsList() {
        return wardStreetsList;
    }

    public void setWardStreetsList(List<WardStreets> wardStreetsList) {
        this.wardStreetsList = wardStreetsList;
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
        if (!(object instanceof LcdaWards)) {
            return false;
        }
        LcdaWards other = (LcdaWards) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.LcdaWards[ lcdaWardId=" + id + " ]";
    }

     
    public List<EnumeratorWards> getEnumeratorWardsList() {
        return enumeratorWardsList;
    }

    public void setEnumeratorWardsList(List<EnumeratorWards> enumeratorWardsList) {
        this.enumeratorWardsList = enumeratorWardsList;
    }

     
    public List<WardTowns> getWardTownsList() {
        return wardTownsList;
    }

    public void setWardTownsList(List<WardTowns> wardTownsList) {
        this.wardTownsList = wardTownsList;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public Organizations getContractorId() {
        return contractorId;
    }

    public void setContractorId(Organizations contractorId) {
        this.contractorId = contractorId;
    }

    /**
     * @return the createdById
     */
    public Logindetails getCreatedById() {
        return createdById;
    }

    /**
     * @param createdById the createdById to set
     */
    public void setCreatedById(Logindetails createdById) {
        this.createdById = createdById;
    }

    /**
     * @return the localGovId
     */
    public Long getLocalGovId() {
        return localGovId;
    }

    /**
     * @param localGovId the localGovId to set
     */
    public void setLocalGovId(Long localGovId) {
        this.localGovId = localGovId;
    }

    public BigDecimal getBuildingValue() {
        return buildingValue;
    }

    public void setBuildingValue(BigDecimal buildingValue) {
        this.buildingValue = buildingValue;
    }

    public BigDecimal getLandValue() {
        return landValue;
    }

    public void setLandValue(BigDecimal landValue) {
        this.landValue = landValue;
    }

     
    public Collection<StreetGaps> getStreetGapsCollection() {
        return streetGapsCollection;
    }

    public void setStreetGapsCollection(Collection<StreetGaps> streetGapsCollection) {
        this.streetGapsCollection = streetGapsCollection;
    }
}
