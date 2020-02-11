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
public enum PaymentStatusEnum {

    NOTPAID("NOT PAID"),
    PARTIAL("PARTIAL PAYMENT"),
    INFOERROR("MISSING PAYMENT"),
    MATCHDISC("MATCHED DISCOUNT AMOUNT"),
    MATCHFULLAMT("MATCHED FULL PAYMENT AMOUNT"),
    MATCHLATEAMT("MATCHED LATE PAYMENT AMOUNT"),
    PAID("FULLY PAID"),
    MATCHSINGLE("MATCHED FULLY WITH SINGLE PAYMENT"),
    MACTHSETZERO("MATCHED WITH SINGLE PAYMENT SET TO ZERO"),
    NEGATIVEYEAR("MATCHED WITH NEGATIVE VALUE FOR NEXT YEAR"),
    MATCHEDOWING("MATCHED AND STILL OWING"),
    MATCHBILL("FOUND PAYMENT BILL"),
    NOMATCHBILL("COULD NOT FIND BILL");

    private final String qualification;

    private PaymentStatusEnum(String qualification) {
        this.qualification = qualification;
    }

    @Override
    public String toString() {

        return this.qualification;
    }
}
