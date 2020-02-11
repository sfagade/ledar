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
public enum UntarredRoads {

    MOTORABLE("MOTORABLE"), NOTMOTORABLE("NOTMOTORABLE");

    private final String unstarredRoad;

    private UntarredRoads(String qualification) {
        this.unstarredRoad = qualification;
    }

    @Override
    public String toString() {
        switch (this) {
            case NOTMOTORABLE:
                return "NOT MOTORABLE";
            default:
                return this.unstarredRoad;
        }
    }

}
