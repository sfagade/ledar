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
public enum EmploymentStatus {
 
    EMPLOYED("EMPLOYED"), SELFEMPLOYED("SELFEMPLOYED"), RETIRED("RETIRED"), OTHERS("OTHERS");
    
    private final String empStatus;

    private EmploymentStatus(String qualification) {
        this.empStatus = qualification;
    }
    
    @Override
    public String toString() {
        switch (this) {
            case SELFEMPLOYED:
                return "SELF EMPLOYED";
            default:
                return this.empStatus;
        }
    }
}
