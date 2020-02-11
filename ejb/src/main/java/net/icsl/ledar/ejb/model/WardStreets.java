package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "ref_ward_streets")
@AttributeOverride(name = "id", column = @Column(name = "ward_street_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "WardStreets.findAll", query = "SELECT w FROM WardStreets w")
    ,
    @NamedQuery(name = "WardStreets.findByWardStreetId", query = "SELECT w FROM WardStreets w WHERE w.id = :wardStreetId")
    ,
    @NamedQuery(name = "WardStreets.findByStreetName", query = "SELECT w FROM WardStreets w WHERE w.streetName = :streetName")
    ,
    @NamedQuery(name = "WardStreets.findByOffStreetOne", query = "SELECT w FROM WardStreets w WHERE w.offStreetOne = :offStreetOne")
    ,
    @NamedQuery(name = "WardStreets.findByOffStreetTwo", query = "SELECT w FROM WardStreets w WHERE w.offStreetTwo = :offStreetTwo")})
public class WardStreets extends IcslLedarModelBase implements Serializable {

    @OneToMany(mappedBy = "wardStreetId")
    private Collection<StreetGaps> streetGapsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wardStreetId")
    private List<BareLands> bareLandsList;
    @JoinColumn(name = "created_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    private Logindetails createdById;

    @JoinColumn(name = "ward_town_id", referencedColumnName = "ward_town_id")
    //@ManyToOne(optional = false)
    @ManyToOne
    private WardTowns wardTownId;

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "street_name")
    private String streetName;
    //@Basic(optional = false)
    //@NotNull
    //@Size(min = 1, max = 100)
    @Column(name = "off_street_one")
    private String offStreetOne;
    @Size(max = 100)
    @Column(name = "off_street_two")
    private String offStreetTwo;
    @Size(max = 100)
    @Column(name = "nearest_bus_stop")
    private String nearestBusStop;
    @Column(name = "no_of_buildings")
    private Long noOfBuildings;
    @Column(name = "has_electricity")
    private Boolean hasElectricity;
    @Column(name = "is_generator_electricity")
    private Boolean isGeneratorElectricity;
    @Column(name = "has_street_light")
    private Boolean hasStreetLight;
    @Column(name = "is_community_light")
    private Boolean isCommunityLight;
    @Size(max = 15)
    @Column(name = "waste_disposal_system")
    private String wasteDisposalSystem;
    @Size(max = 15)
    @Column(name = "tarred_road")
    private String tarredRoad;
    @Size(max = 25)
    @Column(name = "untarred_road")
    private String untarredRoad;
    @Column(name = "has_drainage_facility")
    private Boolean hasDrainageFacility;
    @Column(name = "drainage_covered")
    private Boolean drainageCovered;
    @Column(name = "has_walkways")
    private Boolean hasWalkways;
    @Column(name = "has_street_landscaping")
    private Boolean hasStreetLandscaping;
    @Size(max = 15)
    @Column(name = "nearest_bus_stop_type")
    private String nearestBusStopType;
    @Size(max = 15)
    @Column(name = "nearest_rail_terminus")
    private String nearestRailTerminus;
    @Size(max = 15)
    @Column(name = "nearest_water_terminus")
    private String nearestWaterTerminus;
    @Column(name = "is_verified")
    private Boolean isVerified;
    @Size(max = 1000)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "estate_name")
    private String estateName;

    @JoinColumn(name = "verified_by_id", referencedColumnName = "logindetail_id")
    @ManyToOne
    //@ManyToOne(optional = false)
    private Logindetails verifiedById;
    @JoinColumn(name = "settlement_type_id", referencedColumnName = "settlement_type_id")
    @ManyToOne
    //@ManyToOne(optional = false)
    private SettlementTypes settlementTypeId;
    @JoinColumn(name = "street_type_id", referencedColumnName = "street_type_id")
    @ManyToOne
    //@ManyToOne(optional = false)
    private StreetTypes streetTypeId;
    @JoinColumn(name = "lcda_ward_id", referencedColumnName = "lcda_ward_id")
    @ManyToOne(optional = false)
    private LcdaWards lcdaWardId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wardStreetId")
    private List<WardLandProperties> wardLandPropertiesList;

    @Transient
    private String wardName, townName;
    @Transient
    private Long wardId;

    public WardStreets() {
    }

    public WardStreets(Long wardStreetId) {
        this.id = wardStreetId;
    }

    public WardStreets(Long wardStreetId, String streetName, String offStreetOne, String nearest_busstop, Long ward_id, Date created_) {
        this.id = wardStreetId;
        this.streetName = streetName;
        this.offStreetOne = offStreetOne;
        this.nearestBusStop = nearest_busstop;
        this.created = created_;
        this.wardId = ward_id;
    }

    public WardStreets(Long wardStreetId, String streetName, String offStreetOne, String nearest_busstop, Date created_) {
        this.id = wardStreetId;
        this.streetName = streetName;
        this.offStreetOne = offStreetOne;
        this.nearestBusStop = nearest_busstop;
        this.created = created_;
    }

    public WardStreets(Long wardStreetId, String streetName, String offStreetOne, String nearest_busstop, String estate_name, Date created_) {
        this.id = wardStreetId;
        this.streetName = streetName;
        this.offStreetOne = offStreetOne;
        this.nearestBusStop = nearest_busstop;
        this.created = created_;
        this.estateName = estate_name;
    }

    public WardStreets(Long street_id, String streetName, String offStreetOne, String offStreetTwo, String nearestBusStop, String wardId, String townId, Date modified_) {
        this.streetName = streetName;
        this.id = street_id;
        this.offStreetOne = offStreetOne;
        this.offStreetTwo = offStreetTwo;
        this.nearestBusStop = nearestBusStop;
        this.wardName = wardId;
        this.townName = townId;
        this.modified = modified_;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getOffStreetOne() {
        return offStreetOne;
    }

    public void setOffStreetOne(String offStreetOne) {
        this.offStreetOne = offStreetOne;
    }

    public String getOffStreetTwo() {
        return offStreetTwo;
    }

    public void setOffStreetTwo(String offStreetTwo) {
        this.offStreetTwo = offStreetTwo;
    }

    public String getNearestBusStop() {
        return nearestBusStop;
    }

    public void setNearestBusStop(String nearestBusStop) {
        this.nearestBusStop = nearestBusStop;
    }

    public Long getNoOfBuildings() {
        return noOfBuildings;
    }

    public void setNoOfBuildings(Long noOfBuildings) {
        this.noOfBuildings = noOfBuildings;
    }

    public Boolean getHasElectricity() {
        return hasElectricity;
    }

    public void setHasElectricity(Boolean hasElectricity) {
        this.hasElectricity = hasElectricity;
    }

    public Boolean getIsGeneratorElectricity() {
        return isGeneratorElectricity;
    }

    public void setIsGeneratorElectricity(Boolean isGeneratorElectricity) {
        this.isGeneratorElectricity = isGeneratorElectricity;
    }

    public Boolean getHasStreetLight() {
        return hasStreetLight;
    }

    public void setHasStreetLight(Boolean hasStreetLight) {
        this.hasStreetLight = hasStreetLight;
    }

    public Boolean getIsCommunityLight() {
        return isCommunityLight;
    }

    public void setIsCommunityLight(Boolean isCommunityLight) {
        this.isCommunityLight = isCommunityLight;
    }

    public String getWasteDisposalSystem() {
        return wasteDisposalSystem;
    }

    public void setWasteDisposalSystem(String wasteDisposalSystem) {
        this.wasteDisposalSystem = wasteDisposalSystem;
    }

    public String getTarredRoad() {
        return tarredRoad;
    }

    public void setTarredRoad(String tarredRoad) {
        this.tarredRoad = tarredRoad;
    }

    public String getUntarredRoad() {
        return untarredRoad;
    }

    public void setUntarredRoad(String untarredRoad) {
        this.untarredRoad = untarredRoad;
    }

    public Boolean getHasDrainageFacility() {
        return hasDrainageFacility;
    }

    public void setHasDrainageFacility(Boolean hasDrainageFacility) {
        this.hasDrainageFacility = hasDrainageFacility;
    }

    public Boolean getDrainageCovered() {
        return drainageCovered;
    }

    public void setDrainageCovered(Boolean drainageCovered) {
        this.drainageCovered = drainageCovered;
    }

    public Boolean getHasWalkways() {
        return hasWalkways;
    }

    public void setHasWalkways(Boolean hasWalkways) {
        this.hasWalkways = hasWalkways;
    }

    public Boolean getHasStreetLandscaping() {
        return hasStreetLandscaping;
    }

    public void setHasStreetLandscaping(Boolean hasStreetLandscaping) {
        this.hasStreetLandscaping = hasStreetLandscaping;
    }

    public String getNearestBusStopType() {
        return nearestBusStopType;
    }

    public void setNearestBusStopType(String nearestBusStopType) {
        this.nearestBusStopType = nearestBusStopType;
    }

    public String getNearestRailTerminus() {
        return nearestRailTerminus;
    }

    public void setNearestRailTerminus(String nearestRailTerminus) {
        this.nearestRailTerminus = nearestRailTerminus;
    }

    public String getNearestWaterTerminus() {
        return nearestWaterTerminus;
    }

    public void setNearestWaterTerminus(String nearestWaterTerminus) {
        this.nearestWaterTerminus = nearestWaterTerminus;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Logindetails getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(Logindetails verifiedById) {
        this.verifiedById = verifiedById;
    }

    public SettlementTypes getSettlementTypeId() {
        return settlementTypeId;
    }

    public void setSettlementTypeId(SettlementTypes settlementTypeId) {
        this.settlementTypeId = settlementTypeId;
    }

    public StreetTypes getStreetTypeId() {
        return streetTypeId;
    }

    public void setStreetTypeId(StreetTypes streetTypeId) {
        this.streetTypeId = streetTypeId;
    }

    public LcdaWards getLcdaWardId() {
        return lcdaWardId;
    }

    public void setLcdaWardId(LcdaWards lcdaWardId) {
        this.lcdaWardId = lcdaWardId;
    }

     
    public List<WardLandProperties> getWardLandPropertiesList() {
        return wardLandPropertiesList;
    }

    public void setWardLandPropertiesList(List<WardLandProperties> wardLandPropertiesList) {
        this.wardLandPropertiesList = wardLandPropertiesList;
    }

    public WardTowns getWardTownId() {
        return wardTownId;
    }

    public void setWardTownId(WardTowns wardTownId) {
        this.wardTownId = wardTownId;
    }

    /**
     * @return the wardName
     */
    public String getWardName() {
        return wardName;
    }

    /**
     * @param wardName the wardName to set
     */
    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    /**
     * @return the townName
     */
    public String getTownName() {
        return townName;
    }

    /**
     * @param townName the townName to set
     */
    public void setTownName(String townName) {
        this.townName = townName;
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

     
    public List<BareLands> getBareLandsList() {
        return bareLandsList;
    }

    public void setBareLandsList(List<BareLands> bareLandsList) {
        this.bareLandsList = bareLandsList;
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
     * @return the wardId
     */
    public Long getWardId() {
        return wardId;
    }

    /**
     * @param wardId the wardId to set
     */
    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

     
    public Collection<StreetGaps> getStreetGapsCollection() {
        return streetGapsCollection;
    }

    public void setStreetGapsCollection(Collection<StreetGaps> streetGapsCollection) {
        this.streetGapsCollection = streetGapsCollection;
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.WardStreets[ wardStreetId=" + streetName + " ]";
    }
}
