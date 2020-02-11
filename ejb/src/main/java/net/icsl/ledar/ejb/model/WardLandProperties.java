package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Cacheable(true)
@Table(name = "ward_land_properties")
@AttributeOverride(name = "id", column = @Column(name = "ward_land_property_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
@NamedQueries({
    @NamedQuery(name = "WardLandProperties.findAll", query = "SELECT w FROM WardLandProperties w")
    ,@NamedQuery(name = "WardLandProperties.findByWardLandPropertyId", query = "SELECT w FROM WardLandProperties w WHERE w.id = :wardLandPropertyId")
    ,@NamedQuery(name = "WardLandProperties.findByPropertyId", query = "SELECT w FROM WardLandProperties w WHERE w.propertyId = :propertyId")
    ,@NamedQuery(name = "WardLandProperties.findByPropertyNumber", query = "SELECT w FROM WardLandProperties w WHERE w.propertyNumber = :propertyNumber")
    ,@NamedQuery(name = "WardLandProperties.findByIsIrregularAddress", query = "SELECT w FROM WardLandProperties w WHERE w.isIrregularAddress = :isIrregularAddress")
    ,@NamedQuery(name = "WardLandProperties.findByRoadSide", query = "SELECT w FROM WardLandProperties w WHERE w.roadSide = :roadSide")
    ,@NamedQuery(name = "WardLandProperties.findByDateCaptured", query = "SELECT w FROM WardLandProperties w WHERE w.dateCaptured = :dateCaptured")})
public class WardLandProperties extends IcslLedarModelBase implements Serializable {

    @Column(name = "is_inits_synced", columnDefinition = "tinyint(1) default 0")
    private Boolean isInitsSynced;
    @Column(name = "no_of_buildings")
    private Integer noOfBuildings;
    @Size(max = 25)
    @Column(name = "property_valuation_status", columnDefinition = "varchar(25) default 'PENDING VALUATION'")
    private String propertyValuationStatus;
//
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wardLandPropertyId")
    private List<PropertyComplaints> propertyComplaintsList;
    @JoinColumn(name = "property_quality_id", referencedColumnName = "property_quality_id")
    @ManyToOne
    private PropertyQualities propertyQualities;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wardLandPropertyId")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<PropertyDocuments> propertyDocumentsCollection;
    @Size(max = 18)
    @Column(name = "legacy_property_id")
    private String legacyPropertyId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "wardLandPropertyId")
    private PropertyValuations propertyValuation;

    @Column(name = "is_block_number")
    private Boolean isBlockNumber;
    @Column(name = "title_document")
    private Boolean titleDocument;

    @Column(name = "document_viewed")
    private Boolean documentViewed;
    @JoinColumn(name = "verified_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails verifiedById;
    @JoinColumn(name = "last_updated_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails lastUpdatedById;
    @JoinColumn(name = "synced_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails syncedById;
    @JoinColumn(name = "entered_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails EnteredById;
    @JoinColumn(name = "no_title_document_id", referencedColumnName = "no_title_document_id")
    @ManyToOne
    private NoTitleDocumentTypes noTitleDocumentId;
    @JoinColumn(name = "title_document_id", referencedColumnName = "title_document_id")
    @ManyToOne
    private TitleDocumentTypes titleDocumentId;
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 18)
    @Column(name = "property_id")
    private String propertyId;
    @Column(name = "property_number")
    private String propertyNumber;
    @Column(name = "is_irregular_address")
    private Boolean isIrregularAddress;
    @Column(name = "is_verified")
    private Boolean isVerified;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "road_side")
    private String roadSide;
    @Basic(optional = false)
    @NotNull
    //@Size(min = 1, max = 35)
    @Column(name = "property_longitude")
    private String propertyLongitude;
    @Basic(optional = false)
    @NotNull
    //@Size(min = 1, max = 35)
    @Column(name = "property_latitude")
    private String propertyLatitude;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_captured")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCaptured;
    @Column(name = "verified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifiedDate;
    @Column(name = "inits_sync_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date initsSyncDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "property_types")
    private String propertyTypes;
    @Size(min = 1, max = 25)
    @Column(name = "land_size")
    private String landSize;

    @Size(max = 200, message = "Address too long")
    @Column(name = "irregular_address")
    private String irregularAddress;
    @Size(max = 25)
    @Column(name = "electricity_type")
    private String electricityType;
    @Column(name = "has_electricity")
    private Boolean hasElectricity;
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    @Column(name = "district_name")
    private String districtName;
    @Column(name = "lga_name")
    private String lgaName;
    @Basic(optional = false)
    @NotNull
    @Size(max = 35)
    @Column(name = "water_supply")
    private String waterSupply;

    @JoinColumn(name = "captured_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails capturedById;
    @JoinColumn(name = "owner_organization_id", referencedColumnName = "organization_id")
    @ManyToOne()
    private Organizations ownerOrganizationId;
    @JoinColumn(name = "contractor_id", referencedColumnName = "organization_id")
    @ManyToOne(optional = false)
    private Organizations contractorId;
    @JoinColumn(name = "property_biodata_id", referencedColumnName = "property_biodata_id")
    @ManyToOne()
    private PropertyBiodatas propertyBiodataId;

    @JoinColumn(name = "ward_street_id", referencedColumnName = "ward_street_id")
    @ManyToOne()
    private WardStreets wardStreetId;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "wardLandPropertyId")
    private List<PropertyClassificationDetails> propertyClassificationDetailsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wardLandPropertyId")
    private List<PropertyServices> propertyServiceTypesList;

    @Column(name = "settlement_type_name")
    private String settlementTypeName;
    @Column(name = "has_street_light")
    private Boolean hasStreetLight;
    @Column(name = "street_light_type")
    private String streetLightType;
    @Column(name = "waste_disposal_system")
    private String wasteDisposalSystem;
    @Column(name = "tarred_road")
    private Boolean tarredRoad;
    @Column(name = "untarred_road")
    private Boolean untarredRoad;
    @Column(name = "road_condition")
    private String roadCondition;
    @Column(name = "has_drainage_facility")
    private Boolean hasDrainageFacility;
    @Column(name = "is_drainage_covered")
    private Boolean isDrainageCovered;
    @Column(name = "has_walkways")
    private Boolean hasWalkways;
    @Column(name = "has_street_landscaping")
    private Boolean hasStreetLandscaping;
    @Column(name = "nearest_bus_stop")
    private String nearestBusStop;
    @Column(name = "nearest_rail_terminus")
    private String nearestRailTerminus;
    @Column(name = "nearest_water_terminus")
    private String nearestWaterRerminus;
    @Column(name = "ownership_type")
    private String ownershipType;
    @Column(name = "phcn_id")
    private String phcnId;
    @Column(name = "lswc_id")
    private String lswcId;
    @Column(name = "estate_name")
    private String estateName;
    @Column(name = "building_area")
    private Double buildingArea;
    @Column(name = "push_status")
    private String pushStatus;
    @Column(name = "inits_id")
    private Long initsId;
    @Column(name = "admin_comment")
    private String adminComment;

    public WardLandProperties() {
    }

    public WardLandProperties(Long wardLandPropertyId) {
        this.id = wardLandPropertyId;
    }

    public WardLandProperties(Long prop_id, String legacyPropertyId, Boolean isBlockNumber, Boolean titleDocument, Boolean documentViewed, Logindetails verifiedById,
            NoTitleDocumentTypes noTitleDocumentId, TitleDocumentTypes titleDocumentId, String propertyId, String propertyNumber, Boolean isIrregularAddress, Boolean isVerified, String roadSide,
            String propertyLongitude, String propertyLatitude, Date dateCaptured, Date verifiedDate, String propertyTypes, String electricityType, Boolean hasElectricity,
            String description, String waterSupply, Logindetails capturedById, PropertyBiodatas propertyBiodataId, WardStreets wardStreetId, String land_size,
            Date created_, Date modified_) {
        this.legacyPropertyId = legacyPropertyId;
        this.isBlockNumber = isBlockNumber;
        this.titleDocument = titleDocument;
        //this.isCompletedStructure = isCompletedStructure;
        this.documentViewed = documentViewed;
        this.verifiedById = verifiedById;
        this.noTitleDocumentId = noTitleDocumentId;
        this.titleDocumentId = titleDocumentId;
        this.propertyId = propertyId;
        this.propertyNumber = propertyNumber;
        this.isIrregularAddress = isIrregularAddress;
        this.isVerified = isVerified;
        this.roadSide = roadSide;
        this.propertyLongitude = propertyLongitude;
        this.propertyLatitude = propertyLatitude;
        this.dateCaptured = dateCaptured;
        this.verifiedDate = verifiedDate;
        this.propertyTypes = propertyTypes;
        //this.buildingFootprint = buildingFootprint;
        this.electricityType = electricityType;
        this.hasElectricity = hasElectricity;
        this.description = description;
        this.waterSupply = waterSupply;
        this.capturedById = capturedById;
        this.propertyBiodataId = propertyBiodataId;
        this.landSize = land_size;
        this.wardStreetId = wardStreetId;
        this.id = prop_id;
        this.created = created_;
        this.modified = modified_;
    }

    public WardLandProperties(Long wardLandPropertyId, String propertyId, String propertyNumber, String roadSide, String propertyLongitude, String propertyLatitude, Date dateCaptured,
            String propertyTypes, String gpsLongitude, String gpsLatitude) {
        this.id = wardLandPropertyId;
        this.propertyId = propertyId;
        this.propertyNumber = propertyNumber;
        this.roadSide = roadSide;
        this.propertyLongitude = propertyLongitude;
        this.propertyLatitude = propertyLatitude;
        this.dateCaptured = dateCaptured;
        this.propertyTypes = propertyTypes;
        this.districtName = gpsLongitude;
        this.lgaName = gpsLatitude;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyNumber() {
        return propertyNumber;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public Boolean getIsIrregularAddress() {
        return isIrregularAddress;
    }

    public void setIsIrregularAddress(Boolean isIrregularAddress) {
        this.isIrregularAddress = isIrregularAddress;
    }

    public String getRoadSide() {
        return roadSide;
    }

    public void setRoadSide(String roadSide) {
        this.roadSide = roadSide;
    }

    public String getPropertyLongitude() {
        return propertyLongitude;
    }

    public void setPropertyLongitude(String propertyLongitude) {
        this.propertyLongitude = propertyLongitude;
    }

    public String getPropertyLatitude() {
        return propertyLatitude;
    }

    public void setPropertyLatitude(String propertyLatitude) {
        this.propertyLatitude = propertyLatitude;
    }

    public Date getDateCaptured() {
        return dateCaptured;
    }

    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
    }

    public String getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(String propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Logindetails getCapturedById() {
        return capturedById;
    }

    public void setCapturedById(Logindetails capturedById) {
        this.capturedById = capturedById;
    }

    public PropertyBiodatas getPropertyBiodataId() {
        return propertyBiodataId;
    }

    public void setPropertyBiodataId(PropertyBiodatas propertyBiodataId) {
        this.propertyBiodataId = propertyBiodataId;
    }

    public String getLandSize() {
        return landSize;
    }

    public void setLandSize(String landSize) {
        this.landSize = landSize;
    }

    public WardStreets getWardStreetId() {
        return wardStreetId;
    }

    public void setWardStreetId(WardStreets wardStreetId) {
        this.wardStreetId = wardStreetId;
    }

     
    public List<PropertyClassificationDetails> getPropertyClassificationDetailsList() {
        return propertyClassificationDetailsList;
    }

    public void setPropertyClassificationDetailsList(List<PropertyClassificationDetails> propertyClassificationDetailsList) {
        this.propertyClassificationDetailsList = propertyClassificationDetailsList;
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
        if (!(object instanceof WardLandProperties)) {
            return false;
        }
        WardLandProperties other = (WardLandProperties) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.WardLandProperties[ wardLandPropertyId=" + id + " ]";
    }

    /**
     * @return the waterSupply
     */
    public String getWaterSupply() {
        return waterSupply;
    }

    /**
     * @param waterSupply the waterSupply to set
     */
    public void setWaterSupply(String waterSupply) {
        this.waterSupply = waterSupply;
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
     * @return the verifiedDate
     */
    public Date getVerifiedDate() {
        return verifiedDate;
    }

    /**
     * @param verifiedDate the verifiedDate to set
     */
    public void setVerifiedDate(Date verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    /**
     * @return the electricityType
     */
    public String getElectricityType() {
        return electricityType;
    }

    /**
     * @param electricityType the electricityType to set
     */
    public void setElectricityType(String electricityType) {
        this.electricityType = electricityType;
    }

    /**
     * @return the hasElectricity
     */
    public Boolean getHasElectricity() {
        return hasElectricity;
    }

    /**
     * @param hasElectricity the hasElectricity to set
     */
    public void setHasElectricity(Boolean hasElectricity) {
        this.hasElectricity = hasElectricity;
    }

    public Boolean getIsBlockNumber() {
        return isBlockNumber;
    }

    public void setIsBlockNumber(Boolean isBlockNumber) {
        this.isBlockNumber = isBlockNumber;
    }

    public Boolean getTitleDocument() {
        return titleDocument;
    }

    public void setTitleDocument(Boolean titleDocument) {
        this.titleDocument = titleDocument;
    }

    public Boolean getDocumentViewed() {
        return documentViewed;
    }

    public void setDocumentViewed(Boolean documentViewed) {
        this.documentViewed = documentViewed;
    }

    public Logindetails getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(Logindetails verifiedById) {
        this.verifiedById = verifiedById;
    }

    public NoTitleDocumentTypes getNoTitleDocumentId() {
        return noTitleDocumentId;
    }

    public void setNoTitleDocumentId(NoTitleDocumentTypes noTitleDocumentId) {
        this.noTitleDocumentId = noTitleDocumentId;
    }

    public TitleDocumentTypes getTitleDocumentId() {
        return titleDocumentId;
    }

    public void setTitleDocumentId(TitleDocumentTypes titleDocumentId) {
        this.titleDocumentId = titleDocumentId;
    }

    public String getLegacyPropertyId() {
        return legacyPropertyId;
    }

    public void setLegacyPropertyId(String legacyPropertyId) {
        this.legacyPropertyId = legacyPropertyId;
    }

     
    public Collection<PropertyDocuments> getPropertyDocumentsCollection() {
        return propertyDocumentsCollection;
    }

    public void setPropertyDocumentsCollection(Collection<PropertyDocuments> propertyDocumentsCollection) {
        this.propertyDocumentsCollection = propertyDocumentsCollection;
    }

    /**
     * @return the irregularAddress
     */
    public String getIrregularAddress() {
        return irregularAddress;
    }

    /**
     * @param irregularAddress the irregularAddress to set
     */
    public void setIrregularAddress(String irregularAddress) {
        this.irregularAddress = irregularAddress;
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
     * @return the hasStreetLight
     */
    public Boolean getHasStreetLight() {
        return hasStreetLight;
    }

    /**
     * @param hasStreetLight the hasStreetLight to set
     */
    public void setHasStreetLight(Boolean hasStreetLight) {
        this.hasStreetLight = hasStreetLight;
    }

    /**
     * @return the streetLightType
     */
    public String getStreetLightType() {
        return streetLightType;
    }

    /**
     * @param streetLightType the streetLightType to set
     */
    public void setStreetLightType(String streetLightType) {
        this.streetLightType = streetLightType;
    }

    /**
     * @return the wasteDisposalSystem
     */
    public String getWasteDisposalSystem() {
        return wasteDisposalSystem;
    }

    /**
     * @param wasteDisposalSystem the wasteDisposalSystem to set
     */
    public void setWasteDisposalSystem(String wasteDisposalSystem) {
        this.wasteDisposalSystem = wasteDisposalSystem;
    }

    /**
     * @return the tarredRoad
     */
    public Boolean getTarredRoad() {
        return tarredRoad;
    }

    /**
     * @param tarredRoad the tarredRoad to set
     */
    public void setTarredRoad(Boolean tarredRoad) {
        this.tarredRoad = tarredRoad;
    }

    /**
     * @return the untarredRoad
     */
    public Boolean getUntarredRoad() {
        return untarredRoad;
    }

    /**
     * @param untarredRoad the untarredRoad to set
     */
    public void setUntarredRoad(Boolean untarredRoad) {
        this.untarredRoad = untarredRoad;
    }

    /**
     * @return the hasDrainageFacility
     */
    public Boolean getHasDrainageFacility() {
        return hasDrainageFacility;
    }

    /**
     * @param hasDrainageFacility the hasDrainageFacility to set
     */
    public void setHasDrainageFacility(Boolean hasDrainageFacility) {
        this.hasDrainageFacility = hasDrainageFacility;
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

    public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getLgaName() {
		return lgaName;
	}

	public void setLgaName(String lgaName) {
		this.lgaName = lgaName;
	}

	/**
     * @return the hasWalkways
     */
    public Boolean getHasWalkways() {
        return hasWalkways;
    }

    /**
     * @param hasWalkways the hasWalkways to set
     */
    public void setHasWalkways(Boolean hasWalkways) {
        this.hasWalkways = hasWalkways;
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
     * @return the nearestWaterRerminus
     */
    public String getNearestWaterRerminus() {
        return nearestWaterRerminus;
    }

    /**
     * @param nearestWaterRerminus the nearestWaterRerminus to set
     */
    public void setNearestWaterRerminus(String nearestWaterRerminus) {
        this.nearestWaterRerminus = nearestWaterRerminus;
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

    /**
     * @return the phcnId
     */
    public String getPhcnId() {
        return phcnId;
    }

    /**
     * @param phcnId the phcnId to set
     */
    public void setPhcnId(String phcnId) {
        this.phcnId = phcnId;
    }

    /**
     * @return the lswcId
     */
    public String getLswcId() {
        return lswcId;
    }

    /**
     * @param lswcId the lswcId to set
     */
    public void setLswcId(String lswcId) {
        this.lswcId = lswcId;
    }

     
    public List<PropertyComplaints> getPropertyComplaintsList() {
        return propertyComplaintsList;
    }

    public void setPropertyComplaintsList(List<PropertyComplaints> propertyComplaintsList) {
        this.propertyComplaintsList = propertyComplaintsList;
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

    public Logindetails getLastUpdatedById() {
        return lastUpdatedById;
    }

    public void setLastUpdatedById(Logindetails lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
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
     * @return the EnteredById
     */
    public Logindetails getEnteredById() {
        return EnteredById;
    }

    /**
     * @param EnteredById the EnteredById to set
     */
    public void setEnteredById(Logindetails EnteredById) {
        this.EnteredById = EnteredById;
    }

    public Integer getNoOfBuildings() {
        return noOfBuildings;
    }

    public void setNoOfBuildings(Integer noOfBuildings) {
        this.noOfBuildings = noOfBuildings;
    }

    public String getPropertyValuationStatus() {
        return propertyValuationStatus;
    }

    public void setPropertyValuationStatus(String propertyValuationStatus) {
        this.propertyValuationStatus = propertyValuationStatus;
    }

    /**
     * @return the propertyValuation
     */
    public PropertyValuations getPropertyValuation() {
        return propertyValuation;
    }

    /**
     * @param propertyValuation the propertyValuation to set
     */
    public void setPropertyValuation(PropertyValuations propertyValuation) {
        this.propertyValuation = propertyValuation;
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
     * @return the buildingArea
     */
    public Double getBuildingArea() {
        return buildingArea;
    }

    /**
     * @param buildingArea the buildingArea to set
     */
    public void setBuildingArea(Double buildingArea) {
        this.buildingArea = buildingArea;
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

    public Long getInitsId() {
        return initsId;
    }

    public void setInitsId(Long initsId) {
        this.initsId = initsId;
    }

    public Date getInitsSyncDate() {
        return initsSyncDate;
    }

    public void setInitsSyncDate(Date syncDate) {
        this.initsSyncDate = syncDate;
    }

    /**
     * @return the adminComment
     */
    public String getAdminComment() {
        return adminComment;
    }

    /**
     * @param adminComment the adminComment to set
     */
    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

}
