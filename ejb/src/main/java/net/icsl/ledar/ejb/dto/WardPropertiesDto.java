package net.icsl.ledar.ejb.dto;

import java.util.Date;

/**
 *
 * @author sfagade
 * @created Jun 21, 2016
 */
public class WardPropertiesDto {

    private Long propId, lastUpdatedById, verifiedById, enteredById, capturedById, contractorId, wardStreetId;

    private String lastUpdatedByName, verifiedByName, enteredByName, propertyPid, propertyNumber, irregularAddress, capturedByName, contractorName, wardStreetName, districtName, lcdaName, estateName;
    private String propertyLatitude, propertyLongitude;
    private Boolean isIrregularAddress, isVerified, isInitsSynced;

    private Date dateCaptured, dateCreated, dateModified;

    public WardPropertiesDto() {
    }

    public WardPropertiesDto(Long prop_id, String property_idn, String longitude, String latitude, String address, String captured_by_name, Date sync_date, Date captured_date) {
        this.propId = prop_id;
        this.propertyPid = property_idn;
        this.propertyLatitude = latitude;
        this.propertyLongitude = longitude;
        this.wardStreetName = address;
        this.capturedByName = captured_by_name;
        this.dateCaptured = captured_date;
        this.dateCreated = sync_date;
    }

    public WardPropertiesDto(Long propId, Long lastUpdatedById, Long verifiedById, Long enteredById, Long capturedById, Long contractorId, Long wardStreetId, String lastUpdatedByName,
            String verifiedByName, String enteredByName, String propertyPid, String propertyNumber, String irregularAddress, String capturedByName, String contractorName, String wardStreetName,
            Boolean isIrregularAddress, Boolean isVerified, String district, String lcda, Date dateCaptured, Date dateCreated, Date dateModified, String estate_name, Boolean inits_synced) {
        this.propId = propId;
        this.lastUpdatedById = lastUpdatedById;
        this.verifiedById = verifiedById;
        this.enteredById = enteredById;
        this.capturedById = capturedById;
        this.contractorId = contractorId;
        this.wardStreetId = wardStreetId;
        this.lastUpdatedByName = lastUpdatedByName;
        this.verifiedByName = verifiedByName;
        this.enteredByName = enteredByName;
        this.propertyPid = propertyPid;
        this.propertyNumber = propertyNumber;
        this.irregularAddress = irregularAddress;
        this.capturedByName = capturedByName;
        this.contractorName = contractorName;
        this.wardStreetName = wardStreetName;
        this.isIrregularAddress = isIrregularAddress;
        this.isVerified = isVerified;
        this.dateCaptured = dateCaptured;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.districtName = district;
        this.estateName = estate_name;
        this.lcdaName = lcda;
        this.isInitsSynced = inits_synced;
    }

    /**
     * @return the propId
     */
    public Long getPropId() {
        return propId;
    }

    /**
     * @param propId the propId to set
     */
    public void setPropId(Long propId) {
        this.propId = propId;
    }

    /**
     * @return the lastUpdatedById
     */
    public Long getLastUpdatedById() {
        return lastUpdatedById;
    }

    /**
     * @param lastUpdatedById the lastUpdatedById to set
     */
    public void setLastUpdatedById(Long lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
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
     * @return the enteredById
     */
    public Long getEnteredById() {
        return enteredById;
    }

    /**
     * @param enteredById the enteredById to set
     */
    public void setEnteredById(Long enteredById) {
        this.enteredById = enteredById;
    }

    /**
     * @return the capturedById
     */
    public Long getCapturedById() {
        return capturedById;
    }

    /**
     * @param capturedById the capturedById to set
     */
    public void setCapturedById(Long capturedById) {
        this.capturedById = capturedById;
    }

    /**
     * @return the contractorId
     */
    public Long getContractorId() {
        return contractorId;
    }

    /**
     * @param contractorId the contractorId to set
     */
    public void setContractorId(Long contractorId) {
        this.contractorId = contractorId;
    }

    /**
     * @return the wardStreetId
     */
    public Long getWardStreetId() {
        return wardStreetId;
    }

    /**
     * @param wardStreetId the wardStreetId to set
     */
    public void setWardStreetId(Long wardStreetId) {
        this.wardStreetId = wardStreetId;
    }

    /**
     * @return the lastUpdatedByName
     */
    public String getLastUpdatedByName() {
        return lastUpdatedByName;
    }

    /**
     * @param lastUpdatedByName the lastUpdatedByName to set
     */
    public void setLastUpdatedByName(String lastUpdatedByName) {
        this.lastUpdatedByName = lastUpdatedByName;
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
     * @return the enteredByName
     */
    public String getEnteredByName() {
        return enteredByName;
    }

    /**
     * @param enteredByName the enteredByName to set
     */
    public void setEnteredByName(String enteredByName) {
        this.enteredByName = enteredByName;
    }

    /**
     * @return the propertyPid
     */
    public String getPropertyPid() {
        return propertyPid;
    }

    /**
     * @param propertyPid the propertyPid to set
     */
    public void setPropertyPid(String propertyPid) {
        this.propertyPid = propertyPid;
    }

    /**
     * @return the propertyNumber
     */
    public String getPropertyNumber() {
        return propertyNumber;
    }

    /**
     * @param propertyNumber the propertyNumber to set
     */
    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
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
     * @return the capturedByName
     */
    public String getCapturedByName() {
        return capturedByName;
    }

    /**
     * @param capturedByName the capturedByName to set
     */
    public void setCapturedByName(String capturedByName) {
        this.capturedByName = capturedByName;
    }

    /**
     * @return the contractorName
     */
    public String getContractorName() {
        return contractorName;
    }

    /**
     * @param contractorName the contractorName to set
     */
    public void setContractorName(String contractorName) {
        this.contractorName = contractorName;
    }

    /**
     * @return the wardStreetName
     */
    public String getWardStreetName() {
        return wardStreetName;
    }

    /**
     * @param wardStreetName the wardStreetName to set
     */
    public void setWardStreetName(String wardStreetName) {
        this.wardStreetName = wardStreetName;
    }

    /**
     * @return the isIrregularAddress
     */
    public Boolean getIsIrregularAddress() {
        return isIrregularAddress;
    }

    /**
     * @param isIrregularAddress the isIrregularAddress to set
     */
    public void setIsIrregularAddress(Boolean isIrregularAddress) {
        this.isIrregularAddress = isIrregularAddress;
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
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the dateModified
     */
    public Date getDateModified() {
        return dateModified;
    }

    /**
     * @param dateModified the dateModified to set
     */
    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
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

    public Boolean getIsInitsSynced() {
        return isInitsSynced;
    }

    public void setIsInitsSynced(Boolean isInitsSynced) {
        this.isInitsSynced = isInitsSynced;
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

}
