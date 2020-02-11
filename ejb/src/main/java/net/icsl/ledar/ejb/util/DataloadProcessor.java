package net.icsl.ledar.ejb.util;

import java.io.*;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import net.icsl.ledar.ejb.enums.PaymentStatusEnum;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import org.apache.commons.lang3.text.WordUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author sfagade
 */
@Stateless
public class DataloadProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private PrintedBillsService billService;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private LandPropertiesDataService propertyService;
    @Inject
    private LcdaWardsDataServices lcdaService;

    @Inject
    DataProcessorUtil dataProcUtil;

    @Inject
    private EmailSender emailSender;

    final DateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//    private final DateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private final DateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

    @Asynchronous
    public void processDuplicateBills(List<Object[]> duplicateBillList, Long login_id) {

        String bank_code, billing_year;
        List<PrintedBills> printedBill;
        PrintedBills currentBill, nextBill;
        int counter, success_count;

        if (duplicateBillList != null && duplicateBillList.size() > 0 && login_id != null) {
            success_count = 0;
            for (Object[] obj : duplicateBillList) {
                bank_code = obj[0].toString();
                billing_year = obj[1].toString();

                printedBill = billService.fetchPaymentBills(bank_code, billing_year);
                if ((printedBill != null) && (printedBill.size() > 1)) { //this should always be the case
                    currentBill = printedBill.get(0);
                    for (counter = 1; counter < printedBill.size(); counter++) {
                        nextBill = printedBill.get(counter);
                        if (nextBill.getHasMatchPayment() && !currentBill.getHasMatchPayment()) { //we can't delete bill that has payment
                            currentBill.setIsDeleted(Boolean.TRUE);
                            currentBill.setIsDelivered(Boolean.FALSE);
                            billService.saveUpdateBillInformation(currentBill, null);
                            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "{0} Removed duplicate current bill: {1}", new Object[]{counter, currentBill.getPropertyIdn()});
                            success_count++;
                        } else if (!nextBill.getHasMatchPayment()) {
                            nextBill.setIsDeleted(Boolean.TRUE);
                            billService.saveUpdateBillInformation(nextBill, null);
                            success_count++;
                            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "{0} Removed duplicate next bill: {1}", new Object[]{counter, nextBill.getPropertyIdn()});
                        } else {
                            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "{0} No valid acton set: {1} -- {2}", new Object[]{counter, nextBill.getBankPaymentCode(), nextBill.getHasMatchPayment()});
                        }
                    }
                }
            }
            Logindetails logindetail = registeredService.find(login_id);
            String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
            String email_message = "Finished processing duplicate Bills " + success_count + " instances was successfully resolved. ";
            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Finished processing duplicate bills, about to send email");

            try {
                emailSender.sendPlainEmailMessage(email_message, "LEDAR Duplicate Process Report", to_address);
            } catch (MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "No data to process for duplicate bills");
        }
    }

    /**
     * This method is used to process duplicate payments in the system. All
     * duplicate payments are retrieved the all but one in the group is marked
     * duplicate.
     *
     * @param duplicatePaymentList
     * @param login_id
     * @Authur sfagade
     */
    @Asynchronous
    public void processDuplicatePayment(List<Object[]> duplicatePaymentList, Long login_id) {

        String payer_name, payer_id, bank;
        Date value_date;
        BigDecimal amount;
        DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        int counter, success_count = 0, false_count = 0, sql_error_count = 0;
        BillPayments currentPayment, nextPayment;

        List<BillPayments> billsPayments;

        if (duplicatePaymentList != null && duplicatePaymentList.size() > 0 && login_id != null) {
            for (Object[] obj : duplicatePaymentList) {
                payer_name = obj[0].toString();
                payer_id = obj[1].toString();
                bank = obj[4].toString();
                amount = new BigDecimal(obj[3].toString());

                try {
                    value_date = sqlFormat.parse(obj[2].toString());
                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "About to fetch duplicates: {0} - {1} - {2} - {3} - {4}", new Object[]{payer_name, payer_id, bank, amount, obj[2]});
                    billsPayments = billService.fetchDuplicateRecord(payer_id, payer_name, amount, value_date);

                    if (billsPayments != null && billsPayments.size() > 1) { //this should always be the case here
                        currentPayment = billsPayments.get(0);
                        for (counter = 1; counter < billsPayments.size(); counter++) {
                            nextPayment = billsPayments.get(counter);
                            if (currentPayment.getValueDate().equals(nextPayment.getValueDate()) && currentPayment.getCrAmount().compareTo(nextPayment.getCrAmount()) == 0
                                    && currentPayment.getAssessRef().equals(nextPayment.getAssessRef()) && currentPayment.getShortName().equals(nextPayment.getShortName())
                                    && (currentPayment.getAddressToUse() != null && currentPayment.getAddressToUse().equals(nextPayment.getAddressToUse()))) {
                                //both payment are the same, I should mark one duplicate and leave the other
                                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Found match {0}", payer_id);
                                nextPayment.setIsDuplicate(Boolean.TRUE);
                                billService.saveBillPaymentInformation(nextPayment, null, Boolean.FALSE);
                                success_count++;
                            } else {
                                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "False claim encountered: {0} -- {1}", new Object[]{payer_name, amount});
                                false_count++;
                            }
                        }
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "Invalid SQL Date: " + obj[2], ex.getMessage());
                    sql_error_count++;
                }
            }

            Logindetails logindetail = registeredService.find(login_id);
            String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
            String email_message = "Finished processing duplicate payments " + success_count + " instances was successfully resolved. " + false_count + " had inconsistent record set. "
                    + sql_error_count + " had SQL date error";

            try {
                emailSender.sendPlainEmailMessage(email_message, "LEDAR Duplicate Process Report", to_address);
            } catch (MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "No data to process for duplicate payments");
        }
    }

    /**
     * This method is used to load payment information into the database, it has
     * to find the bill information before it can save the payment data. The
     * payments are just saved and processing is done by calling another method
     * using a different workflow
     *
     * @param file_path
     * @param file_name
     * @param year
     * @param logindetail_id
     * @param file_id
     * @Authur sFagade
     */
    @Asynchronous
    public void processBillsPaymentInformation(String file_path, String file_name, String year, Long logindetail_id, Long file_id) {

        HashMap<String, ArrayList<String>> paymentReport = new HashMap<>();
        paymentReport.put("errors", new ArrayList<String>());
        paymentReport.put("success", new ArrayList<String>());

        Logindetails logindetail;
        FileUploads paymentFile;
        List<PrintedBills> prBills;
        BillPayments currentPayment, existPayment;
        PrintedBills currentBill = null;

        Row currentRow;
        String payer_address, access_ref, payer_phone, lrc_pin, ppt_address, lga_location, lga_name, district_name, address_use, email_msg, temp_name;
        Date payment_date;
        long match_count = 0, dup_count = 0, not_found_count = 0, icsl_not_found = 0;
        BigDecimal amount_paid;
        Boolean found_correct, new_entity;

        File payment_file = new File(file_path + "/" + file_name);

        if (payment_file.exists()) { //we need to confirm that the file exist on the server
            try {

                XSSFWorkbook excel_sheet_file = new XSSFWorkbook(new FileInputStream(payment_file));
                XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);

                paymentFile = propertyService.findFileByUploadId(file_id);
                logindetail = registeredService.find(logindetail_id);
                temp_name = dataProcUtil.renameImageFile(file_name, "PROCESSING");

                for (int row = 1; row < sheet_.getPhysicalNumberOfRows(); row++) {
                    currentRow = sheet_.getRow(row);
                    if (currentRow != null && currentRow.getCell(5) != null) { //the value date cell should never be null
                        System.out.println(row + "-> cell is: " + currentRow.getCell(5).toString() + " -- " + currentRow.getCell(13).toString());

                        if (currentRow.getCell(25) == null) {
                            currentRow.createCell(25);
                        }

                        try {
                            payment_date = date_format.parse(currentRow.getCell(5).toString().trim());
                            found_correct = false;

                            if (currentRow.getCell(13).toString().contains(".")) {
                                access_ref = currentRow.getCell(13).toString().substring(0, currentRow.getCell(13).toString().indexOf("."));
                            } else {
                                access_ref = currentRow.getCell(13).toString();
                                if (access_ref.contains("-")) { //this will be the case if the pay report comes with the year prepended on the assess_ref column
                                    access_ref = access_ref.substring(access_ref.indexOf("-") + 1);
                                } else if (access_ref.contains("_")) {
                                    access_ref = access_ref.substring(0, access_ref.indexOf("_"));
                                }
                            }
                            prBills = billService.fetchPaymentBills(access_ref, year); //find the bills associated with this payment
                            amount_paid = dataProcUtil.parseStringToBigDecimal(currentRow.getCell(6).toString());

                            if (prBills != null && prBills.size() > 0) {
                                if (prBills.size() > 1) { //Bill exist but is duplicate, we can't continue
                                    for (PrintedBills bill : prBills) {
                                        if ((bill.getOwnerName() != null && !bill.getOwnerName().isEmpty()) && (bill.getStreetName() != null && !bill.getStreetName().isEmpty())
                                                && (bill.getDistrictName() != null && !bill.getDistrictName().isEmpty())) {
                                            found_correct = Boolean.TRUE;
                                            currentBill = bill;
                                        } else {
                                            bill.setIsDeleted(Boolean.TRUE); //set bill deleted bcos its duplicate
                                            billService.saveUpdateBillInformation(bill, null);
                                        }
                                    }
                                    if (!found_correct) {
                                        currentRow.getCell(25).setCellValue("DUPLICATE BILL");
                                        dup_count++;
                                        continue;
                                    }
                                } else {
                                    currentBill = prBills.get(0);
                                }

                            } else { //we will and find the bill with the property id, we're doing this for treating exception files
                                prBills = billService.fetchBillsWithSingleCriteria(access_ref, year);
                                if (prBills != null && prBills.size() > 0) {
                                    currentBill = prBills.get(0);
                                }
                            }
                            if (currentBill == null) { //we cannot find the bill with the bank-code, we have to search for it using other fields
                                //TODO I should only report Bill NOT FOUND after trying to use other field
                                if (currentRow.getCell(26) == null) {
                                    currentRow.createCell(26);
                                }

                                prBills = billService.fetchBillsByPayerIdOrAddress(currentRow.getCell(1).getStringCellValue(), null, null, Boolean.FALSE); //get the bill with the payer id
                                if ((prBills != null) && (prBills.size() == 1) && prBills.get(0).getBillingYear().equalsIgnoreCase(year)) { //this payer only has one property, this is his property
                                    currentBill = prBills.get(0);
                                    currentRow.getCell(26).setCellValue("MATCHED WITH PAYER ID");
                                } else {
                                    address_use = (currentRow.getCell(19) != null && !currentRow.getCell(19).toString().isEmpty()) ? currentRow.getCell(19).getStringCellValue() : null;
                                    if (address_use != null) { //there is address on this row, will try and match with this address
                                        prBills = billService.fetchBillsByPayerIdOrAddress(null, address_use, null, Boolean.FALSE); //get the bill with the payer id
                                        if (prBills != null && prBills.size() == 1 && prBills.get(0).getBillingYear().equalsIgnoreCase(year)) { //found the property with the address
                                            currentBill = prBills.get(0);
                                            currentRow.getCell(26).setCellValue("MATCHED WITH ADDRESS");
                                        } else { //property not found with address, will try and match with street
                                            if (address_use.indexOf(" ") > 0) {
                                                String pre_addr = address_use.substring(0, address_use.indexOf(" "));
                                                try {
                                                    Long.valueOf(pre_addr);
                                                    address_use = address_use.substring(address_use.indexOf(" ") + 1);
                                                    prBills = billService.fetchBillsByPayerIdOrAddress(null, null, address_use, Boolean.FALSE); //get the bill using the street name
                                                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Fetching bills with street name: {0}", address_use);
                                                } catch (NumberFormatException nfe) {
                                                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "Hos-no not a number: {0}", pre_addr);
                                                    prBills = billService.fetchBillsByPayerIdOrAddress(null, null, address_use, Boolean.FALSE); //get the bill using the street name
                                                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Fetching bills with street name inside catch: {0}", address_use);
                                                }
                                                if ((prBills != null) && (prBills.size() > 0) && prBills.get(0).getBillingYear().equalsIgnoreCase(year)) { //there's a bill on the street used, we need to match the money to be exact
                                                    for (PrintedBills bill : prBills) {
                                                        if (bill.getAmountDue().compareTo(amount_paid) == 0) { //the amount due is the same with the amount paid, this would be the right bill
                                                            currentBill = prBills.get(0);
                                                            currentRow.getCell(26).setCellValue("MATCHED WITH STREET");
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (currentBill == null) { //we have tried to match the payment with all the criteria known and failed, we should report this and move on
                                    if (currentRow.getCell(26) == null) {
                                        currentRow.createCell(26);
                                    }
                                    currentRow.getCell(26).setCellValue("BILL NOT FOUND");
                                    not_found_count++;
                                    if (currentRow.getCell(16) != null && currentRow.getCell(16).getStringCellValue().equalsIgnoreCase("ICSL")) {
                                        icsl_not_found++;
                                    }
                                    //    continue;
                                }
                            }
                            //This is a perfect scenario, I should save the payment information and associate it to the bill
                            payer_address = (currentRow.getCell(20) != null) ? currentRow.getCell(20).toString() : null;
                            payer_phone = (currentRow.getCell(21) != null) ? currentRow.getCell(21).toString() : null;
                            lrc_pin = (currentRow.getCell(22) != null) ? currentRow.getCell(22).toString() : null;
                            ppt_address = (currentRow.getCell(23) != null) ? currentRow.getCell(23).toString() : null;
                            lga_location = (currentRow.getCell(24) != null) ? currentRow.getCell(24).toString() : null;
                            lga_name = (currentRow.getCell(17) != null) ? currentRow.getCell(17).toString() : null;
                            district_name = (currentRow.getCell(18) != null) ? currentRow.getCell(18).toString() : null;
                            address_use = (currentRow.getCell(19) != null) ? currentRow.getCell(19).toString() : null;

                            currentRow.getCell(2).setCellType(Cell.CELL_TYPE_STRING);

                            existPayment = billService.findBillsPaymentById(null, currentRow.getCell(11).getStringCellValue());

                            if (existPayment != null && existPayment.getPaymentStatus().equalsIgnoreCase(PaymentStatusEnum.NOMATCHBILL.toString()) && currentBill != null) {
                                //payment has been saved before, but we couldn't find the bill the payment was made for then
                                currentPayment = existPayment;
                                currentPayment.setPrintedBillId(currentBill);
                                if (currentRow.getCell(26) != null) {
                                    currentRow.getCell(26).setCellValue("FOUND");
                                }
                                new_entity = Boolean.FALSE;

                            } else if (existPayment == null) {
                                //payment has neva been loaded before, we need to make sure to save it now
                                currentPayment = new BillPayments(null, currentRow.getCell(0).toString(), currentRow.getCell(1).toString(), currentRow.getCell(2).toString(),
                                        currentRow.getCell(3).toString(), currentRow.getCell(4).getDateCellValue(), currentRow.getCell(5).getDateCellValue(),
                                        dataProcUtil.parseStringToBigDecimal(currentRow.getCell(6).toString()), currentRow.getCell(7).toString(),
                                        currentRow.getCell(8) + "", currentRow.getCell(9).toString(), currentRow.getCell(10).toString(),
                                        currentRow.getCell(11).toString(), currentRow.getCell(12).getDateCellValue(), currentRow.getCell(13).toString(),
                                        currentRow.getCell(14).toString(), currentRow.getCell(15).toString(), currentRow.getCell(16).toString(),
                                        lga_name, district_name, address_use, payer_address, payer_phone, lrc_pin, ppt_address, lga_location, paymentFile, logindetail,
                                        currentBill, null, null);

                                new_entity = Boolean.TRUE;
                            } else {
                                if (existPayment.getCrAmount().compareTo(amount_paid) == 0) {
                                    currentRow.getCell(25).setCellValue("PAYMENT AND AMOUNT EXIST");
                                } else {
                                    currentRow.getCell(25).setCellValue("PAYMENT EXIST WITH DIFFERENT AMOUNT");
                                }
                                continue;
                            }

                            currentPayment.setIsProcessed(Boolean.FALSE);
                            currentPayment.setIsDuplicate(Boolean.FALSE);

                            //if (existPayment == null) {
                            if (currentBill != null) {
                                currentBill.setHasMatchPayment(Boolean.TRUE);
                                currentBill.setIsDelivered(Boolean.TRUE);
                                currentPayment.setConsultantId(currentBill.getConsultantId());
                                currentPayment.setPaymentStatus(PaymentStatusEnum.MATCHBILL.toString());
                            } else {
                                currentPayment.setPaymentStatus(PaymentStatusEnum.NOMATCHBILL.toString());
                            }

                            currentPayment = billService.saveBillPaymentInformation(currentPayment, currentBill, new_entity);
                            if (currentPayment != null) {
                                System.out.println(row + "-> Payment saved: " + currentRow.getCell(5) + " -- " + access_ref + " -- " + currentPayment.getId());
                                currentRow.getCell(25).setCellValue("PAYMENT SAVED");
                                match_count++;
                            } else {
                                currentRow.getCell(25).setCellValue("COULD NOT SAVE");
                            }
                            currentBill = null;
                            //   }
                        } catch (java.text.ParseException tex) {
                            currentRow.getCell(25).setCellValue("WRONG DATE FORMAT");
                        }
                    }
                    if ((row % 10) == 0) { //I want to save the results after every 100 rows
                        try (FileOutputStream fis = new FileOutputStream(file_path + "/" + temp_name, false)) {
                            excel_sheet_file.write(fis);
                        }
                    }
                }
                file_name = dataProcUtil.renameImageFile(file_name, "PROCESSED");

                //after processing the file, i need to send a copy to the user
                try (FileOutputStream fis = new FileOutputStream(file_path + "/" + file_name)) {
                    //uploadedFile_.write(download_dir + "/" + uploadedFile_.getFileName());//write(fis); //save file with changes made, I may need to email it instead
                    excel_sheet_file.write(fis);
                }

                if (logindetail == null) {
                    logindetail = registeredService.find(logindetail_id);
                }

                String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
                System.out.println("About to send email to: " + to_address[0]);
                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_path + "/" + file_name));

                email_msg = "<div>" + match_count + " payments matched with Bills.</div><div> Could not match a total of " + not_found_count + " payments, " + icsl_not_found + " of those belong"
                        + " to ICSL.</div><div>" + dup_count + " duplicates bills were found</div>";

                emailSender.sendEmailWithAttachment(to_address, null, null, "ICSL Payment Load Report File", email_msg, result_files);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * This method is used to push bills to ABC, it also saves a record of that
     * bill to our database, if the bill exist it updates on our database, and
     * does the same with ABC
     *
     * @param file_path
     * @param file_name
     * @param year
     * @param logindetail_id
     * @param file_id
     * @Authur sFagade
     */
    @Asynchronous
    public void loadAndProcessBillInformation(String file_path, String file_name, String year, Long logindetail_id, Long file_id) {

        List<PrintedBills> priBills;
        Map<String, String> api_params;
        PrintedBills newBill;
        Logindetails logindetail;
        File payment_file = new File(file_path + "/" + file_name);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date_of_notice, disc_date, full_date, late_pay1, late_pay2, late_pay3, late_pay_end, late_pay_end2, late_pay_end3;
        Row present_row;
        String api_return, notice_number, bank_code, payer_id, load_type, email_msg, addr_to_use, epayment_code, temp_name;
        BigDecimal yearCharge, disc_amount, late_pay_amt1, late_pay_amt2, late_pay_amt3, luc_amount;
        //Calendar cal = Calendar.getInstance();
        long suc_count = 0, save_fail = 0, app_fail = 0;
        Long bill_id;

        if (payment_file.exists()) { //this should always be the case

            try {

                XSSFWorkbook excel_sheet_file = new XSSFWorkbook(new FileInputStream(payment_file));
                XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);

                logindetail = registeredService.find(logindetail_id);
                temp_name = dataProcUtil.renameImageFile(file_name, "PROCESSING");

                for (int x = 1; x < sheet_.getPhysicalNumberOfRows(); x++) { //We start row @ 1 bcos 0 is headers
                    present_row = sheet_.getRow(x);

                    if (present_row != null && present_row.getCell(0) != null) { //if operator name is not set then we've reached the end of valid data

                        api_params = new HashMap<>();

                        if (present_row.getCell(43) == null) {
                            present_row.createCell(43);
                        }

                        if (present_row.getCell(4) != null) {
                            present_row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                            present_row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                            present_row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                            notice_number = present_row.getCell(2).getStringCellValue() + "";
                            bank_code = present_row.getCell(1).toString();
                            payer_id = present_row.getCell(4).getStringCellValue(); //get the payer ID for the row
                            if (present_row.getCell(15) != null) {
                                present_row.getCell(15).setCellType(Cell.CELL_TYPE_STRING);
                                epayment_code = present_row.getCell(15).toString();
                            } else {
                                epayment_code = bank_code;
                            }
                            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Processing ABC Bill row: {3}) -- {0} -- {1} -- {2}", new Object[]{notice_number, bank_code, payer_id, x});

                            if (payer_id.contains("-")) {
                                load_type = payer_id.substring(0, payer_id.indexOf("-"));
                                payer_id = payer_id.substring(payer_id.indexOf("-") + 1);

                                if (present_row.getCell(10) == null || present_row.getCell(9) == null) {
                                    present_row.getCell(43).setCellValue("DISTRICT MISSING");
                                    continue;
                                }
                                if (present_row.getCell(5) == null) {
                                    present_row.getCell(43).setCellValue("OWNERNAME MISSING");
                                    continue;
                                }

                                priBills = billService.fetchPaymentBills(bank_code, year); //Check if this Bill already exist in our records

                                bill_id = (priBills != null && priBills.size() > 0) ? priBills.get(0).getId() : null;

                                yearCharge = dataProcUtil.parseStringToBigDecimal(present_row.getCell(7).toString());
                                disc_amount = dataProcUtil.parseStringToBigDecimal(present_row.getCell(17).toString());
                                luc_amount = dataProcUtil.parseStringToBigDecimal(present_row.getCell(16).toString());
                                System.out.println("Getting value: " + present_row.getCell(8));
                                date_of_notice = present_row.getCell(8).getDateCellValue();
                                disc_date = present_row.getCell(18).getDateCellValue();
                                full_date = present_row.getCell(19).getDateCellValue();
                                late_pay1 = present_row.getCell(20).getDateCellValue();
                                late_pay_end = present_row.getCell(22).getDateCellValue();
                                late_pay_amt1 = dataProcUtil.parseStringToBigDecimal(present_row.getCell(21).toString());
                                late_pay2 = present_row.getCell(23).getDateCellValue();
                                late_pay_end2 = present_row.getCell(24).getDateCellValue();
                                late_pay_amt2 = dataProcUtil.parseStringToBigDecimal(present_row.getCell(25).toString());
                                late_pay3 = present_row.getCell(26).getDateCellValue();
                                late_pay_end3 = present_row.getCell(27).getDateCellValue();
                                late_pay_amt3 = dataProcUtil.parseStringToBigDecimal(present_row.getCell(28).toString());

                                if (present_row.getCell(11) != null) {
                                    present_row.getCell(11).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (present_row.getCell(12) != null) {
                                    present_row.getCell(12).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (present_row.getCell(13) != null) {
                                    present_row.getCell(13).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (present_row.getCell(14) != null) {
                                    present_row.getCell(14).setCellType(Cell.CELL_TYPE_STRING);
                                }

                                newBill = new PrintedBills(bill_id, notice_number, bank_code, present_row.getCell(6).getStringCellValue(), present_row.getCell(10).getStringCellValue(),
                                        present_row.getCell(9).getStringCellValue(), present_row.getCell(5).getStringCellValue(), yearCharge, year, date_of_notice, disc_amount, disc_date, full_date,
                                        late_pay1, late_pay_end, late_pay_amt1, late_pay2, late_pay_end2, late_pay_amt2, late_pay3, late_pay_end3, late_pay_amt3, payer_id,
                                        logindetail.getPerson().getOrganization(), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
                                newBill.setHouseNo((present_row.getCell(11) != null) ? present_row.getCell(11).toString() : null);
                                newBill.setPlotNo((present_row.getCell(12) != null) ? present_row.getCell(12).toString() : null);
                                newBill.setBlockNo((present_row.getCell(13) != null) ? present_row.getCell(13).toString() : null);
                                newBill.setFlatNo((present_row.getCell(14) != null) ? present_row.getCell(14).toString() : null);
                                newBill.setPaymentStatus(PaymentStatusEnum.NOTPAID.toString());
                                newBill.setBalanceCf(BigDecimal.ZERO);
                                newBill.setLuc(luc_amount);
                                newBill.setCreatedById(logindetail);
                                newBill.setOwnerHouseNo((present_row.getCell(31) != null) ? present_row.getCell(31).toString() : null);
                                newBill.setOwnerPlotNo((present_row.getCell(32) != null) ? present_row.getCell(32).toString() : null);
                                newBill.setOwnerBlockNo((present_row.getCell(33) != null) ? present_row.getCell(33).toString() : null);
                                newBill.setOwnerFlatNo((present_row.getCell(34) != null) ? present_row.getCell(34).toString() : null);
                                newBill.setOwnerStreetName((present_row.getCell(35) != null) ? present_row.getCell(35).toString() : null);
                                newBill.setOwnerDistrictName((present_row.getCell(36) != null) ? present_row.getCell(36).toString() : null);
                                newBill.setTaxCategory((present_row.getCell(42) != null) ? present_row.getCell(42).toString() : null);
                                newBill.setUsageDescription((present_row.getCell(41) != null) ? present_row.getCell(41).toString() : null);
                                newBill.setParcelIdn((present_row.getCell(29) != null) ? present_row.getCell(29).toString() : null);
                                newBill.setParcelSheetNumber((present_row.getCell(30) != null) ? present_row.getCell(30).toString() : null);
                                newBill.setAssessedValue((present_row.getCell(38) != null) ? dataProcUtil.parseStringToBigDecimal(present_row.getCell(38).toString()) : null);
                                newBill.setMillRate((present_row.getCell(37) != null) ? dataProcUtil.parseStringToBigDecimal(present_row.getCell(37).toString()) : null);
                                newBill.setBalanceCf((present_row.getCell(40) != null) ? dataProcUtil.parseStringToBigDecimal(present_row.getCell(40).toString()) : null);
                                newBill.setConsultantId(logindetail.getPerson().getOrganization());

                                if (bill_id != null) { //this will be true for updates
                                    newBill.setCreated(priBills.get(0).getCreated());
                                }

                                addr_to_use = "";
                                if (newBill.getHouseNo() != null && !newBill.getHouseNo().isEmpty()) {
                                    addr_to_use += newBill.getHouseNo();
                                } else if (newBill.getPlotNo() != null && (!newBill.getPlotNo().isEmpty())) {
                                    addr_to_use += newBill.getPlotNo();
                                } else if (newBill.getBlockNo() != null && (!newBill.getBlockNo().isEmpty())) {
                                    addr_to_use = newBill.getBlockNo();
                                } else {
                                    addr_to_use += newBill.getFlatNo();
                                }

                                addr_to_use += ", " + newBill.getStreetName();

                                api_params.put("PayerType", load_type); //prepare API call parameter
                                api_params.put("PayerID", payer_id);
                                api_params.put("Bank_Payment_Code", bank_code);
                                api_params.put("ipAddress", "");
                                api_params.put("Amount", yearCharge.toPlainString());
                                api_params.put("BillDate", shortDateFormat.format(date_of_notice));
                                api_params.put("Addresstouse", addr_to_use);
                                api_params.put("LgaName", present_row.getCell(9).getStringCellValue());
                                api_params.put("DistrictName", present_row.getCell(10).getStringCellValue());
                                api_params.put("MachantName", logindetail.getPerson().getOrganization().getOrganzaitionCode());
                                api_params.put("NoticeNumber", notice_number);
                                api_params.put("ePaymentCode", epayment_code);
                                api_params.put("fullpay_date", shortDateFormat.format(full_date));
                                api_params.put("start_latepaydate1", shortDateFormat.format(late_pay1));
                                api_params.put("end_latepaydate1", shortDateFormat.format(late_pay_end));
                                api_params.put("amt_latepay1", late_pay_amt1.toPlainString());
                                api_params.put("start_latepaydate2", shortDateFormat.format(late_pay2));
                                api_params.put("end_latepaydate2", shortDateFormat.format(late_pay_end2));
                                api_params.put("amt_latepay2", late_pay_amt2.toPlainString());
                                api_params.put("start_latepaydate3", shortDateFormat.format(late_pay3));
                                api_params.put("end_latepaydate3", shortDateFormat.format(late_pay_end3));
                                api_params.put("amt_latepay3", late_pay_amt3.toPlainString());

                                if (billService.saveUpdateBillInformation(newBill, null)) { //save bill successfully
                                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Saved Bill: {0}", new Object[]{bank_code});

                                    /** api_return = LedarOutsideGateway.pushBillToAbc(api_params, logindetail.getPerson().getOrganization().getId());
                                    if (api_return != null && !api_return.contains("error")) { */
                                        present_row.getCell(43).setCellValue("SAVED ON DB " + newBill.getId());
                                        suc_count++;
                                   /**  } else {
                                        present_row.getCell(29).setCellValue("API ERROR");
                                        app_fail++;
                                    } */

                                } else {
                                    present_row.getCell(43).setCellValue("SAVE FAILED");
                                    save_fail++;
                                    Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Failed to Save Bill: {0}", new Object[]{bank_code});
                                }
                            } else {
                                present_row.getCell(43).setCellValue("INVALID PAYER ID");
                                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Invalid payer id: {0}", new Object[]{payer_id});
                            }
                        } else {
                            present_row.getCell(43).setCellValue("NO PAYER ID OR EPAYMENT");
                        }
                        if ((x % 20) == 0) { //I want to save the results after every 100 rows
                            try (FileOutputStream fis = new FileOutputStream(file_path + "/" + temp_name, false)) {
                                excel_sheet_file.write(fis);
                            }
                        }
                    } else { //we have reached the last row in this sheet
                        break;
                    }
                }

                file_name = dataProcUtil.renameImageFile(file_name, "PROCESSED");
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Finished processing file, about to send mail");
                //after processing the file, i need to send a copy to the user
                try (FileOutputStream fis = new FileOutputStream(file_path + "/" + file_name)) {
                    excel_sheet_file.write(fis);
                }

                if (logindetail == null) {
                    logindetail = registeredService.find(logindetail_id);
                }

                String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
                System.out.println("About to send email to: " + to_address[0]);
                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_path + "/" + file_name));

                email_msg = "<div>" + suc_count + " Bills were successfully saved and pushed to ABC.</div><div> Could not save a total of " + save_fail + " Bills.</div><div> " + app_fail
                        + "Failed to save on ABC side.</div>";

                emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR Pushed Bills Report", email_msg, result_files);

            } catch (IOException | MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "Error Sending Bills", ex);
            } finally {

            }
        }

    }

    /**
     * This method is used to processing Payer ID for POs, it loads the excel
     * sheet uploaded by the user and reads each row of the excel sheet and then
     * construct an object to send to ABC api to generate or retrieve PO's payer
     * ID and when its done it sends the user an email of the excel sheet with
     * the Payer IDs filled
     *
     * @param file_path
     * @param file_name
     * @param selected_load_type
     * @param login_id
     * @Authur sFagade
     */
    @Asynchronous
    public void proceeExcelSheetForPayerId(String file_path, String file_name, String selected_load_type, Long login_id) {

        File excelFile = new File(file_path + "/" + file_name);
        Logindetails logindetail;

        if (excelFile.exists()) {

            String owner_name, payer_id = "", error_msg = "", email_msg;
            Row currentRow;
            Map<String, String> api_params;
            //int error_count = 1;

            try {
                XSSFWorkbook excel_sheet_file = new XSSFWorkbook(new FileInputStream(excelFile));
                XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);

                for (int row = 1; row < sheet_.getPhysicalNumberOfRows(); row++) {
                    currentRow = sheet_.getRow(row);
                    if (currentRow != null && currentRow.getCell(5) != null) {
                        owner_name = currentRow.getCell(5).getStringCellValue().trim(); //five should be Customer_fullname column
                        System.out.println(row + "-> getting payer ID: " + currentRow.getCell(5).toString() + " -- " + currentRow.getCell(1).toString());
                        if (!owner_name.isEmpty()) {

                            api_params = preparePayerIdMapObject(currentRow, selected_load_type);
                            if (api_params != null) {
                                generateAndWritePayerIdToFile(api_params, currentRow);
                            }
                            payer_id = null;
                        }
                    }
                }
                file_name = dataProcUtil.renameImageFile(file_name, "PROCESSED");
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Finished generating PAYER ID, about to send mail");
                //after processing the file, i need to send a copy to the user
                try (FileOutputStream fis = new FileOutputStream(file_path + "/" + file_name)) {
                    excel_sheet_file.write(fis);
                }

                logindetail = registeredService.find(login_id);

                String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
                String bc_address[] = {"sfagade@ic-sol.net"};

                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_path + "/" + file_name));

                email_msg = "<div> Your payer id file processing has been concluded, please find attached the processed file." + error_msg + "</div>";

                emailSender.sendEmailWithAttachment(to_address, bc_address, null, "LEDAR Payer ID Process Report", email_msg, result_files);

            } catch (IOException | MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, "File not found on server for Payer ID load: {0}", file_name);

        }
    }

    @Asynchronous
    public void loadAndValidateBillRegistrationStatus(String file_path, String file_name, Long logindetail_id) {
        File bills_file = new File(file_path + "/" + file_name);

        Row current_row;
        Map<String, String> api_params;
        String bank_code, valid_status, email_msg, payer_id, payer_type;
        JSONObject returned_msg;
        Logindetails logindetail;

        if (bills_file.exists()) { //this should always be the case
            try {
                XSSFWorkbook excel_sheet_file = new XSSFWorkbook(new FileInputStream(bills_file));
                XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);
                logindetail = registeredService.find(logindetail_id);

                for (int x = 1; x < sheet_.getPhysicalNumberOfRows(); x++) { //We start row @ 1 bcos 0 is headers
                    current_row = sheet_.getRow(x);

                    if (current_row != null && current_row.getCell(0) != null) { //if operator name is not set then we've reached the end of valid data

                        api_params = new HashMap<>();

                        if (current_row.getCell(29) == null) {
                            current_row.createCell(29);
                        }

                        if (current_row.getCell(1) != null) {
                            current_row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);

                            bank_code = current_row.getCell(1).toString();
                            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Validating Bill on row: {1}) -- {0}", new Object[]{bank_code, x});

                            api_params.put("bank_code", bank_code);

                            try {
                                valid_status = LedarOutsideGateway.makeAbcBillValidationCall(api_params, logindetail.getPerson().getOrganization().getId());
                                if (valid_status != null && valid_status.contains("EntryID")) { //this should always be the same
                                    valid_status = valid_status.substring(valid_status.indexOf("{"), valid_status.indexOf("}") + 1);
                                    returned_msg = new JSONObject(valid_status);
                                    current_row.getCell(29).setCellValue("REGISTERED: " + returned_msg.get("PayerType") + "-" + returned_msg.get("PayerID"));
                                } else if (valid_status != null && valid_status.contains("ErrorMessage")) { //Bill hasn't been registered b4
                                    valid_status = valid_status.substring(valid_status.indexOf("{"), valid_status.indexOf("}") + 1);
                                    returned_msg = new JSONObject(valid_status);
                                    current_row.getCell(29).setCellValue("NOT REGISTERED: " + returned_msg.get("ErrorMessage"));
                                    if (current_row.getCell(4) == null) { //there's no Payer ID, we need to generate one now
                                        //payer_id = current_row.getCell(4).getStringCellValue().substring(0, current_row.getCell(4).getStringCellValue().indexOf("-"));
                                        payer_type = (current_row.getCell(3) == null || current_row.getCell(3).getStringCellValue().equalsIgnoreCase("N")) ? "Individuals" : "Company";
                                        api_params = preparePayerIdMapObject(current_row, payer_type);
                                        if (api_params != null) { //this should always be the case
                                            this.generateAndWritePayerIdToFile(api_params, current_row);
                                        }
                                    }
                                }
                            } catch (JSONException | UnknownHostException ex) {
                                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
                                current_row.getCell(29).setCellValue("NETWORK ERROR: " + ex.getMessage());
                            }
                        }
                    }
                }

                file_name = dataProcUtil.renameImageFile(file_name, "PROCESSED");
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Finished processing file, about to send mail");
                //after processing the file, i need to send a copy to the user
                try (FileOutputStream fis = new FileOutputStream(file_path + "/" + file_name)) {
                    excel_sheet_file.write(fis);
                }

                String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
                System.out.println("About to send email to: " + to_address[0]);
                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_path + "/" + file_name));

                email_msg = "<div>Bills validation has been checked completely, please see attachment for report.</div>";

                emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR Bills Validation Report", email_msg, result_files);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void loadDistrictStreet(String file_path, String file_name, Long logindetail_id) {
        File street_files = new File(file_path + "/" + file_name);

        Row current_row;
        String district, street_name, email_msg, estate_name, lcda;
        List<LcdaWards> lcdaWard;
        WardStreets street;
        LcdaWards selectedDistrict;
        Logindetails logindetail;

        if (street_files.exists()) { //this should always be the case
            try {
                XSSFWorkbook excel_sheet_file = new XSSFWorkbook(new FileInputStream(street_files));
                XSSFSheet sheet_ = excel_sheet_file.getSheetAt(0);
                logindetail = registeredService.find(logindetail_id);

                for (int x = 1; x < sheet_.getPhysicalNumberOfRows(); x++) { //We start row @ 1 bcos 0 is headers
                    current_row = sheet_.getRow(x);
                    selectedDistrict = null;

                    if (current_row != null && current_row.getCell(0) != null) { //if operator name is not set then we've reached the end of valid data

                        if (current_row.getCell(4) == null) {
                            current_row.createCell(4);
                        }

                        if (current_row.getCell(1) != null) {
                            current_row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);

                            district = (current_row.getCell(0) != null) ? current_row.getCell(0).toString() : null;
                            street_name = (current_row.getCell(1) != null) ? current_row.getCell(1).toString() : null;
                            estate_name = (current_row.getCell(2) != null) ? current_row.getCell(2).toString().toUpperCase() : null;
                            lcda = (current_row.getCell(3) != null) ? current_row.getCell(3).toString() : null;
                            Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Loading Street: {1}) -- {0} -- {2}", new Object[]{district, x, street_name});

                            if (district != null && street_name != null && lcda != null) {
                                lcdaWard = lcdaService.fetchAllLcdaWardsByName(district, logindetail.getPerson().getOrganization().getId(), Boolean.FALSE);
                                if (lcdaWard != null && lcdaWard.size() > 0) {
                                    for (LcdaWards ward : lcdaWard) {
                                        if (ward.getLocalCouncilDevAreaId().getLcdaName().equalsIgnoreCase(lcda)) {
                                            selectedDistrict = ward;
                                            break;
                                        }
                                    }
                                    if (selectedDistrict != null) {
                                        street = new WardStreets(null, street_name.toUpperCase(), null, null, estate_name, null);
                                        street.setLcdaWardId(lcdaWard.get(0));
                                        street.setIsVerified(Boolean.TRUE);
                                        street.setCreatedById(logindetail);
                                        street.setVerifiedById(logindetail);

                                        street = lcdaService.saveNewStreetInformation(street, null);
                                        if (street != null) {
                                            current_row.getCell(4).setCellValue("SAVE SUCCESSFULLY");
                                        } else {
                                            current_row.getCell(4).setCellValue("SAVE FAILED");
                                        }
                                    } else {
                                        current_row.getCell(4).setCellValue("COULD NOT FIND DISTRICT");
                                    }

                                } else {
                                    current_row.getCell(4).setCellValue("NO DISTRICT");
                                }
                            } else {
                                current_row.getCell(4).setCellValue("REQUIRED FIELD VALIDATION ERROR");
                            }
                        }
                    }
                }

                file_name = dataProcUtil.renameImageFile(file_name, "PROCESSED");
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.INFO, "Streets Load Report");
                //after processing the file, i need to send a copy to the user
                try (FileOutputStream fis = new FileOutputStream(file_path + "/" + file_name)) {
                    excel_sheet_file.write(fis);
                }

                String to_address[] = {logindetail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress(), "sfagade@ic-sol.net"};
                System.out.println("About to send email to: " + to_address[0]);
                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_path + "/" + file_name));

                email_msg = "<div>Bills validation has been checked completely, please see attachment for report.</div>";

                emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR NEW STREET LOAD Report", email_msg, result_files);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(DataloadProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Map<String, String> preparePayerIdMapObject(Row excel_row, String load_type) {
        Map<String, String> api_param;
        String address, bank_code, owner_name, title_, gender_;

        excel_row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
        bank_code = excel_row.getCell(1).getStringCellValue();
        if (bank_code.contains(".")) {
            bank_code = bank_code.substring(0, bank_code.indexOf("."));
        }
        owner_name = excel_row.getCell(5).getStringCellValue().trim();
        owner_name = owner_name.replace("  ", " ");
        address = "";
        if (excel_row.getCell(11) != null) {
            address += excel_row.getCell(11);
        } else if (excel_row.getCell(12) != null) {
            address += excel_row.getCell(12);
        } else if (excel_row.getCell(13) != null) {
            address += excel_row.getCell(13);
        } else if (excel_row.getCell(14) != null) {
            address += excel_row.getCell(14);
        }

        address += ", " + excel_row.getCell(6) + "," + excel_row.getCell(9);

        api_param = new HashMap<>();
        api_param.put("payer_type", load_type);

        if (load_type.equals("Individuals")) {
            title_ = "MR";
            gender_ = "M";

            if (owner_name.contains(".")) { //this will be the case if the name includes a title
                title_ = owner_name.substring(0, owner_name.indexOf(".")).trim();
                owner_name = owner_name.substring(owner_name.indexOf(".") + 1).trim();
                if (title_.equalsIgnoreCase("MRS") || title_.equalsIgnoreCase("MS")) {
                    gender_ = "F";
                }
            }
            api_param.put("title", title_);
            api_param.put("gender", gender_);
            api_param.put("surName", owner_name.substring(owner_name.indexOf(" ") + 1));
            api_param.put("dateOfBirth", shortDateFormat.format(new Date()));

        } else {
            api_param.put("ShortName", WordUtils.initials(owner_name));
        }

        api_param.put("fullName", owner_name + " " + bank_code);
        api_param.put("mstatus", "S");
        api_param.put("email", owner_name.replaceAll(" ", "") + bank_code + "@luc.com");
        api_param.put("phone", bank_code + "0000");
        api_param.put("addNo", "1");
        api_param.put("address", address);

        return api_param;
    }

    private void generateAndWritePayerIdToFile(Map<String, String> params, Row working_row) {

        String payer_id = null;

        try {
            payer_id = LedarOutsideGateway.makeAbcApiPayerIdCall(params);
            System.out.println("Payer ID is: " + payer_id);
            if (payer_id != null) { //this should always be the case
                if ((payer_id.contains("Payer Created")) || (payer_id.contains("already"))) { //payer id was retrieved successfully, I should  go ahead and update excel file now

                    if (working_row.getCell(4) == null) {
                        working_row.createCell(4);
                    }

                    working_row.getCell(4).setCellValue(payer_id.substring(payer_id.lastIndexOf("(") + 1, payer_id.lastIndexOf(")")));

                } else { //could not retrieved payer id, I need to report this to the user
//                    error_msg += error_count + ") Record at row: " + (row + 1) + " failed: " + payer_id;
//                    error_count++;
                }
            }
        } catch (JSONException | IOException ex) {
            Logger.getLogger(DataloadProcessor.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (java.lang.StringIndexOutOfBoundsException tout) {
            Logger.getLogger(DataloadProcessor.class
                    .getName()).log(Level.SEVERE, null, tout);
            if (working_row.getCell(4) == null) {
                working_row.createCell(4);
            }
            working_row.getCell(4).setCellValue(payer_id);
        }
    }
}
