/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author sfagade
 */
public class PrintedBillsDto {

    private String propertyIdn, oldPropertyIdn, epaymentCode, blockNo, ownerPlotNo, ownerStreetName, taxCategory, billingYear, payerId, nameexp48;
    private String lga, bankPaymentCode, parcelIdn, houseNo, plotNo, ownerHouseNo, ownerBlockNo, ownerAddress, usageDescription, name, nameexp47;
    private String nameexp51, barcode, paymentStatus, consultantName, deliveryComments, jsonMessage, longitude, latitude;
    private Long id, consultantId;
    private Date created, triggerDate, discDate, fullpayDate, startLatepaydate1, endLatepaydate1, startLatepaydate2, endLatepaydate2, startLatepaydate3;
    private Date endLatepaydate3, deliveryDate;
    private String houseAddress, streetName, districtName, parcelSheetNumber, flatNo, ownerFlatNo, ownerDistrictName, ownerName, nameexp49, nameexp50;
    private BigDecimal millRate, assessedValue, balanceCf, luc, amountDue, discAmount, amtLatepay1, amtLatepay2, amtLatepay3, totalAmountPaid, nextYrBf;
    private Boolean isDelivered, hasMatchPayment, isStructureChanged;

    public PrintedBillsDto() {
    }

    public PrintedBillsDto(Long bill_id, String propertyIdn, String lga, String address_, Date created) {
        this.propertyIdn = propertyIdn;
        this.id = bill_id;
        this.created = created;
        this.lga = lga;
        this.houseAddress = address_;
    }

    public PrintedBillsDto(Long bill_id, String prop_id, String street_name, String district, String lga, Date created_) {
        this.id = bill_id;
        this.propertyIdn = prop_id;
        this.streetName = street_name;
        this.districtName = district;
        this.lga = lga;
        this.created = created_;
    }

    public PrintedBillsDto(Long id, String propertyIdn, String taxCategory, String billingYear, String payerId, String lga, String bankPaymentCode, String house_no, String name, String consultantName,
            Long consultantId, Date created, Date triggerDate, String districtName, BigDecimal balanceCf, BigDecimal luc, BigDecimal amountDue, String owner, String owner_address) {
        this.propertyIdn = propertyIdn;
        this.taxCategory = taxCategory;
        this.billingYear = billingYear;
        this.payerId = payerId;
        this.lga = lga;
        this.bankPaymentCode = bankPaymentCode;
        this.name = name;
        this.consultantName = consultantName;
        this.id = id;
        this.consultantId = consultantId;
        this.created = created;
        this.triggerDate = triggerDate;
        this.districtName = districtName;
        this.balanceCf = balanceCf;
        this.luc = luc;
        this.amountDue = amountDue;
        this.houseAddress = house_no;
        this.ownerName = owner;
        this.ownerAddress = owner_address;
    }

    public PrintedBillsDto(Long bill_id, String message) {
        this.id = bill_id;
        this.jsonMessage = message;
    }

    public PrintedBillsDto(Long id, String propertyIdn, String payerId, String lga, String bankPaymentCode, String houseNo, String longitude, String latitude, Boolean has_payment, Boolean is_delivered, 
            String delivery_person_name, String tax_cat, Date created) {
        this.id = id;
        this.propertyIdn = propertyIdn;
        this.payerId = payerId;
        this.lga = lga;
        this.bankPaymentCode = bankPaymentCode;
        this.houseAddress = houseNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created = created;
        this.hasMatchPayment = has_payment;
        this.isDelivered = is_delivered;
        this.name = delivery_person_name;
        this.taxCategory = tax_cat;
    }

    /**
     * @return the propertyIdn
     */
    public String getPropertyIdn() {
        return propertyIdn;
    }

    /**
     * @param propertyIdn the propertyIdn to set
     */
    public void setPropertyIdn(String propertyIdn) {
        this.propertyIdn = propertyIdn;
    }

    /**
     * @return the lga
     */
    public String getLga() {
        return lga;
    }

