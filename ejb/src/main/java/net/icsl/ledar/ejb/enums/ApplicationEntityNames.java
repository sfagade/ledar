/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.enums;

/**
 *
 * @author sfagade
 */
public enum ApplicationEntityNames {

    PROVEOFDELIVERY("PROVE OF DELIVERY"),
    PROPERTYBILLS("PROPERTY BILLS"),
    PERSON("PERSON"),
    DELIVERTYFILES("DELIVERY FILES"),
    WARDLANDPROPERTIES("ENUMERATION"),
    TEMPPROPERTIES("TEMP PROPERTIES"),
    WARDSTREET("WARD STREETS"),
    LOGINDETAILS("LOGIN INFORMATION"),
    ORGANIZATION("ORGANIZATION"),
    LCDAWARDS("LGA DISTRICT"),
    BARELAND("BARE LAND"),
    REGISTEREDDEVICE("REGISTERED DEBVICES"),
    BILLSDELIVERY("BILLS DELIVERY FILES"),
    PROPERTYBUILDING("PROPERTY BUILDING"),
    VISITOR("VISITORS");

    private final String qualification;

    private ApplicationEntityNames(String qualification) {
        this.qualification = qualification;
    }

    @Override
    public String toString() {

        return this.qualification;

    }
}
