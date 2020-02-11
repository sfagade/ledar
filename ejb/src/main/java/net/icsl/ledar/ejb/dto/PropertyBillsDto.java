package net.icsl.ledar.ejb.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author sfagade
 * @date Feb 11, 2016
 */
public class PropertyBillsDto {

    private Long billsId, tempPropId, streetId, deliveryOfficerId;

    private Boolean isCurrent;
    private Boolean isDelivered;
    private String remarks;

    private String propertyId;
    private String propertyOwnerLname;
    private String propertyOwnerFname;
    private String propertyNumber;
    private Boolean isOwnerOccupied;
    private String propertyClassification;
    private String officerName;
    private Long officerId;
    private String streetName;
    private String wardName;
    private String lcdaId;
    private String deliveryCode;
    private Date dofn, created, modified;
    private String epaymentCode;
    private BigDecimal yearCharge;
    private BigDecimal previousYearBalance;
    private String billingYear;
    private Date deliveryDate, assignedDate;
    private String deliveryStatus;
    private String receiverName;
    private String receiverLastName;
    private String receiverFirstName;
    private Date timeDelivered;
    private String deliveryOfficerUsername;

    public PropertyBillsDto() {

    }

    public PropertyBillsDto(Long billsId, Long tempPropId, Long streetId, Long deliveryOfficerId, Boolean isCurrent, Boolean isDelivered, String remarks, String propertyId, String propertyOwnerLname,
            String propertyOwnerFname, String propertyNumber, Boolean isOwnerOccupied, String propertyClassification, String streetName, String deliveryCode, Date dofn, String epaymentCode,
            BigDecimal yearCharge, BigDecimal previousYearBalance, String billingYear, Date deliveryDate, String deliveryOfficerUsername, Date created_, Date modified_) {
        this.billsId = billsId;
        this.tempPropId = tempPropId;
        this.streetId = streetId;
        this.deliveryOfficerId = deliveryOfficerId;
        this.isCurrent = isCurrent;
        this.isDelivered = isDelivered;
        this.remarks = remarks;
        this.propertyId = propertyId;
        this.propertyOwnerLname = propertyOwnerLname;
        this.propertyOwnerFname = propertyOwnerFname;
        this.propertyNumber = propertyNumber;
        this.isOwnerOccupied = isOwnerOccupied;
        this.propertyClassification = propertyClassification;
        this.streetName = streetName;
        this.deliveryCode = deliveryCode;
        this.dofn = dofn;
        this.epaymentCode = epaymentCode;
        this.yearCharge = yearCharge;
        this.previousYearBalance = previousYearBalance;
        this.billingYear = billingYear;
        this.deliveryDate = deliveryDate;
        this.deliveryOfficerUsername = deliveryOfficerUsername;
        this.created = created_;
        this.modified = modified_;
    }

    public PropertyBillsDto(Long billsId, Long tempPropId, Boolean isCurrent, Boolean isDelivered, String propertyId, String propertyOwnerLname, String propertyOwnerFname, String propertyNumber,
            String streetName, String billingYr, Date delivered, Date created_, Date modified_) {
        this.billsId = billsId;
        this.tempPropId = tempPropId;
        this.isCurrent = isCurrent;
        this.isDelivered = isDelivered;
        this.propertyId = propertyId;
        this.propertyOwnerLname = propertyOwnerLname;
        this.propertyOwnerFname = propertyOwnerFname;
        this.propertyNumber = propertyNumber;
        this.streetName = streetName;
        this.modified = modified_;
        this.created = created_;
        this.billingYear = billingYr;
        this.deliveryDate = delivered;
    }

    public PropertyBillsDto(Long billsId, Long tempPropId, Boolean isCurrent, Boolean isDelivered, String propertyId, String propertyOwnerLname, String propertyOwnerFname, String propertyNumber,
            String streetName, String billingYr, String ward_name, String lcda_name, String officers_name, Long officer_id, Date delivered, Date date_assigned, Date created_, Date modified_) {
        this.billsId = billsId;
        this.tempPropId = tempPropId;
        this.isCurrent = isCurrent;
        this.isDelivered = isDelivered;
        this.propertyId = propertyId;
        this.propertyOwnerLname = propertyOwnerLname;
        this.propertyOwnerFname = propertyOwnerFname;
        this.propertyNumber = propertyNumber;
        this.streetName = streetName;
        this.modified = modified_;
        this.created = created_;
        this.wardName = ward_name;
        this.lcdaId = lcda_name;
        this.billingYear = billingYr;
        this.officerName = officers_name;
        this.officerId = officer_id;
        this.deliveryDate = delivered;
        this.assignedDate = date_assigned;
    }

    public PropertyBillsDto(Long prop_id, Long tempPropId, Long streetId, Long deliveryOfficerId, String propertyId, String propertyNumber, String officerName, Long officerId, String streetName,
            String wardName, String lcdaId, Date assignedDate) {
        this.tempPropId = tempPropId;
        this.streetId = streetId;
        this.deliveryOfficerId = deliveryOfficerId;
        this.propertyId = propertyId;
        this.propertyNumber = propertyNumber;
        this.officerName = officerName;
        this.officerId = officerId;
        this.streetName = streetName;
        this.wardName = wardName;
        this.lcdaId = lcdaId;
        this.billsId = prop_id;
        this.assignedDate = assignedDate;
    }

    /**
     * @return the isCurrent
     */
    public Boolean getIsCurrent() {
        return isCurrent;
    }