    /**
     * @param lga the lga to set
     */
    public void setLga(String lga) {
        this.lga = lga;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * @return the houseAddress
     */
    public String getHouseAddress() {
        return houseAddress;
    }

    /**
     * @param houseAddress the houseAddress to set
     */
    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    /**
     * @return the oldPropertyIdn
     */
    public String getOldPropertyIdn() {
        return oldPropertyIdn;
    }

    /**
     * @param oldPropertyIdn the oldPropertyIdn to set
     */
    public void setOldPropertyIdn(String oldPropertyIdn) {
        this.oldPropertyIdn = oldPropertyIdn;
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
     * @return the blockNo
     */
    public String getBlockNo() {
        return blockNo;
    }

    /**
     * @param blockNo the blockNo to set
     */
    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    /**
     * @return the ownerPlotNo
     */
    public String getOwnerPlotNo() {
        return ownerPlotNo;
    }

    /**
     * @param ownerPlotNo the ownerPlotNo to set
     */
    public void setOwnerPlotNo(String ownerPlotNo) {
        this.ownerPlotNo = ownerPlotNo;
    }

    /**
     * @return the ownerStreetName
     */
    public String getOwnerStreetName() {
        return ownerStreetName;
    }

    /**
     * @param ownerStreetName the ownerStreetName to set
     */
    public void setOwnerStreetName(String ownerStreetName) {
        this.ownerStreetName = ownerStreetName;
    }

    /**
     * @return the taxCategory
     */
    public String getTaxCategory() {
        return taxCategory;
    }

    /**
     * @param taxCategory the taxCategory to set
     */
    public void setTaxCategory(String taxCategory) {
        this.taxCategory = taxCategory;
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
     * @return the payerId
     */
    public String getPayerId() {
        return payerId;
    }

    /**
     * @param payerId the payerId to set
     */
    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    /**
     * @return the nameexp48
     */
    public String getNameexp48() {
        return nameexp48;
    }

    /**
     * @param nameexp48 the nameexp48 to set
     */
    public void setNameexp48(String nameexp48) {
        this.nameexp48 = nameexp48;
    }

    /**
     * @return the bankPaymentCode
     */
    public String getBankPaymentCode() {
        return bankPaymentCode;
    }

    /**
     * @param bankPaymentCode the bankPaymentCode to set
     */
    public void setBankPaymentCode(String bankPaymentCode) {
        this.bankPaymentCode = bankPaymentCode;
    }

    /**
     * @return the parcelIdn
     */
    public String getParcelIdn() {
        return parcelIdn;
    }

    /**
     * @param parcelIdn the parcelIdn to set
     */
    public void setParcelIdn(String parcelIdn) {
        this.parcelIdn = parcelIdn;
    }

    /**
     * @return the houseNo
     */
    public String getHouseNo() {
        return houseNo;
    }

    /**
     * @param houseNo the houseNo to set
     */
    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    /**
     * @return the plotNo
     */
    public String getPlotNo() {
        return plotNo;
    }

    /**
     * @param plotNo the plotNo to set
     */
    public void setPlotNo(String plotNo) {
        this.plotNo = plotNo;
    }

    /**
     * @return the ownerHouseNo
     */
    public String getOwnerHouseNo() {
        return ownerHouseNo;
    }

    /**
     * @param ownerHouseNo the ownerHouseNo to set
     */
    public void setOwnerHouseNo(String ownerHouseNo) {
        this.ownerHouseNo = ownerHouseNo;
    }

    /**
     * @return the ownerBlockNo
     */
    public String getOwnerBlockNo() {
        return ownerBlockNo;
    }

    /**
     * @param ownerBlockNo the ownerBlockNo to set
     */
    public void setOwnerBlockNo(String ownerBlockNo) {
        this.ownerBlockNo = ownerBlockNo;
    }

    /**
     * @return the ownerAddress
     */
    public String getOwnerAddress() {
        return ownerAddress;
    }

    /**
     * @param ownerAddress the ownerAddress to set
     */
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    /**
     * @return the usageDescription
     */
    public String getUsageDescription() {
        return usageDescription;
    }

    /**
     * @param usageDescription the usageDescription to set
     */
    public void setUsageDescription(String usageDescription) {
        this.usageDescription = usageDescription;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the nameexp47
     */
    public String getNameexp47() {
        return nameexp47;
    }

    /**
     * @param nameexp47 the nameexp47 to set
     */
    public void setNameexp47(String nameexp47) {
        this.nameexp47 = nameexp47;
    }

    /**
     * @return the nameexp51
     */
    public String getNameexp51() {
        return nameexp51;
    }

    /**
     * @param nameexp51 the nameexp51 to set
     */
    public void setNameexp51(String nameexp51) {
        this.nameexp51 = nameexp51;
    }

    /**
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * @param barcode the barcode to set
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * @return the paymentStatus
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @param paymentStatus the paymentStatus to set
     */
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * @return the consultantName
     */
    public String getConsultantName() {
        return consultantName;
    }

    /**
     * @param consultantName the consultantName to set
     */
    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    /**
     * @return the deliveryComments
     */
    public String getDeliveryComments() {
        return deliveryComments;
    }

    /**
     * @param deliveryComments the deliveryComments to set
     */
    public void setDeliveryComments(String deliveryComments) {
        this.deliveryComments = deliveryComments;
    }

    /**
     * @return the consultantId
     */
    public Long getConsultantId() {
        return consultantId;
    }

    /**
     * @param consultantId the consultantId to set
     */
    public void setConsultantId(Long consultantId) {
        this.consultantId = consultantId;
    }

    /**
     * @return the triggerDate
     */
    public Date getTriggerDate() {
        return triggerDate;
    }

    /**
     * @param triggerDate the triggerDate to set
     */
    public void setTriggerDate(Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    /**
     * @return the discDate
     */
    public Date getDiscDate() {
        return discDate;
    }

    /**
     * @param discDate the discDate to set
     */
    public void setDiscDate(Date discDate) {
        this.discDate = discDate;
    }

    /**
     * @return the fullpayDate
     */
    public Date getFullpayDate() {
        return fullpayDate;
    }

    /**
     * @param fullpayDate the fullpayDate to set
     */
    public void setFullpayDate(Date fullpayDate) {
        this.fullpayDate = fullpayDate;
    }

    /**
     * @return the startLatepaydate1
     */
    public Date getStartLatepaydate1() {
        return startLatepaydate1;
    }

    /**
     * @param startLatepaydate1 the startLatepaydate1 to set
     */
    public void setStartLatepaydate1(Date startLatepaydate1) {
        this.startLatepaydate1 = startLatepaydate1;
    }

    /**
     * @return the endLatepaydate1
     */
    public Date getEndLatepaydate1() {
        return endLatepaydate1;
    }

    /**
     * @param endLatepaydate1 the endLatepaydate1 to set
     */
    public void setEndLatepaydate1(Date endLatepaydate1) {
        this.endLatepaydate1 = endLatepaydate1;
    }

    /**
     * @return the startLatepaydate2
     */
    public Date getStartLatepaydate2() {
        return startLatepaydate2;
    }

    /**
     * @param startLatepaydate2 the startLatepaydate2 to set
     */
    public void setStartLatepaydate2(Date startLatepaydate2) {
        this.startLatepaydate2 = startLatepaydate2;
    }

    /**
     * @return the endLatepaydate2
     */
    public Date getEndLatepaydate2() {
        return endLatepaydate2;
    }

    /**
     * @param endLatepaydate2 the endLatepaydate2 to set
     */
    public void setEndLatepaydate2(Date endLatepaydate2) {
        this.endLatepaydate2 = endLatepaydate2;
    }

    /**
     * @return the startLatepaydate3
     */
    public Date getStartLatepaydate3() {
        return startLatepaydate3;
    }

    /**
     * @param startLatepaydate3 the startLatepaydate3 to set
     */
    public void setStartLatepaydate3(Date startLatepaydate3) {
        this.startLatepaydate3 = startLatepaydate3;
    }

    /**
     * @return the endLatepaydate3
     */
    public Date getEndLatepaydate3() {
        return endLatepaydate3;
    }

    /**
     * @param endLatepaydate3 the endLatepaydate3 to set
     */
    public void setEndLatepaydate3(Date endLatepaydate3) {
        this.endLatepaydate3 = endLatepaydate3;
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
     * @return the parcelSheetNumber
     */
    public String getParcelSheetNumber() {
        return parcelSheetNumber;
    }

    /**
     * @param parcelSheetNumber the parcelSheetNumber to set
     */
    public void setParcelSheetNumber(String parcelSheetNumber) {
        this.parcelSheetNumber = parcelSheetNumber;
    }

    /**
     * @return the flatNo
     */
    public String getFlatNo() {
        return flatNo;
    }

    /**
     * @param flatNo the flatNo to set
     */
    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    /**
     * @return the ownerFlatNo
     */
    public String getOwnerFlatNo() {
        return ownerFlatNo;
    }

    /**
     * @param ownerFlatNo the ownerFlatNo to set
     */
    public void setOwnerFlatNo(String ownerFlatNo) {
        this.ownerFlatNo = ownerFlatNo;
    }

    /**
     * @return the ownerDistrictName
     */
    public String getOwnerDistrictName() {
        return ownerDistrictName;
    }

    /**
     * @param ownerDistrictName the ownerDistrictName to set
     */
    public void setOwnerDistrictName(String ownerDistrictName) {
        this.ownerDistrictName = ownerDistrictName;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the nameexp49
     */
    public String getNameexp49() {
        return nameexp49;
    }

    /**
     * @param nameexp49 the nameexp49 to set
     */
    public void setNameexp49(String nameexp49) {
        this.nameexp49 = nameexp49;
    }

    /**
     * @return the nameexp50
     */
    public String getNameexp50() {
        return nameexp50;
    }

    /**
     * @param nameexp50 the nameexp50 to set
     */
    public void setNameexp50(String nameexp50) {
        this.nameexp50 = nameexp50;
    }

    /**
     * @return the millRate
     */
    public BigDecimal getMillRate() {
        return millRate;
    }

    /**
     * @param millRate the millRate to set
     */
    public void setMillRate(BigDecimal millRate) {
        this.millRate = millRate;
    }

    /**
     * @return the assessedValue
     */
    public BigDecimal getAssessedValue() {
        return assessedValue;
    }

    /**
     * @param assessedValue the assessedValue to set
     */
    public void setAssessedValue(BigDecimal assessedValue) {
        this.assessedValue = assessedValue;
    }

    /**
     * @return the balanceCf
     */
    public BigDecimal getBalanceCf() {
        return balanceCf;
    }

    /**
     * @param balanceCf the balanceCf to set
     */
    public void setBalanceCf(BigDecimal balanceCf) {
        this.balanceCf = balanceCf;
    }

    /**
     * @return the luc
     */
    public BigDecimal getLuc() {
        return luc;
    }

    /**
     * @param luc the luc to set
     */
    public void setLuc(BigDecimal luc) {
        this.luc = luc;
    }

    /**
     * @return the amountDue
     */
    public BigDecimal getAmountDue() {
        return amountDue;
    }

    /**
     * @param amountDue the amountDue to set
     */
    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    /**
     * @return the discAmount
     */
    public BigDecimal getDiscAmount() {
        return discAmount;
    }

    /**
     * @param discAmount the discAmount to set
     */
    public void setDiscAmount(BigDecimal discAmount) {
        this.discAmount = discAmount;
    }

    /**
     * @return the amtLatepay1
     */
    public BigDecimal getAmtLatepay1() {
        return amtLatepay1;
    }

    /**
     * @param amtLatepay1 the amtLatepay1 to set
     */
    public void setAmtLatepay1(BigDecimal amtLatepay1) {
        this.amtLatepay1 = amtLatepay1;
    }

    /**
     * @return the amtLatepay2
     */
    public BigDecimal getAmtLatepay2() {
        return amtLatepay2;
    }

    /**
     * @param amtLatepay2 the amtLatepay2 to set
     */
    public void setAmtLatepay2(BigDecimal amtLatepay2) {
        this.amtLatepay2 = amtLatepay2;
    }

    /**
     * @return the amtLatepay3
     */
    public BigDecimal getAmtLatepay3() {
        return amtLatepay3;
    }

    /**
     * @param amtLatepay3 the amtLatepay3 to set
     */
    public void setAmtLatepay3(BigDecimal amtLatepay3) {
        this.amtLatepay3 = amtLatepay3;
    }

    /**
     * @return the totalAmountPaid
     */
    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    /**
     * @param totalAmountPaid the totalAmountPaid to set
     */
    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    /**
     * @return the nextYrBf
     */
    public BigDecimal getNextYrBf() {
        return nextYrBf;
    }

    /**
     * @param nextYrBf the nextYrBf to set
     */
    public void setNextYrBf(BigDecimal nextYrBf) {
        this.nextYrBf = nextYrBf;
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

    /**
     * @return the hasMatchPayment
     */
    public Boolean getHasMatchPayment() {
        return hasMatchPayment;
    }

    /**
     * @param hasMatchPayment the hasMatchPayment to set
     */
    public void setHasMatchPayment(Boolean hasMatchPayment) {
        this.hasMatchPayment = hasMatchPayment;
    }

    public String getJsonMessage() {
        return jsonMessage;
    }

    public void setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the isStructureChanged
     */
    public Boolean getIsStructureChanged() {
        return isStructureChanged;
    }

    /**
     * @param isStructureChanged the isStructureChanged to set
     */
    public void setIsStructureChanged(Boolean isStructureChanged) {
        this.isStructureChanged = isStructureChanged;
    }
}
