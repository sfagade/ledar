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
public enum UserActitiyName {

    WEBLOGIN("WEB LOGIN"),
    MOBILELOGIN("MOBILE APP LOGIN"),
    SAVEDPOD("SAVED POD"),
    SAVEDENUMERATION("SAVED ENUMERATION"),
    SCANNEDPOD("SCANNED POD FILE"),
    SAVEDTEMPBILL("SAVED NEW BILL INFORMATION"),
    NEWSTREET("CREATED NEW STREET INFORMATION"),
    ASSIGNEDDELIVERY("ASSIGNED DELIVERY TO FIELD OFFICER"),
    SAVEDCONTRACTOR("SAVED NEW CONTRACTOR INFORMATION"),
    SAVEDDISTRICT("SAVED NEW DISTRICT INFORMATION"),
    UPDATEDPROPERTY("EDIT PROPERTY INFORMATION"),
    DELETEDPROPERTY("DELETED PROPERTY INFORMATION"),
    UPDATEDPROPERTYSTATUS("UPDATED PROPERTY VALUATION STATUS"),
    UPDATEDLOGINSTATUS("UPDATED USER LOGIN ACTIVE STATUS"),
    UPDATEDUSERINFO("UPDATED USER PERSONAL INFORMATION"),
    UPDATEDDEVICEINFO("UPDATED REGISTERED DEVICE INFORMATION"),
    UPDATEBILDINGINFO("UPDATED BUILDING INFORMATION"),
    SAVEDBARELAND("SAVED NEW BARELAND INFORMATION"),
    DELETESTREET("DELETED STREET INFORMATION"),
    DELETEBARELAND("DELETED BARELAND INFORMATION"),
    SAVEVISIT("SAVED NEW VISITOR INFORMATION");

    private final String userActivityName;

    private UserActitiyName(String qualification) {
        this.userActivityName = qualification;
    }

    @Override
    public String toString() {

        return this.userActivityName;
    }
}
