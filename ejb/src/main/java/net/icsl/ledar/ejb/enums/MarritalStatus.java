package net.icsl.ledar.ejb.enums;

public enum MarritalStatus {

    DIVORCED("Dv"), MARRIED("Ma"), SINGLE("Sg"), WIDOWED("Wd"), WIDOWER("We");

    private String marritalCode;

    private MarritalStatus(String s) {
        marritalCode = s;
    }

    public String getMarritalCode() {
        return marritalCode;
    }
}
