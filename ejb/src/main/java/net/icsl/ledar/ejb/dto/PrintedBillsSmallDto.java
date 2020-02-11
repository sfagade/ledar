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
public class PrintedBillsSmallDto {

    private String propertyIdn, lga;
    private Long id;
    private Date created;
    private String houseAddress, streetName, districtName;

    public PrintedBillsSmallDto() {
    }

    public PrintedBillsSmallDto(Long bill_id, String propertyIdn, String lga, String address_, Date created) {
        this.propertyIdn = propertyIdn;
        this.id = bill_id;
        this.created = created;
        this.lga = lga;
        this.houseAddress = address_;
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
}
