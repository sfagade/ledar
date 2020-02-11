/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.util;

import java.io.*;
import java.math.BigDecimal;
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
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.mail.MessagingException;

import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author sfagade
 */
@Stateless
@LocalBean
public class DataExportProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    PrintedBillsService billService;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    DataProcessorUtil dataProcUtil;
    @Inject
    private EmailSender emailSender;

    @Asynchronous
    public void processDeliveredBillsExportRecords(Map<String, String> criteria, Long login_id, String file_path, Boolean copy_third_party) {
        //Map<String, String> criteria = new HashMap<>();
        List<PrintedBills> printedBillsList;
        Row current_row;

        int totalRecord = (int) billService.countFilteredBillResults(criteria);
        String[] bookHeaderData = {"NOTICE NUMBER", "PROPERTY IDN", "PARCEL IDN", "PARCEL  SHEET NUMBER", "HOUSE NUMBER", "STREET NAME", "DISTRICT AREA NAME", "LGA", "TAX CATEGORY",
            "DELIVERY DATE", "IS DELIVERED", "BILLING YEAR", "CONSULTANT", "LATITUDE", "LONGITUDE"};

        int rowCount = 0;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet workSheet = workbook.createSheet("Co-ordinate Reports");
        Logindetails login_detail = registeredService.find(login_id);

        this.createDuplicatesSheetHeaders(bookHeaderData, workSheet);
        rowCount++;

        String file_access = file_path + "/" + "gps_coordinates_reports" + "_" + new SimpleDateFormat("HH_mm_ss", Locale.ENGLISH).format(new Date()) + ".xlsx";

        for (int record_count = 0; record_count < totalRecord; record_count += 1000) {
            printedBillsList = billService.fetchPrintedBillsFilter(criteria, record_count, 1000, null, null);
            if (printedBillsList != null && printedBillsList.size() > 0) {
                for (PrintedBills bills : printedBillsList) {
                    current_row = workSheet.createRow(rowCount);
                    this.createGpsCordinateSheetRow(bills, current_row);
                    Logger.getLogger(DataExportProcessor.class.getName()).log(Level.INFO, "Processing bill record for GPS {0} -- {1}", new Object[]{bills.getPropertyIdn(), bills.getDeliveryDate()});
                    rowCount++;
                }
                //I want to save the results after every 100 rows
                try (FileOutputStream fis = new FileOutputStream(file_access, false)) {
                    workbook.write(fis);
                } catch (FileNotFoundException fe) {
                    Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, fe);
                } catch (IOException ioe) {
                    Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ioe);
                }

            } else {
                break;
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(file_access)) {
            workbook.write(outputStream);

            String to_address[] = {login_detail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
            String bc_address[] = new String[1];
            bc_address[0] = "sfagade@ic-sol.net";
            if (copy_third_party) {
                //bc_address[1] = "adeolaakinwale@gmail.com";
            }

            List<File> result_files = new ArrayList<>();
            result_files.add(new File(file_access));

            String email_msg = "<div> GPS coordinate report for delivered bills has been generated successfully. Please see attachment.</div>";

            emailSender.sendEmailWithAttachment(to_address, bc_address, null, "LEDAR GPS delivery report", email_msg, result_files);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is used to export duplicate payment information based on the
     * selected month by the user. It gets that duplicate payment information
     * and generates an excel sheet and email the excel file to the user that
     * request for the report.
     *
     * @param month
     * @param year
     * @param login_id
     * @param file_path
     * @author sfagade
     */
    @Asynchronous
    public void processMonthlyDuplicatePaymentReport(int month, int year, Long login_id, String file_path) {

        List<Object[]> duplicateBillList = billService.fetchDuplicateMonthlyPayments(null, month, year);
        List<BillPayments> billsPayments;
        String payer_id, payer_name;
        Date valueDate;
        BigDecimal crAmount;
        Row current_row;
        Logindetails login_detail;

        final DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if (duplicateBillList != null && duplicateBillList.size() > 0) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet workSheet = workbook.createSheet("Duplicate Payments");
            login_detail = registeredService.find(login_id);

            String[] bookHeaderData = {"vPayerName", "vPayerID", "EntryID", "AppliedDate", "EntryDate", "ValueDate", "CrAmount", "ReceiptBIR", "DSlipRef", "AgencyRef", "RevCode", "TransRef", "SysDate",
                "AssessRef", "Ref", "ShortName", "MerchantName", "LgaName", "DistrictName", "AddressToUse", "PayerAddress", "PayerPhoneNo", "LRCPin", "pptAddress", "LGALocation"};

            int rowCount = 0;

            this.createDuplicatesSheetHeaders(bookHeaderData, workSheet);

            rowCount++;
            for (Object[] duplicates : duplicateBillList) {
                try {
                    payer_id = duplicates[1].toString();
                    payer_name = duplicates[0].toString();
                    crAmount = dataProcUtil.parseStringToBigDecimal(duplicates[3].toString()); //I need to confirm that precision is not being sacrifised here
                    valueDate = shortDateFormat.parse(duplicates[2].toString());

                    billsPayments = billService.fetchDuplicateRecord(payer_id, payer_name, crAmount, valueDate);

                    if (billsPayments != null && billsPayments.size() > 0) { //this should always be the case here
                        Logger.getLogger(DataExportProcessor.class.getName()).log(Level.INFO, "Adding duplicates to excel: {0}", billsPayments.size());
                        for (BillPayments payment : billsPayments) {
                            current_row = workSheet.createRow(rowCount);
                            this.createDuplicateSheetRow(payment, current_row);
                            rowCount++;
                        }
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String file_access = file_path + "/" + "duplicate_payments_" + month + "_" + new SimpleDateFormat("HH_mm_ss", Locale.ENGLISH).format(new Date()) + ".xlsx";

            try (FileOutputStream outputStream = new FileOutputStream(file_access)) {
                workbook.write(outputStream);

                String to_address[] = {login_detail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                String bc_address[] = {"sfagade@ic-sol.net"};

                List<File> result_files = new ArrayList<>();
                result_files.add(new File(file_access));

                String email_msg = "<div> Duplicate payment report for the month of " + month + " has been generated successfully. Please see attachment.</div>";

                emailSender.sendEmailWithAttachment(to_address, bc_address, null, "LEDAR " + month + " Duplicate Payments Report", email_msg, result_files);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Asynchronous
    public void processBillPaymentStatus(int year, Long login_id, String status, String file_path) {
        if (status != null && year > 1000) {
            List<PrintedBills> printed_bills;
            Map<String, String> criteria;
            String[] local_gov = {"EPE", "ETI-OSA", "IBEJU-LEKKI", "IKORODU", "KOSOFE", "SOMOLU"};
            String[] sheet_headers = {"Property ID", "Payer ID", "Bank Payment Code", "Address", "LGA", "District", "LUC", "Amount Due", "Discount Amount", "Payment Status", "Owner Name", "1st Late Pay", "2nd Late Pay", "3rd Late Pay","Latitude","Longitude"};
            Boolean has_payment = (status.equals("With Payment"));
            Logindetails login_detail = registeredService.find(login_id);
            Row current_row;
            int rowCount = 1;
            String file_name;
            List<File> result_files = new ArrayList<>();
            List<String> order_field = new ArrayList<>();
            order_field.add("districtName");

            for (String lsg : local_gov) {
                criteria = new HashMap<>();
                criteria.put("lga_name", lsg);
                criteria.put("consultant", login_detail.getPerson().getOrganization().getId().toString());
                criteria.put("deleted", "false");
                criteria.put("has_payment", has_payment.toString());

                printed_bills = billService.fetchPrintedBillsFilter(criteria, 0, 2000000, order_field, null);
                if (printed_bills != null && printed_bills.size() > 0) {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet workSheet = workbook.createSheet("Bills " + status);
                    this.createDuplicatesSheetHeaders(sheet_headers, workSheet);
                    Logger.getLogger(DataExportProcessor.class.getName()).log(Level.INFO, "Bills {0} for {1} created", new Object[]{status, lsg});
                    for (PrintedBills bill : printed_bills) {
                        current_row = workSheet.createRow(rowCount);
                        Logger.getLogger(DataExportProcessor.class.getName()).log(Level.INFO, "Adding sheet row {0} - {1} - {2} - {3}", new Object[]{lsg, bill.getPropertyIdn(), bill.getPayerId(), bill.getAmountDue()});
                        createBillsSheetRow(bill, current_row);
                        rowCount++;
                    }
                    file_name = file_path + "/" + "bills_" + status + "_" + lsg + "_" + new SimpleDateFormat("HH_mm_ss", Locale.ENGLISH).format(new Date()) + ".xlsx";
                    try (FileOutputStream outputStream = new FileOutputStream(file_name)) {
                        workbook.write(outputStream);
                        result_files.add(new File(file_name));
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, "Could not create bills file", ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, "IO while creating bills file", ex);
                    }
                    rowCount = 1;
                }
            }

            try {
                String to_address[] = {login_detail.getPerson().getContactInformationsList().get(0).getOfficeEmailAddress()};
                //String bc_address[] = {"sfagade@ic-sol.net"};
                Logger.getLogger(DataExportProcessor.class.getName()).log(Level.INFO, "Finished generating bills export, about to send mail now");
                String email_msg = "<div> Bills " + status + " export has completed. Please see attached files.</div>";
                emailSender.sendEmailWithAttachment(to_address, null, null, "LEDAR Bills " + status + " Data export", email_msg, result_files);
            } catch (MessagingException ex) {
                Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Logger.getLogger(DataExportProcessor.class.getName()).log(Level.SEVERE, "Invalid request sent: {0} -- {1}", new Object[]{status, year});
        }
    }

    public void createDuplicatesSheetHeaders(String[] header_names, XSSFSheet sheet) {

        Row header_row = sheet.createRow(0);
        int colum_count = 0;
        Cell cell;

        for (String header : header_names) {
            cell = header_row.createCell(colum_count++);
            cell.setCellValue(header);
        }

    }

    public void createDuplicateSheetRow(BillPayments payment_data, Row row) {

        if (payment_data != null && row != null) {
            Cell new_cell;
            new_cell = row.createCell(0);
            new_cell.setCellValue(payment_data.getVPayerName());

            new_cell = row.createCell(1);
            new_cell.setCellValue(payment_data.getVPayerId());

            new_cell = row.createCell(2);
            new_cell.setCellValue(payment_data.getEntryId());

            new_cell = row.createCell(3);
            new_cell.setCellValue(payment_data.getAppliedDate());

            new_cell = row.createCell(4);
            new_cell.setCellValue(payment_data.getEntryDate());

            new_cell = row.createCell(5);
            new_cell.setCellValue(payment_data.getValueDate());

            new_cell = row.createCell(6);
            new_cell.setCellValue(payment_data.getCrAmount() + "");

            new_cell = row.createCell(7);
            new_cell.setCellValue(payment_data.getReceiptBir());

            new_cell = row.createCell(8);
            new_cell.setCellValue(payment_data.getDSlipRef());

            new_cell = row.createCell(9);
            new_cell.setCellValue(payment_data.getAgencyRef());

            new_cell = row.createCell(10);
            new_cell.setCellValue(payment_data.getRevCode());

            new_cell = row.createCell(11);
            new_cell.setCellValue(payment_data.getTransRef());

            new_cell = row.createCell(12);
            new_cell.setCellValue(payment_data.getSysDate());

            new_cell = row.createCell(13);
            new_cell.setCellValue(payment_data.getAssessRef());

            new_cell = row.createCell(14);
            new_cell.setCellValue(payment_data.getReferenceStr());

            new_cell = row.createCell(15);
            new_cell.setCellValue(payment_data.getShortName());

            new_cell = row.createCell(16);
            new_cell.setCellValue(payment_data.getMerchantName());

            new_cell = row.createCell(17);
            new_cell.setCellValue(payment_data.getLgaName());

            new_cell = row.createCell(18);
            new_cell.setCellValue(payment_data.getDistrictName());

            new_cell = row.createCell(19);
            new_cell.setCellValue(payment_data.getAddressToUse());

            new_cell = row.createCell(20);
            new_cell.setCellValue(payment_data.getPayerAddress());

            new_cell = row.createCell(21);
            new_cell.setCellValue(payment_data.getPayerPhoneNo());

            new_cell = row.createCell(22);
            new_cell.setCellValue(payment_data.getLrcPin());

            new_cell = row.createCell(23);
            new_cell.setCellValue(payment_data.getPptAddress());

            new_cell = row.createCell(24);
            new_cell.setCellValue(payment_data.getLgaLocation());
        }
    }

    public void createBillsSheetRow(PrintedBills billData, Row row) {
//, "Owner Name", "1st Late Pay", "2nd Late Pay", "3rd Late Pay"
        if (billData != null && row != null) {
            String address = "", pay_status, latitude = (billData.getLatitude() != null) ? billData.getLatitude() : null;

            pay_status = (billData.getHasMatchPayment()) ? "Has Paid" : "Has not paid";

            address = getBillPropertyNo(billData, address);

            address += ", " + billData.getStreetName();

            Cell new_cell;
            new_cell = row.createCell(0);
            new_cell.setCellValue(billData.getPropertyIdn());

            new_cell = row.createCell(1);
            new_cell.setCellValue(billData.getPayerId());

            new_cell = row.createCell(2);
            new_cell.setCellValue(billData.getBankPaymentCode());

            new_cell = row.createCell(3);
            new_cell.setCellValue(address);

            new_cell = row.createCell(4);
            new_cell.setCellValue(billData.getLga());

            new_cell = row.createCell(5);
            new_cell.setCellValue(billData.getDistrictName());

            new_cell = row.createCell(6);
            new_cell.setCellValue(dataProcUtil.formartBigDecimanToString(billData.getLuc()));

            new_cell = row.createCell(7);
            new_cell.setCellValue(dataProcUtil.formartBigDecimanToString(billData.getAmountDue()));

            new_cell = row.createCell(8);
            new_cell.setCellValue(dataProcUtil.formartBigDecimanToString(billData.getDiscAmount()));

            new_cell = row.createCell(9);
            new_cell.setCellValue(pay_status);
            new_cell = row.createCell(10);
            new_cell.setCellValue(billData.getOwnerName());
            new_cell = row.createCell(11);
            new_cell.setCellValue(dataProcUtil.formartBigDecimanToString(billData.getAmtLatepay1()));
            new_cell = row.createCell(12);
            new_cell.setCellValue(dataProcUtil.formartBigDecimanToString(billData.getAmtLatepay2()));
            new_cell = row.createCell(13);
            new_cell.setCellValue(dataProcUtil.formartBigDecimanToString(billData.getAmtLatepay3()));
            if(latitude != null) {
	            new_cell = row.createCell(14);
	            new_cell.setCellValue(latitude);
	            new_cell = row.createCell(15);
	            new_cell.setCellValue(billData.getLongitude());
            }

        }
    }

    private void createGpsCordinateSheetRow(PrintedBills bills, Row currentRow) {

        if (bills != null && currentRow != null) {
            String house_no = "", latitude = (bills.getLatitude() != null) ? bills.getLatitude() : null;
            Date delivery_date= (bills.getDeliveryDate() != null) ? bills.getDeliveryDate() : null;

            /**
             * {"NOTICE NUMBER", "PROPERTY IDN", "PARCEL IDN", "PARCEL SHEET
             * NUMBER", "HOUSE NUMBER", "STREET NAME", "DISTRICT AREA NAME",
             * "LGA", "TAX CATEGORY", "DELIVERY DATE", "IS DELIVERED", "BILLING
             * YEAR", "CONSULTANT", "LATITUDE", "LONGITUDE"};
             */
            house_no = getBillPropertyNo(bills, house_no);

            house_no += ", " + bills.getStreetName();

            Cell new_cell;
            new_cell = currentRow.createCell(0);
            new_cell.setCellValue(bills.getPropertyIdn());

            new_cell = currentRow.createCell(1);
            new_cell.setCellValue(bills.getPropertyIdn());

            new_cell = currentRow.createCell(2);
            new_cell.setCellValue(bills.getParcelIdn());

            new_cell = currentRow.createCell(3);
            new_cell.setCellValue(bills.getParcelSheetNumber());

            new_cell = currentRow.createCell(4);
            new_cell.setCellValue(house_no);

            new_cell = currentRow.createCell(5);
            new_cell.setCellValue(bills.getStreetName());

            new_cell = currentRow.createCell(6);
            new_cell.setCellValue(bills.getDistrictName());
            new_cell = currentRow.createCell(7);
            new_cell.setCellValue(bills.getLga());
            new_cell = currentRow.createCell(8);
            new_cell.setCellValue(bills.getTaxCategory());
            new_cell = currentRow.createCell(9);
            if(delivery_date != null) {
            new_cell.setCellValue(delivery_date);
            } else {
            	new_cell.setCellValue("No Date");	
            }
            new_cell = currentRow.createCell(10);
            new_cell.setCellValue("YES");
            new_cell = currentRow.createCell(11);
            new_cell.setCellValue(bills.getBillingYear());
            new_cell = currentRow.createCell(12);
            new_cell.setCellValue("ICSL");
            new_cell = currentRow.createCell(13);
            if(latitude != null) {
            	new_cell.setCellValue(latitude);
            	new_cell = currentRow.createCell(14);
                new_cell.setCellValue(bills.getLongitude());
            } else {
            	new_cell.setCellValue("No DATA");
            }
            
            

        }
    }

    private String getBillPropertyNo(PrintedBills bills, String house_no) {
        if (bills.getHouseNo() != null && !bills.getHouseNo().isEmpty()) {
            house_no += bills.getHouseNo();
        } else if (bills.getPlotNo() != null && (!bills.getPlotNo().isEmpty())) {
            house_no += bills.getPlotNo();
        } else if (bills.getBlockNo() != null && (!bills.getBlockNo().isEmpty())) {
            house_no = bills.getBlockNo();
        } else {
            house_no += (bills.getFlatNo() != null) ? bills.getFlatNo() : "";
        }
        return house_no;
    }
}
