/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.dto;

import java.util.Date;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;

/**
 *
 * @author sfagade
 * @date May 28, 2017
 */
public class GenericModelDto {

    private Date dateCaptured, deliveryDate, created;
    private Long recordCount, modelId;
    private Logindetails logindetail;
    private PrintedBills printedBillId;
    private String gpsLongitude, gpsLatitude;

    public GenericModelDto(Date dateCaptured, Long recordCount, Logindetails logindetail) {
        this.dateCaptured = dateCaptured;
        this.recordCount = recordCount;
        this.logindetail = logindetail;
    }

    public GenericModelDto(Long modelId, Logindetails logindetail, PrintedBills printedBillId, String gpsLongitude, String gpsLatitude) {
        this.modelId = modelId;
        this.logindetail = logindetail;
        this.printedBillId = printedBillId;
        this.gpsLongitude = gpsLongitude;
        this.gpsLatitude = gpsLatitude;
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
     * @return the recordCount
     */
    public Long getRecordCount() {
        return recordCount;
    }

    /**
     * @param recordCount the recordCount to set
     */
    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @return the modelId
     */
    public Long getModelId() {
        return modelId;
    }

    /**
     * @param modelId the modelId to set
     */
    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    /**
     * @return the logindetail
     */
    public Logindetails getLogindetail() {
        return logindetail;
    }

    /**
     * @param logindetail the logindetail to set
     */
    public void setLogindetail(Logindetails logindetail) {
        this.logindetail = logindetail;
    }

    /**
     * @return the printedBillId
     */
    public PrintedBills getPrintedBillId() {
        return printedBillId;
    }

    /**
     * @param printedBillId the printedBillId to set
     */
    public void setPrintedBillId(PrintedBills printedBillId) {
        this.printedBillId = printedBillId;
    }

    /**
     * @return the gpsLongitude
     */
    public String getGpsLongitude() {
        return gpsLongitude;
    }

    /**
     * @param gpsLongitude the gpsLongitude to set
     */
    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    /**
     * @return the gpsLatitude
     */
    public String getGpsLatitude() {
        return gpsLatitude;
    }

    /**
     * @param gpsLatitude the gpsLatitude to set
     */
    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
