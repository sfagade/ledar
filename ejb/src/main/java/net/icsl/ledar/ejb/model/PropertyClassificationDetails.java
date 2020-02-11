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
@Table(name = "property_classification_details")
@AttributeOverride(name = "id", column = @Column(name = "prop_classification_detail_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "PropertyClassificationDetails.findAll", query = "SELECT p FROM PropertyClassificationDetails p")
    ,
    @NamedQuery(name = "PropertyClassificationDetails.findByPropClassificationDetailId", query = "SELECT p FROM PropertyClassificationDetails p WHERE p.id = :propClassificationDetailId")
    ,
    @NamedQuery(name = "PropertyClassificationDetails.findByNoOfRooms", query = "SELECT p FROM PropertyClassificationDetails p WHERE p.noOfRooms = :noOfRooms")
    ,
    @NamedQuery(name = "PropertyClassificationDetails.findByNoOfUnits", query = "SELECT p FROM PropertyClassificationDetails p WHERE p.noOfUnits = :noOfUnits")})
public class PropertyClassificationDetails extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "usage_category_id", referencedColumnName = "usage_category_id")
    @ManyToOne(optional = false)
    private PropertyUsageCategories usageCategory;

    @Column(name = "no_of_rooms")
    private Long noOfRooms;
    @Column(name = "no_of_units")
    private Long noOfUnits;
    @Column(name = "no_of_rooms_per_unit")
    private Long noOfRoomsPerUnit;
    @Column(name = "no_of_flats")
    private Long noOfFlats;
    @Column(name = "no_of_rooms_per_flat")
    private Long noOfRoomsPerFlat;
    @Column(name = "no_of_floors")
    private Long noOfFloors;
    @Size(max = 6)
    @Column(name = "size_per_floor")
    private String sizePerFloor;
    @Size(max = 200)
    @Column(name = "property_name")
    private String propertyName;
    @Size(max = 100)
    @Column(name = "facilities")
    private String facilities;
    @Column(name = "no_of_pumps")
    private Long noOfPumps;
    @Size(max = 6)
    @Column(name = "forecourt_area")
    private String forecourtArea;
    @Size(max = 200)
    @Column(name = "warehouse_information")
    private String warehouseInformation;
    @Size(max = 200)
    @Column(name = "recreational_information")
    private String recreationalInformation;
    @Column(name = "is_private")
    private Boolean isPrivateBusiness;
    //@Size(max = 45)
    @Column(name = "building_footprint")
    private Double buildingFootprint;
    @Column(name = "is_completed_structure")
    private Boolean isCompletedStructure;

    @JoinColumn(name = "ward_land_property_id", referencedColumnName = "ward_land_property_id")
    @ManyToOne(optional = false)
    private WardLandProperties wardLandPropertyId;
    @JoinColumn(name = "residential_type_id", referencedColumnName = "residential_type_id")
    @ManyToOne
    private ResidentialTypes residentialTypeId;
    /**
     * @JoinColumn(name = "commercial_type_id", referencedColumnName =
     * "commercial_type_id")
     * @ManyToOne private CommercialTypes commercialTypeId;
     */
    @JoinColumn(name = "property_classification_id", referencedColumnName = "property_classification_id")
    @ManyToOne(optional = false)
    private PropertyClassifications propertyClassificationId;
    /**
     * @JoinColumn(name = "residential_use_id", referencedColumnName =
     * "residential_use_id")
     * @ManyToOne private ResidentialUseages residentialUseId;
     */
    @JoinColumn(name = "last_updated_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails lastUpdatedById;
    @Column(name = "push_status")
    private String pushStatus;
    @Column(name = "inits_id")
    private Long initsId;

    public PropertyClassificationDetails() {
    }

    public PropertyClassificationDetails(Long propClassificationDetailId) {
        this.id = propClassificationDetailId;
    }

    public PropertyClassificationDetails(Long propClassificationDetailId, Long noOfRooms, Long noOfUnits, Long noOfRoomsPerUnit, Long noOfFlats, Long noOfRoomsPerFlat, Long noOfFloors,
            String sizePerFloor, String propertyName, String facilities, Long noOfPumps, String forecourtArea, WardLandProperties wardLandPropertyId, ResidentialTypes residentialTypeId,
            PropertyUsageCategories commercialTypeId, double footprint, Boolean isCompletedStructure, PropertyClassifications propertyClassificationId, Date created_, Date modified_) {
        this.id = propClassificationDetailId;
        this.noOfRooms = noOfRooms;
        this.noOfUnits = noOfUnits;
        this.noOfRoomsPerUnit = noOfRoomsPerUnit;
        this.noOfFlats = noOfFlats;
        this.noOfRoomsPerFlat = noOfRoomsPerFlat;
        this.noOfFloors = noOfFloors;
        this.sizePerFloor = sizePerFloor;
        this.propertyName = propertyName;
        this.facilities = facilities;
        this.noOfPumps = noOfPumps;
        this.forecourtArea = forecourtArea;
        this.wardLandPropertyId = wardLandPropertyId;
        this.residentialTypeId = residentialTypeId;
        this.usageCategory = commercialTypeId;
        this.buildingFootprint = footprint;
        this.isCompletedStructure = isCompletedStructure;
        this.propertyClassificationId = propertyClassificationId;
        this.created = created_;
        this.modified = modified_;
    }

    public Long getNoOfRooms() {
        return noOfRooms;
    }

    public void setNoOfRooms(Long noOfRooms) {
        this.noOfRooms = noOfRooms;
    }

    public Long getNoOfUnits() {
        return noOfUnits;
    }

    public void setNoOfUnits(Long noOfUnits) {
        this.noOfUnits = noOfUnits;
    }

    public Long getNoOfRoomsPerUnit() {
        return noOfRoomsPerUnit;
    }

    public void setNoOfRoomsPerUnit(Long noOfRoomsPerUnit) {
        this.noOfRoomsPerUnit = noOfRoomsPerUnit;
    }

    public Long getNoOfFlats() {
        return noOfFlats;
    }

    public void setNoOfFlats(Long noOfFlats) {
        this.noOfFlats = noOfFlats;
    }

    public Long getNoOfRoomsPerFlat() {
        return noOfRoomsPerFlat;
    }

    public void setNoOfRoomsPerFlat(Long noOfRoomsPerFlat) {
        this.noOfRoomsPerFlat = noOfRoomsPerFlat;
    }

    public Long getNoOfFloors() {
        return noOfFloors;
    }

    public void setNoOfFloors(Long noOfFloors) {
        this.noOfFloors = noOfFloors;
    }

    public String getSizePerFloor() {
        return sizePerFloor;
    }

    public void setSizePerFloor(String sizePerFloor) {
        this.sizePerFloor = sizePerFloor;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Long getNoOfPumps() {
        return noOfPumps;
    }

    public void setNoOfPumps(Long noOfPumps) {
        this.noOfPumps = noOfPumps;
    }

    public String getForecourtArea() {
        return forecourtArea;
    }

    public void setForecourtArea(String forecourtArea) {
        this.forecourtArea = forecourtArea;
    }

    public double getBuildingFootprint() {
        return buildingFootprint;
    }

    public void setBuildingFootprint(double buildingFootprint) {
        this.buildingFootprint = buildingFootprint;
    }

    public Boolean getIsCompletedStructure() {
        return isCompletedStructure;
    }

    public void setIsCompletedStructure(Boolean isCompletedStructure) {
        this.isCompletedStructure = isCompletedStructure;
    }

    public PropertyClassifications getPropertyClassificationId() {
        return propertyClassificationId;
    }

    public void setPropertyClassificationId(PropertyClassifications propertyClassificationId) {
        this.propertyClassificationId = propertyClassificationId;
    }

    public WardLandProperties getWardLandPropertyId() {
        return wardLandPropertyId;
    }

    public void setWardLandPropertyId(WardLandProperties wardLandPropertyId) {
        this.wardLandPropertyId = wardLandPropertyId;
    }

    public ResidentialTypes getResidentialTypeId() {
        return residentialTypeId;
    }

    public void setResidentialTypeId(ResidentialTypes residentialTypeId) {
        this.residentialTypeId = residentialTypeId;
    }

    /**
     * public CommercialTypes getCommercialTypeId() { return commercialTypeId; }
     *
     * public void setCommercialTypeId(CommercialTypes commercialTypeId) {
     * this.commercialTypeId = commercialTypeId; }
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PropertyClassificationDetails)) {
            return false;
        }
        PropertyClassificationDetails other = (PropertyClassificationDetails) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.PropertyClassificationDetails[ propClassificationDetailId=" + id + " ]";
    }

    /**
     * @return the facilities
     */
    public String getFacilities() {
        return facilities;
    }

    /**
     * @param facilities the facilities to set
     */
    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    /**
     * @return the warehouseInformation
     */
    public String getWarehouseInformation() {
        return warehouseInformation;
    }

    /**
     * @param warehouseInformation the warehouseInformation to set
     */
    public void setWarehouseInformation(String warehouseInformation) {
        this.warehouseInformation = warehouseInformation;
    }

    /**
     * @return the recreationalInformation
     */
    public String getRecreationalInformation() {
        return recreationalInformation;
    }

    /**
     * @param recreationalInformation the recreationalInformation to set
     */
    public void setRecreationalInformation(String recreationalInformation) {
        this.recreationalInformation = recreationalInformation;
    }

    /**
     * @return the isPrivateBusiness
     */
    public Boolean getIsPrivateBusiness() {
        return isPrivateBusiness;
    }

    /**
     * @param isPrivateBusiness the isPrivateBusiness to set
     */
    public void setIsPrivateBusiness(Boolean isPrivateBusiness) {
        this.isPrivateBusiness = isPrivateBusiness;
    }

    /**
     * @return the residentialUseId
     */
    /**
     * public ResidentialUseages getResidentialUseId() { return
     * residentialUseId; }
     */
    /**
     * @param residentialUseId the residentialUseId to set
     */
    /**
     * public void setResidentialUseId(ResidentialUseages residentialUseId) {
     * this.residentialUseId = residentialUseId; }
     */
    public Logindetails getLastUpdatedById() {
        return lastUpdatedById;
    }

    public void setLastUpdatedById(Logindetails lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
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

    public PropertyUsageCategories getUsageCategory() {
        return usageCategory;
    }

    public void setUsageCategory(PropertyUsageCategories usageCategory) {
        this.usageCategory = usageCategory;
    }
}
