package net.icsl.ledar.ejb.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author sfagade Jan 19, 2017
 */
public class BillsPaymentsDto {

    private String vpayerName, vpayerId, entryId, appliedDate, loginName, loginId;
    private Date entryDate, valueDate, sysDate, triggerDate, created, modified;
    private BigDecimal crAmount;
    private String receiptBir, dSlipRef, agencyRef, revCode, transRef, assessRef, referenceStr, shortName, merchantName, lgaName, districtName, addressToUse, payerAddress, payerPhoneNo;
    private String lrcPin, pptAddress, lgaLocation, billProperty, bankCode, bankNote, bankAcct, paymentChannel;
    private Boolean isProcessed, isDuplicate;
    private Long paymentId;

    public BillsPaymentsDto() {

    }

    public BillsPaymentsDto(String vPayerName, String vPayerId, String entryId, String appliedDate, String loginName, String loginId, Date entryDate, Date valueDate, Date sysDate, Date triggerDate,
            Date created, Date modified, BigDecimal crAmount, String receiptBir, String dSlipRef, String agencyRef, String revCode, String transRef, String assessRef, String referenceStr,
            String shortName, String merchantName, String lgaName, String districtName, String addressToUse, String payerAddress, String payerPhoneNo, String lrcPin, String pptAddress,
            String lgaLocation, String billProperty, String bankCode, Boolean isProcessed, Boolean isDuplicate, Long paymentId) {
        this.vpayerName = vPayerName;
        this.vpayerId = vPayerId;
        this.entryId = entryId;
        this.appliedDate = appliedDate;
        this.loginName = loginName;
        this.loginId = loginId;
        this.entryDate = entryDate;
        this.valueDate = valueDate;
        this.sysDate = sysDate;
        this.triggerDate = triggerDate;
        this.created = created;
        this.modified = modified;
        this.crAmount = crAmount;
        this.receiptBir = receiptBir;
        this.dSlipRef = dSlipRef;
        this.agencyRef = agencyRef;
        this.revCode = revCode;
        this.transRef = transRef;
        this.assessRef = assessRef;
        this.referenceStr = referenceStr;
        this.shortName = shortName;
        this.merchantName = merchantName;
        this.lgaName = lgaName;
        this.districtName = districtName;
        this.addressToUse = addressToUse;
        this.payerAddress = payerAddress;
        this.payerPhoneNo = payerPhoneNo;
        this.lrcPin = lrcPin;
        this.pptAddress = pptAddress;
        this.lgaLocation = lgaLocation;
        this.billProperty = billProperty;
        this.bankCode = bankCode;
        this.isProcessed = isProcessed;
        this.isDuplicate = isDuplicate;
        this.paymentId = paymentId;
    }

    public BillsPaymentsDto(Long pay_id, String bank_code, Date pay_day, String payer_id, String full_name, BigDecimal amount, String slip, String trans_ref, String entry_id, String applied_date, 
            String aganecy_ref, String rev_code, String bank_note, String bank_acct, String bank_name, String channel, Date created_, Date modified_) {

        this.paymentId = pay_id;
        this.bankCode = bank_code;
        this.valueDate = pay_day;
        this.vpayerId = payer_id;
        this.vpayerName = full_name;
        this.crAmount = amount;
        this.dSlipRef = slip;
        this.transRef = trans_ref;
        this.entryId = entry_id;
        this.appliedDate = applied_date;
        this.agencyRef = aganecy_ref;
        this.revCode = rev_code;
        this.bankAcct = bank_acct;
        this.bankNote = bank_note;
        this.shortName = bank_name;
        this.paymentChannel = channel;
        this.created = created_;
        this.modified = modified_;
    }

    /**
     * @return the entryId
     */
    public String getEntryId() {
        return entryId;
    }

    /**
     * @param entryId the entryId to set
     */
    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    /**
     * @return the appliedDate
     */
    public String getAppliedDate() {
        return appliedDate;
    }

    /**
     * @param appliedDate the appliedDate to set
     */
    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the loginId
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * @param loginId the loginId to set
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * @return the entryDate
     */
    public Date getEntryDate() {
        return entryDate;
    }

    /**
     * @param entryDate the entryDate to set
     */
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * @return the valueDate
     */
    public Date getValueDate() {
        return valueDate;
    }

    /**
     * @param valueDate the valueDate to set
     */
    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    /**
     * @return the sysDate
     */
    public Date getSysDate() {
        return sysDate;
    }

    /**
     * @param sysDate the sysDate to set
     */
    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
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
     * @return the crAmount
     */
    public BigDecimal getCrAmount() {
        return crAmount;
    }

    /**
     * @param crAmount the crAmount to set
     */
    public void setCrAmount(BigDecimal crAmount) {
        this.crAmount = crAmount;
    }

    /**
     * @return the receiptBir
     */
    public String getReceiptBir() {
        return receiptBir;
    }

    /**
     * @param receiptBir the receiptBir to set
     */
    public void setReceiptBir(String receiptBir) {
        this.receiptBir = receiptBir;
    }

