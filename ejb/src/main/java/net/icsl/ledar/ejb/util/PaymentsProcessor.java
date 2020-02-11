/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.mail.MessagingException;
import net.icsl.ledar.ejb.enums.PaymentStatusEnum;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;

/**
 *
 * @author sfagade
 */
@Stateless
@LocalBean
public class PaymentsProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmailSender emailSender;
    @Inject
    private PrintedBillsService billService;
    @Inject
    private RegisteredUsersDataService registeredService;

    /**
     * This method is used to calculate payment on a bill, it determines if the
     * bill has been fully paid for and also what kind of BF the property should
     * have going into next year. It first fetches all the payments recorded for
     * a bill and then calculate everything putting into consideration last time
     * of payment and discount and all
     *
     * @param user_id
     * @param consultant_id
     * @param lga_name
     * @param district
     * @Authur sFagade
     */
    @Asynchronous
    public void processBillsPayment(Long user_id, Long consultant_id, String lga_name, String district) {

        Map<String, String> criteria = new HashMap<>();
        if (consultant_id != null) {
            criteria.put("contractor_id", consultant_id.toString());
        }
        if (lga_name != null) {
            criteria.put("lga_name", lga_name);
        }
        if (district != null) {
            criteria.put("district", district);
        }
        criteria.put("has_payment", "true");

        List<PrintedBills> paymentBills = billService.fetchAllBillsForProcessing(criteria); //get all the bills that have payments attached to them in  the system
        List<BillPayments> payments;
        Long bills_count, payment_count, bf_count, credit_count, cleared_bf_count;
        BigDecimal totalPaid, balanceBf;
        Date last_payment_date;
        Logindetails logindetail;
        BigDecimal compareAmount = new BigDecimal(1000), negativeAmount = new BigDecimal(-1);

        if ((paymentBills != null) && (paymentBills.size() > 0)) {
            bills_count = Long.valueOf(paymentBills.size());
            payment_count = 0L;
            bf_count = 0L;
            credit_count = 0L;
            cleared_bf_count = 0L;
            for (PrintedBills bill : paymentBills) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Processing Payment: {0} -- {1} -- {2}", new Object[]{bill.getPropertyIdn(), bill.getBankPaymentCode(),
                    bill.getPayerId()});
                totalPaid = BigDecimal.ZERO;
                balanceBf = BigDecimal.ZERO;
                last_payment_date = null;
                payments = billService.fetchAllBillsPaymentByBillId(bill.getId(), Boolean.FALSE); //get all the payments attached to a bill except for ones marked duplicate
                if ((payments != null) && (payments.size() > 0)) {
                    payment_count += Long.valueOf(payments.size());
                    if (payments.size() == 1) { //only one payment has been made on this bill,
                        totalPaid = payments.get(0).getCrAmount();
                        if (!payments.get(0).getSysDate().after(bill.getDiscDate())) {
                            balanceBf = bill.getDiscAmount().subtract(totalPaid); //subtract amount paid from discount amount
                            bill.setReconciliationStatus(PaymentStatusEnum.MATCHDISC.toString());
                        } else if (bill.getDeliveryDate() != null) { //we know the date the bill was delivered, so we can validate which payment user should be paying
                            if (payments.get(0).getSysDate().before(bill.getDeliveryDate())) { //payment is before bills discount date
                                balanceBf = bill.getDiscAmount().subtract(totalPaid); //subtract amount paid from discount amount
                                bill.setReconciliationStatus(PaymentStatusEnum.MATCHDISC.toString());
                            } else if (payments.get(0).getSysDate().before(bill.getStartLatepaydate1())) { //payment was made before first last payment date
                                balanceBf = bill.getAmountDue().subtract(totalPaid);
                                bill.setReconciliationStatus(PaymentStatusEnum.MATCHFULLAMT.toString());
                            } else if (payments.get(0).getSysDate().before(bill.getStartLatepaydate2())) { //payment was made after start of first late payment but before second late payment
                                balanceBf = bill.getAmtLatepay1().subtract(totalPaid);
                                bill.setReconciliationStatus(PaymentStatusEnum.MATCHLATEAMT.toString());
                            } else if (payments.get(0).getSysDate().before(bill.getStartLatepaydate3())) { //
                                balanceBf = bill.getAmtLatepay2().subtract(totalPaid);
                                bill.setReconciliationStatus(PaymentStatusEnum.MATCHLATEAMT.toString());
                            } else { //payment has entered third late payment
                                balanceBf = bill.getAmtLatepay3().subtract(totalPaid);
                                bill.setReconciliationStatus(PaymentStatusEnum.MATCHLATEAMT.toString());
                            }
                        } else if (payments.get(0).getCrAmount().compareTo(bill.getDiscAmount()) == 0 || (payments.get(0).getCrAmount().compareTo(bill.getAmountDue()) == 0)
                                || (payments.get(0).getCrAmount().compareTo(bill.getAmtLatepay1()) == 0) || (payments.get(0).getCrAmount().compareTo(bill.getAmtLatepay2()) == 0)
                                || (payments.get(0).getCrAmount().compareTo(bill.getAmtLatepay3()) == 0)) { //validate against discount amount
                            balanceBf = BigDecimal.ZERO;
                            bill.setReconciliationStatus(PaymentStatusEnum.MATCHSINGLE.toString());
                        } else { //PO has paid amount that is not an exact match with any category of charge
                            balanceBf = bill.getDiscAmount().subtract(totalPaid); //subtract amount paid from discount amount
                            if (balanceBf.compareTo(compareAmount) <= 0) { //balance carried forward is less than 1001
                                balanceBf = BigDecimal.ZERO;
                                bill.setReconciliationStatus(PaymentStatusEnum.MACTHSETZERO.toString());
                            } else if (balanceBf.compareTo(negativeAmount) <= -1) {
                                bill.setReconciliationStatus(PaymentStatusEnum.NEGATIVEYEAR.toString());
                            } else {
                                bill.setReconciliationStatus(PaymentStatusEnum.MATCHEDOWING.toString());
                            }
                        }

                        payments.get(0).setIsProcessed(Boolean.TRUE);
                        billService.saveBillPaymentInformation(payments.get(0), null, Boolean.FALSE);
                        bill.setTotalAmountPaid(payments.get(0).getCrAmount());
                    } else {
                        last_payment_date = payments.get(0).getSysDate();
                        for (BillPayments pay : payments) {
                            totalPaid.add(pay.getCrAmount());
                            pay.setIsProcessed(Boolean.TRUE);
                            billService.saveBillPaymentInformation(pay, null, Boolean.FALSE);
                        }

                        if (totalPaid.compareTo(bill.getDiscAmount()) == 0 || (totalPaid.compareTo(bill.getAmountDue()) == 0) || (totalPaid.compareTo(bill.getAmtLatepay1()) == 0)
                                || (totalPaid.compareTo(bill.getAmtLatepay2()) == 0) || (totalPaid.compareTo(bill.getAmtLatepay3()) == 0)) { //validate against category payment
                            balanceBf = BigDecimal.ZERO;

                        } //TODO I need to validate this section to reflect properly incase of patial penalties and discount
                        /**
                         * else if
                         * (!last_payment_date.after(bill.getDiscDate())) {
                         * balanceBf = bill.getDiscAmount().subtract(totalPaid);
                         * }
                         */
                        else {
                            balanceBf = bill.getAmountDue().subtract(totalPaid);
                        }
                        BigDecimal bigBigDecimal = new BigDecimal(2000);
                        if (balanceBf.compareTo(BigDecimal.ZERO) < 0) { //bf is less than 2000
                            credit_count += 1L;
                            balanceBf = BigDecimal.ZERO;
                        } else if (balanceBf.compareTo(bigBigDecimal) == 0) { //balance carried forward is 2000
                            cleared_bf_count += 1L;
                            //balanceBf = BigDecimal.ZERO;
                        } else if (balanceBf.compareTo(bigBigDecimal) > 0) { //balance carried forward greater than 2,000
                            bf_count += 1L;
                        }

                        bill.setTotalAmountPaid(totalPaid);
                    }
                } else {
                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "Bill with wrong Boolean: {0}", bill.getBankPaymentCode());
                }

                if ((balanceBf.compareTo(BigDecimal.ZERO) == 0) || (balanceBf.compareTo(BigDecimal.ZERO) > 0)) { //PO has paid completely or more than Amount charged
                    bill.setPaymentStatus(PaymentStatusEnum.PAID.toString());
                } else if (balanceBf.compareTo(BigDecimal.ZERO) < 0) {
                    bill.setPaymentStatus(PaymentStatusEnum.PARTIAL.toString());
                }
                bill.setNextYrBf(balanceBf);
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Next year''s balance carried forward: {0} -- {1} -- {2}", new Object[]{balanceBf, bills_count, payment_count});

                try {
                    billService.saveUpdateBillInformation(bill, null); //update the bill info
                } catch (Exception ex) {
                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "Failed to update bill info: " + bill.getId(), ex);
                }
            }
            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Finished processing payment");
            logindetail = registeredService.find(user_id);

            String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "About to send email to: {0}", to_address[0]);

            try {
                String email_msg = "<div>" + bills_count + " Bills processed, from a total of " + payment_count + " payments.</div><div>" + credit_count + " properties have credits going into next"
                        + " year.</div><div>" + cleared_bf_count + " properties had their balance set to zero after calculations.</div><div>" + bf_count + " properties have debt going into next year."
                        + "</div> <div>Processing complete\n</div>";
                String subject = "Bills Payment Processed";
                emailSender.sendPlainEmailMessage(email_msg, subject, to_address);
            } catch (MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
