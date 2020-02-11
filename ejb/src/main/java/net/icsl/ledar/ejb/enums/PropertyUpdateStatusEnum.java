/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.enums;

import java.io.Serializable;

/**
 *
 * @author sfagade
 */
public enum PropertyUpdateStatusEnum implements Serializable {

    DUPLICATE("DUPLICATE PROPERTY"),
    DEMOLISHED("PROPERTY DEMOLISHED"),
    USAGECHANGED("PROPERTY USAGE CHANGED"),
    STRUCTURECHAGED("PROPERTY STRUCTURE CHANGED"),
    STREETGAPINIT("PENDING REVIEW"),
    DUPLICATEBILL("DUPLICATE BILL");

    private final String propertyStatus;

    private PropertyUpdateStatusEnum(String qualification) {
        this.propertyStatus = qualification;
    }

    @Override
    public String toString() {

        return this.propertyStatus;
    }

    public String getStatus() {
        return this.toString();
    }
}
