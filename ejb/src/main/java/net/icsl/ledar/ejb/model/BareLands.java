package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author sfagade
 * @date Mar 8, 2016
 */
@Entity
@Table(name = "bare_lands")
@AttributeOverride(name = "id", column = @Column(name = "bare_land_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
@NamedQueries({
    @NamedQuery(name = "BareLands.findAll", query = "SELECT b FROM BareLands b")
    ,
    @NamedQuery(name = "BareLands.findByBareLandId", query = "SELECT b FROM BareLands b WHERE b.id = :bareLandId")
    ,
    @NamedQuery(name = "BareLands.findByIsFenced", query = "SELECT b FROM BareLands b WHERE b.isFenced = :isFenced")
    ,
    @NamedQuery(name = "BareLands.findByPropertyNumber", query = "SELECT b FROM BareLands b WHERE b.propertyNumber = :propertyNumber")})
public class BareLands extends IcslLedarModelBase implements Serializable {

	@JoinColumn(name = "usage_category_id", referencedColumnName = "usage_category_id")
    @ManyToOne
    private PropertyUsageCategories usageCategory;
	@JoinColumn(name = "contractor_id", referencedColumnName = "organization_id")
    @ManyToOne(optional = false)
    private Organizations contractorId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bareLandId")
    private List<BarelandFiles> barelandFilesList;
    private static final long serialVersionUID = 1L;

    @Size(max = 25)
    @Column(name = "property_valuation_status", columnDefinition = "varchar(25) default 'PENDING VALUATION'")
    private String propertyValuationStatus;
    @Column(name = "is_inits_synced", columnDefinition = "tinyint(1) default 0")
    private Boolean isInitsSynced;
    @Column(name = "is_fenced")
    private Boolean isFenced;
    @Size(max = 45)
    @Column(name = "property_number")
    private String propertyNumber;
    @Size(max = 30)
    @Column(name = "property_id")
    private String propertyId;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    @Column(name = "land_size_area")
    private String landSizeArea;
    @Column(name = "property_latitude")
    private String propertyLatitude;
    @Column(name = "property_longitude")
    private String propertyLongitude;
    @Column(name = "road_condition")
    private String roadCondition;
    @Column(name = "is_tarred_road")
    private Boolean isTarredRoad;
    @Column(name = "is_drainage_covered")
    private Boolean isDrainageCovered;
    @Column(name = "has_street_landscaping")
    private Boolean hasStreetLandscaping;
    @Column(name = "nearest_bus_stop")
    private String nearestBusStop;
    @Column(name = "nearest_rail_terminus")
    private String nearestRailTerminus;
    @Column(name = "nearest_water_terminus")
    private String nearestWaterTerminus;
    @Column(name = "settlement_type_name")
    private String settlementTypeName;
    @Column(name = "estate_name")
    private String estateName;
    @Column(name = "road_side")
    private String roadSide;
    @Column(name = "ownership_type")
    private String ownershipType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_captured")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCaptured;
    @Column(name = "irregular_address")
    private String irregularAddress;
    @JoinColumn(name = "ward_street_id", referencedColumnName = "ward_street_id")
    @ManyToOne()
    private WardStreets wardStreetId;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails createdById;
    @JoinColumn(name = "verified_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails verifiedById;
    @JoinColumn(name = "synced_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails syncedById;
    @JoinColumn(name = "property_quality_id", referencedColumnName = "property_quality_id")
    @ManyToOne
    private PropertyQualities propertyQualities;
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
    @ManyToOne()
    private Organizations ownerOrganizationId;
    @JoinColumn(name = "property_biodata_id", referencedColumnName = "property_biodata_id")
    @ManyToOne()
    private PropertyBiodatas propertyBiodataId;
    @Column(name = "is_block_number")
    private Boolean isBlockNumber;
    @Column(name = "is_verified")
    private Boolean isVerified;
    @Column(name = "push_status")
    private String pushStatus;
    @Column(name = "inits_id")
    private Long initsId;
    @Column(name = "inits_sync_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date initsSyncDate;

    public BareLands() {
    }

    public BareLands(Long bareLandId) {
        this.id = bareLandId;
    }

    public BareLands(Long land_id, Boolean isFenced, String propertyNumber, String description, WardStreets wardStreetId, Logindetails createdById, Date timeCaptured, String prop_id, String land_size_area,
            String property_latitude, String property_longitude, String road_condition, boolean is_road_tarred, boolean is_drainage_covered, boolean has_street_landscaping,
            String nearest_bus_stop, String nearest_rail_terminus, String nearest_water_terminus, String settlement_type_name, Date created_, Date modified_) {
        this.isFenced = isFenced;
        this.propertyNumber = propertyNumber;
        this.description = description;
        this.wardStreetId = wardStreetId;
        this.createdById = createdById;
        this.created = created_;
        this.id = land_id;
        this.modified = modified_;
        this.dateCaptured = timeCaptured;
        this.propertyId = prop_id;
        this.landSizeArea = land_size_area;
        this.propertyLatitude = property_latitude;
        this.propertyLongitude = property_longitude;
        this.roadCondition = road_condition;
        this.isTarredRoad = is_road_tarred;
        this.isDrainageCovered = is_drainage_covered;
        this.hasStreetLandscaping = has_street_landscaping;
        this.nearestBusStop = nearest_bus_stop;
        this.nearestWaterTerminus = nearest_water_terminus;
        this.nearestRailTerminus = nearest_rail_terminus;
    }

    public Boolean getIsFenced() {
        return isFenced;
    }

    public void setIsFenced(Boolean isFenced) {
        this.isFenced = isFenced;
    }

    public String getPropertyNumber() {
        return propertyNumber;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WardStreets getWardStreetId() {
        return wardStreetId;
    }

    public void setWardStreetId(WardStreets wardStreetId) {
        this.wardStreetId = wardStreetId;
    }

    public Logindetails getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Logindetails createdById) {
        this.createdById = createdById;
    }

    public Logindetails getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(Logindetails verifiedById) {
        this.verifiedById = verifiedById;
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
        if (!(object instanceof BareLands)) {
            return false;
        }
        BareLands other = (BareLands) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.BareLands[ bareLandId=" + id + " ]";
    }

    /**
     * @return the dateCaptured
     */
    public Date getDateCaptured() {
        return dateCaptured;
    }

    /**
     * @param dateCaptured the dateCaptured to set
     */
    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
    }

    /**
     * @return the propertyId
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * @param propertyId the propertyId to set
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getIrregularAddress() {
        return irregularAddress;
    }

    public void setIrregularAddress(String irregularAddress) {
        this.irregularAddress = irregularAddress;
    }

    public List<BarelandFiles> getBarelandFilesList() {
        return barelandFilesList;
    }

    public void setBarelandFilesList(List<BarelandFiles> barelandFilesList) {
        this.barelandFilesList = barelandFilesList;
    }

    /**
     * @return the landSizeArea
     */
    public String getLandSizeArea() {
        return landSizeArea;
    }

    /**
     * @param landSizeArea the landSizeArea to set
     */
    public void setLandSizeArea(String landSizeArea) {
        this.landSizeArea = landSizeArea;
    }

    /**
     * @return the propertyLatitude
     */
    public String getPropertyLatitude() {
        return propertyLatitude;
    }

    /**
     * @param propertyLatitude the propertyLatitude to set
     */
    public void setPropertyLatitude(String propertyLatitude) {
        this.propertyLatitude = propertyLatitude;
    }

    /**
     * @return the propertyLongitude
     */
    public String getPropertyLongitude() {
        return propertyLongitude;
    }

    /**
     * @param propertyLongitude the propertyLongitude to set
     */
    public void setPropertyLongitude(String propertyLongitude) {
        this.propertyLongitude = propertyLongitude;
    }

    /**
     * @return the roadCondition
     */
    public String getRoadCondition() {
        return roadCondition;
    }

    /**
     * @param roadCondition the roadCondition to set
     */
    public void setRoadCondition(String roadCondition) {
        this.roadCondition = roadCondition;
    }

    /**
     * @return the isTarredRoad
     */
    public Boolean getIsTarredRoad() {
        return isTarredRoad;
    }

    /**
     * @param isTarredRoad the isTarredRoad to set
     */
    public void setIsTarredRoad(Boolean isTarredRoad) {
        this.isTarredRoad = isTarredRoad;
    }

    /**
     * @return the isDrainageCovered
     */
    public Boolean getIsDrainageCovered() {
        return isDrainageCovered;
    }

    /**
     * @param isDrainageCovered the isDrainageCovered to set
     */
    public void setIsDrainageCovered(Boolean isDrainageCovered) {
        this.isDrainageCovered = isDrainageCovered;
    }

    /**
     * @return the hasStreetLandscaping
     */
    public Boolean getHasStreetLandscaping() {
        return hasStreetLandscaping;
    }

    /**
     * @param hasStreetLandscaping the hasStreetLandscaping to set
     */
    public void setHasStreetLandscaping(Boolean hasStreetLandscaping) {
        this.hasStreetLandscaping = hasStreetLandscaping;
    }

    /**
     * @return the nearestBusStop
     */
    public String getNearestBusStop() {
        return nearestBusStop;
    }

    /**
     * @param nearestBusStop the nearestBusStop to set
     */
    public void setNearestBusStop(String nearestBusStop) {
        this.nearestBusStop = nearestBusStop;
    }

    /**
     * @return the nearestRailTerminus
     */
    public String getNearestRailTerminus() {
        return nearestRailTerminus;
    }

    /**
     * @param nearestRailTerminus the nearestRailTerminus to set
     */
    public void setNearestRailTerminus(String nearestRailTerminus) {
        this.nearestRailTerminus = nearestRailTerminus;
    }

    /**
     * @return the nearestWaterTerminus
     */
    public String getNearestWaterTerminus() {
        return nearestWaterTerminus;
    }

    /**
     * @param nearestWaterTerminus the nearestWaterTerminus to set
     */
    public void setNearestWaterTerminus(String nearestWaterTerminus) {
        this.nearestWaterTerminus = nearestWaterTerminus;
    }

    /**
     * @return the settlementTypeName
     */
    public String getSettlementTypeName() {
        return settlementTypeName;
    }

    /**
     * @param settlementTypeName the settlementTypeName to set
     */
    public void setSettlementTypeName(String settlementTypeName) {
        this.settlementTypeName = settlementTypeName;
    }

    /**
     * @return the estateName
     */
    public String getEstateName() {
        return estateName;
    }

    /**
     * @param estateName the estateName to set
     */
    public void setEstateName(String estateName) {
        this.estateName = estateName;
    }

    /**
     * @return the roadSide
     */
    public String getRoadSide() {
        return roadSide;
    }

    /**
     * @param roadSide the roadSide to set
     */
    public void setRoadSide(String roadSide) {
        this.roadSide = roadSide;
    }

    /**
     * @return the propertyQualities
     */
    public PropertyQualities getPropertyQualities() {
        return propertyQualities;
    }

    /**
     * @param propertyQualities the propertyQualities to set
     */
    public void setPropertyQualities(PropertyQualities propertyQualities) {
        this.propertyQualities = propertyQualities;
    }

    /**
     * @return the ownershipType
     */
    public String getOwnershipType() {
        return ownershipType;
    }

    /**
     * @param ownershipType the ownershipType to set
     */
    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public PropertyBiodatas getPropertyBiodataId() {
        return propertyBiodataId;
    }

    public void setPropertyBiodataId(PropertyBiodatas propertyBiodataId) {
        this.propertyBiodataId = propertyBiodataId;
    }

    /**
     * @return the ownerOrganizationId
     */
    public Organizations getOwnerOrganizationId() {
        return ownerOrganizationId;
    }

    /**
     * @param ownerOrganizationId the ownerOrganizationId to set
     */
    public void setOwnerOrganizationId(Organizations ownerOrganizationId) {
        this.ownerOrganizationId = ownerOrganizationId;
    }

    /**
     * @return the isInitsSynced
     */
    public Boolean getIsInitsSynced() {
        return isInitsSynced;
    }

    /**
     * @param isInitsSynced the isInitsSynced to set
     */
    public void setIsInitsSynced(Boolean isInitsSynced) {
        this.isInitsSynced = isInitsSynced;
    }

    /**
     * @return the propertyValuationStatus
     */
    public String getPropertyValuationStatus() {
        return propertyValuationStatus;
    }

    /**
     * @param propertyValuationStatus the propertyValuationStatus to set
     */
    public void setPropertyValuationStatus(String propertyValuationStatus) {
        this.propertyValuationStatus = propertyValuationStatus;
    }

    /**
     * @return the contractorId
     */
    public Organizations getContractorId() {
        return contractorId;
    }

    /**
     * @param contractorId the contractorId to set
     */
    public void setContractorId(Organizations contractorId) {
        this.contractorId = contractorId;
    }

    /**
     * @return the isBlockNumber
     */
    public Boolean getIsBlockNumber() {
        return isBlockNumber;
    }

    /**
     * @param isBlockNumber the isBlockNumber to set
     */
    public void setIsBlockNumber(Boolean isBlockNumber) {
        this.isBlockNumber = isBlockNumber;
    }

    /**
     * @return the pushStatus
     */
    public String getPushStatus() {
        return pushStatus;
    }

    /**
     * @param pushStatus the pushStatus to set
     */
    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    /**
     * @return the initsId
     */
    public Long getInitsId() {
        return initsId;
    }

    /**
     * @param initsId the initsId to set
     */
    public void setInitsId(Long initsId) {
        this.initsId = initsId;
    }

    /**
     * @return the syncedById
     */
    public Logindetails getSyncedById() {
        return syncedById;
    }

    /**
     * @param syncedById the syncedById to set
     */
    public void setSyncedById(Logindetails syncedById) {
        this.syncedById = syncedById;
    }

    /**
     * @return the isVerified
     */
    public Boolean getIsVerified() {
        return isVerified;
    }

    /**
     * @param isVerified the isVerified to set
     */
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    /**
     * @return the initsSyncDate
     */
    public Date getInitsSyncDate() {
        return initsSyncDate;
    }

    /**
     * @param initsSyncDate the initsSyncDate to set
     */
    public void setInitsSyncDate(Date initsSyncDate) {
        this.initsSyncDate = initsSyncDate;
    }
    
    public PropertyUsageCategories getUsageCategory() {
		return usageCategory;
	}

	public void setUsageCategory(PropertyUsageCategories usageCategory) {
		this.usageCategory = usageCategory;
	}

}
