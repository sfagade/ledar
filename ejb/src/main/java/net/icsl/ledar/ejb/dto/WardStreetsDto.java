package net.icsl.ledar.ejb.dto;

import java.util.Date;

/**
 *
 * @author sfagade
 */
public class WardStreetsDto {

    private Long street_id, createdById, districtId, lcdaId, verifiedById, associatedTo;

    private String streetName, createdByName, districtName, lcdaName, verifiedByName, estateName;

    private Boolean isVerified;

    private Date created, modified;

    public WardStreetsDto() {
    }

    public WardStreetsDto(Long id) {
        this.street_id = id;
    }

    public WardStreetsDto(Long street_id, Long createdById, Long districtId, Long lcdaId, Long verifiedById, String streetName, String estate_name, String createdByName, String districtName,
            String lcdaName, String verifiedByName, Boolean isVerified, Long assoTo, Date created, Date modified) {
        this.street_id = street_id;
        this.createdById = createdById;
        this.districtId = districtId;
        this.lcdaId = lcdaId;
        this.verifiedById = verifiedById;
        this.streetName = streetName;
        this.createdByName = createdByName;
        this.districtName = districtName;
        this.lcdaName = lcdaName;
        this.verifiedByName = verifiedByName;
        this.isVerified = isVerified;
        this.estateName = estate_name;
        this.associatedTo = assoTo;
        this.created = created;
        this.modified = modified;
    }

    /**
     * @return the street_id
     */
    public Long getStreet_id() {
        return street_id;
    }

    /**
     * @param street_id the street_id to set
     */
    public void setStreet_id(Long street_id) {
        this.street_id = street_id;
    }

    /**
     * @return the createdById
     */
    public Long getCreatedById() {
        return createdById;
    }

    /**
     * @param createdById the createdById to set
     */
    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    /**
     * @return the districtId
     */
    public Long getDistrictId() {
        return districtId;
    }

    /**
     * @param districtId the districtId to set
     */
    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    /**
     * @return the lcdaId
     */
    public Long getLcdaId() {
        return lcdaId;
    }

    /**
     * @param lcdaId the lcdaId to set
     */
    public void setLcdaId(Long lcdaId) {
        this.lcdaId = lcdaId;
    }

    /**
     * @return the verifiedById
     */
    public Long getVerifiedById() {
        return verifiedById;
    }

    /**
     * @param verifiedById the verifiedById to set
     */
    public void setVerifiedById(Long verifiedById) {
        this.verifiedById = verifiedById;
    }

    /**
     * @return the streetName
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * @param streetName the streetName to set
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * @return the createdByName
     */
    public String getCreatedByName() {
        return createdByName;
    }

    /**
     * @param createdByName the createdByName to set
     */
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    /**
     * @return the districtName
     */
    public String getDistrictName() {
        return districtName;
    }

    /**
     * @param districtName the districtName to set
     */
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    /**
     * @return the lcdaName
     */
    public String getLcdaName() {
        return lcdaName;
    }

    /**
     * @param lcdaName the lcdaName to set
     */
    public void setLcdaName(String lcdaName) {
        this.lcdaName = lcdaName;
    }

    /**
     * @return the verifiedByName
     */
    public String getVerifiedByName() {
        return verifiedByName;
    }

    /**
     * @param verifiedByName the verifiedByName to set
     */
    public void setVerifiedByName(String verifiedByName) {
        this.verifiedByName = verifiedByName;
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
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * @return the associatedTo
     */
    public Long getAssociatedTo() {
        return associatedTo;
    }

    /**
     * @param associatedTo the associatedTo to set
     */
    public void setAssociatedTo(Long associatedTo) {
        this.associatedTo = associatedTo;
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
}
