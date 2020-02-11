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
public enum ValuationStatusEnum {

    PENDING("PENDING VALUATION"), 
    SENT("SENT FOR VALUATION"), 
    RETURNED("REQUIRE MODIFICATIONS"), 
    PROCESSED("APPROVED AND PROCESSED"),
    READYINITS("READY TO PUSH TO INITS"),
    PARTIAL("PARTIAL PUSH"),
    INITSPUSHED("PUSHED TO INITS");

    private final String qualification;

    private ValuationStatusEnum(String qualification) {
        this.qualification = qualification;
    }

    @Override
    public String toString() {

        return this.qualification;
    }
}