    /**
     * @param isCurrent the isCurrent to set
     */
    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public void setReceiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
    }

    public Date getTimeDelivered() {
        return timeDelivered;
    }

    public void setTimeDelivered(Date timeDelivered) {
        this.timeDelivered = timeDelivered;
    }

    public String getReceiverFirstName() {
        return receiverFirstName;
    }

    public void setReceiverFirstName(String receiverFirstName) {
        this.receiverFirstName = receiverFirstName;
    }

    /**
     * @return the isDelivered
     */
    public Boolean getIsDelivered() {
        return isDelivered;
    }

    /**
     * @param isDelivered the isDelivered to set
     */
    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    /**
     * @return the propertyOwnerLname
     */
    public String getPropertyOwnerLname() {
        return propertyOwnerLname;
    }

    /**
     * @param propertyOwnerLname the propertyOwnerLname to set
     */
    public void setPropertyOwnerLname(String propertyOwnerLname) {
        this.propertyOwnerLname = propertyOwnerLname;
    }

    /**
     * @return the propertyOwnerFname
     */
    public String getPropertyOwnerFname() {
        return propertyOwnerFname;
    }

    /**
     * @param propertyOwnerFname the propertyOwnerFname to set
     */
    public void setPropertyOwnerFname(String propertyOwnerFname) {
        this.propertyOwnerFname = propertyOwnerFname;
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
     * @return the isOwnerOccupied
     */
    public Boolean getIsOwnerOccupied() {
        return isOwnerOccupied;
    }

    /**
     * @param isOwnerOccupied the isOwnerOccupied to set
     */
    public void setIsOwnerOccupied(Boolean isOwnerOccupied) {
        this.isOwnerOccupied = isOwnerOccupied;
    }

    /**
     * @return the propertyClassification
     */
    public String getPropertyClassification() {
        return propertyClassification;
    }

    /**
     * @param propertyClassification the propertyClassification to set
     */
    public void setPropertyClassification(String propertyClassification) {
        this.propertyClassification = propertyClassification;
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
     * @return the deliveryCode
     */
    public String getDeliveryCode() {
        return deliveryCode;
    }

    /**
     * @param deliveryCode the deliveryCode to set
     */
    public void setDeliveryCode(String deliveryCode) {
        this.deliveryCode = deliveryCode;
    }

    /**
     * @return the dofn
     */
    public Date getDofn() {
        return dofn;
    }

    /**
     * @param dofn the dofn to set
     */
    public void setDofn(Date dofn) {
        this.dofn = dofn;
    }

    /**
     * @return the epaymentCode
     */
    public String getEpaymentCode() {
        return epaymentCode;
    }

    /**
     * @param epaymentCode the epaymentCode to set
     */
    public void setEpaymentCode(String epaymentCode) {
        this.epaymentCode = epaymentCode;
    }

    /**
     * @return the yearCharge
     */
    public BigDecimal getYearCharge() {
        return yearCharge;
    }

    /**
     * @param yearCharge the yearCharge to set
     */
    public void setYearCharge(BigDecimal yearCharge) {
        this.yearCharge = yearCharge;
    }

    /**
     * @return the previousYearBalance
     */
    public BigDecimal getPreviousYearBalance() {
        return previousYearBalance;
    }

    /**
     * @param previousYearBalance the previousYearBalance to set
     */
    public void setPreviousYearBalance(BigDecimal previousYearBalance) {
        this.previousYearBalance = previousYearBalance;
    }

    /**
     * @return the billingYear
     */
    public String getBillingYear() {
        return billingYear;
    }

    /**
     * @param billingYear the billingYear to set
     */
    public void setBillingYear(String billingYear) {
        this.billingYear = billingYear;
    }

    /**
     * @return the deliveryDate
     */
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param deliveryDate the deliveryDate to set
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return the deliveryOfficerUsername
     */
    public String getDeliveryOfficerUsername() {
        return deliveryOfficerUsername;
    }

    /**
     * @param deliveryOfficerUsername the deliveryOfficerUsername to set
     */
    public void setDeliveryOfficerUsername(String deliveryOfficerUsername) {
        this.deliveryOfficerUsername = deliveryOfficerUsername;
    }

    /**
     * @return the billsId
     */
    public Long getBillsId() {
        return billsId;
    }

    /**
     * @param billsId the billsId to set
     */
    public void setBillsId(Long billsId) {
        this.billsId = billsId;
    }

    /**
     * @return the tempPropId
     */
    public Long getTempPropId() {
        return tempPropId;
    }

    /**
     * @param tempPropId the tempPropId to set
     */
    public void setTempPropId(Long tempPropId) {
        this.tempPropId = tempPropId;
    }

    /**
     * @return the streetId
     */
    public Long getStreetId() {
        return streetId;
    }

    /**
     * @param streetId the streetId to set
     */
    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    /**
     * @return the deliveryOfficerId
     */
    public Long getDeliveryOfficerId() {
        return deliveryOfficerId;
    }

    /**
     * @param deliveryOfficerId the deliveryOfficerId to set
     */
    public void setDeliveryOfficerId(Long deliveryOfficerId) {
        this.deliveryOfficerId = deliveryOfficerId;
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
     * @return the lcdaId
     */
    public String getLcdaId() {
        return lcdaId;
    }

    /**
     * @param lcdaId the lcdaId to set
     */
    public void setLcdaId(String lcdaId) {
        this.lcdaId = lcdaId;
    }

    /**
     * @return the assignedDate
     */
    public Date getAssignedDate() {
        return assignedDate;
    }

    /**
     * @param assignedDate the assignedDate to set
     */
    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }
}