    /**
     * @return the dSlipRef
     */
    public String getdSlipRef() {
        return dSlipRef;
    }

    /**
     * @param dSlipRef the dSlipRef to set
     */
    public void setdSlipRef(String dSlipRef) {
        this.dSlipRef = dSlipRef;
    }

    /**
     * @return the agencyRef
     */
    public String getAgencyRef() {
        return agencyRef;
    }

    /**
     * @param agencyRef the agencyRef to set
     */
    public void setAgencyRef(String agencyRef) {
        this.agencyRef = agencyRef;
    }

    /**
     * @return the revCode
     */
    public String getRevCode() {
        return revCode;
    }

    /**
     * @param revCode the revCode to set
     */
    public void setRevCode(String revCode) {
        this.revCode = revCode;
    }

    /**
     * @return the transRef
     */
    public String getTransRef() {
        return transRef;
    }

    /**
     * @param transRef the transRef to set
     */
    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }

    /**
     * @return the assessRef
     */
    public String getAssessRef() {
        return assessRef;
    }

    /**
     * @param assessRef the assessRef to set
     */
    public void setAssessRef(String assessRef) {
        this.assessRef = assessRef;
    }

    /**
     * @return the referenceStr
     */
    public String getReferenceStr() {
        return referenceStr;
    }

    /**
     * @param referenceStr the referenceStr to set
     */
    public void setReferenceStr(String referenceStr) {
        this.referenceStr = referenceStr;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the merchantName
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * @param merchantName the merchantName to set
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    /**
     * @return the lgaName
     */
    public String getLgaName() {
        return lgaName;
    }

    /**
     * @param lgaName the lgaName to set
     */
    public void setLgaName(String lgaName) {
        this.lgaName = lgaName;
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
     * @return the addressToUse
     */
    public String getAddressToUse() {
        return addressToUse;
    }

    /**
     * @param addressToUse the addressToUse to set
     */
    public void setAddressToUse(String addressToUse) {
        this.addressToUse = addressToUse;
    }

    /**
     * @return the payerAddress
     */
    public String getPayerAddress() {
        return payerAddress;
    }

    /**
     * @param payerAddress the payerAddress to set
     */
    public void setPayerAddress(String payerAddress) {
        this.payerAddress = payerAddress;
    }

    /**
     * @return the payerPhoneNo
     */
    public String getPayerPhoneNo() {
        return payerPhoneNo;
    }

    /**
     * @param payerPhoneNo the payerPhoneNo to set
     */
    public void setPayerPhoneNo(String payerPhoneNo) {
        this.payerPhoneNo = payerPhoneNo;
    }

    /**
     * @return the lrcPin
     */
    public String getLrcPin() {
        return lrcPin;
    }

    /**
     * @param lrcPin the lrcPin to set
     */
    public void setLrcPin(String lrcPin) {
        this.lrcPin = lrcPin;
    }

    /**
     * @return the pptAddress
     */
    public String getPptAddress() {
        return pptAddress;
    }

    /**
     * @param pptAddress the pptAddress to set
     */
    public void setPptAddress(String pptAddress) {
        this.pptAddress = pptAddress;
    }

    /**
     * @return the lgaLocation
     */
    public String getLgaLocation() {
        return lgaLocation;
    }

    /**
     * @param lgaLocation the lgaLocation to set
     */
    public void setLgaLocation(String lgaLocation) {
        this.lgaLocation = lgaLocation;
    }

    /**
     * @return the billProperty
     */
    public String getBillProperty() {
        return billProperty;
    }

    /**
     * @param billProperty the billProperty to set
     */
    public void setBillProperty(String billProperty) {
        this.billProperty = billProperty;
    }

    /**
     * @return the bankCode
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * @param bankCode the bankCode to set
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * @return the isProcessed
     */
    public Boolean getIsProcessed() {
        return isProcessed;
    }

    /**
     * @param isProcessed the isProcessed to set
     */
    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    /**
     * @return the isDuplicate
     */
    public Boolean getIsDuplicate() {
        return isDuplicate;
    }

    /**
     * @param isDuplicate the isDuplicate to set
     */
    public void setIsDuplicate(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
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
     * @return the paymentId
     */
    public Long getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getBankNote() {
        return bankNote;
    }

    public void setBankNote(String bankNote) {
        this.bankNote = bankNote;
    }

    public String getBankAcct() {
        return bankAcct;
    }

    public void setBankAcct(String bankAcct) {
        this.bankAcct = bankAcct;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    /**
     * @return the vpayerName
     */
    public String getVpayerName() {
        return vpayerName;
    }

    /**
     * @param vpayerName the vpayerName to set
     */
    public void setVpayerName(String vpayerName) {
        this.vpayerName = vpayerName;
    }

    /**
     * @return the vpayerId
     */
    public String getVpayerId() {
        return vpayerId;
    }

    /**
     * @param vpayerId the vpayerId to set
     */
    public void setVpayerId(String vpayerId) {
        this.vpayerId = vpayerId;
    }

}
