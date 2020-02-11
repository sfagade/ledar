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
public enum EducationalQualifications {

    PRIMARY("PRIMARY"),
    SECONDARY("SECONDARY"),
    BACHELORDEGREE("BACHELOR'S DEGREE"),
    OTHERS("OTHERS");

    private final String qualification;

    private EducationalQualifications(String qualification) {
        this.qualification = qualification;
    }

    @Override
    public String toString() {
        switch (this) {
            case BACHELORDEGREE:
                return "BACHELOR'S DEGREE";
            default:
                return this.qualification;
        }
    }
}
