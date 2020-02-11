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
public enum WaterSupply {
    
    LSWC("LSWC"), PRIVATEBOREHOLES("PRIVATE BOREHOLE"), NONE("NONE");
    
    private final String waterSupply;

    private WaterSupply(String qualification) {
        this.waterSupply = qualification;
    }
    
    @Override
    public String toString() {
        switch (this) {
            case PRIVATEBOREHOLES:
                return "PRIVATE BOREHOLES";
            default:
                return this.waterSupply;
        }
    }
    
}
