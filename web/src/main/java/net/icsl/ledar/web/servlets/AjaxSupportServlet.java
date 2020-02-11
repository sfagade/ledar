/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.PaymentStatusEnum;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.enums.ValuationStatusEnum;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.model.RegisteredDevices;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.Visitors;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.model.WardStreets;
import net.icsl.ledar.ejb.service.ComplaintDataService;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.bean.LoginBean;

/**
 *
 * @author sfagade
 */
@WebServlet(name = "AjaxSupportServlet", urlPatterns = {"/ajaxSupports"})
public class AjaxSupportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private PrintedBillsService billService;
    @Inject
    private ComplaintDataService complService;

    @Inject
    private LoginBean loginBean;

    Logindetails loginPerson;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JSONObject returnMsg = new JSONObject();

        String formAction = request.getParameter("form_action");

        try {
            if ((formAction != null) && (!formAction.isEmpty())) {
                if (formAction.equalsIgnoreCase("deleteFile")) {
                    returnMsg = deletePropertyFile(request);
                } else if (formAction.equalsIgnoreCase("cngustatus")) {
                    returnMsg = changeUserActiveStatus(request);
                } else if (formAction.equalsIgnoreCase("deleteunvrstreet")) {
                    returnMsg = deleteUnverifiedStreet(request);
                } else if (formAction.equalsIgnoreCase("verynewstr")) {
                    returnMsg = verifyNewStreetData(request);
                } else if (formAction.equalsIgnoreCase("cleanpropertystreet")) {
                    returnMsg = cleanupPropertiesStreet(request);
                } else if (formAction.equalsIgnoreCase("mkvirfied")) {
                    returnMsg = updatePropertyToVerified(request);
                } else if (formAction.equalsIgnoreCase("deleteProperty")) {
                    returnMsg = delectProperty(request);
                } else if (formAction.equalsIgnoreCase("markDuplicate")) {
                    returnMsg = markPaymentAsDuplicate(request);
                } else if (formAction.equals("delDupBill")) {
                    returnMsg = markDuplBillDeleted(request);
                } else if (formAction.equals("visitSignOut")) { //NOTICE this method might nt be needed anymore
                    returnMsg = signVisitorOut(request);
                } else if (formAction.equals("cngdevstatus")) {
                    returnMsg = changeDeviceAbleStatus(request);
                } else if (formAction.equals("matchpaybill")) {
                    returnMsg = matchPaymentToBill(request);
                } else if (formAction.equals("mkbillReso")) {
                    returnMsg = matchBillUpdateResolved(request);
                }
            } else {
                returnMsg.put("errorMessage", "You need to tell me what u want to do dude!");
            }
        } catch (JSONException ex) {
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setContentType("application/json");
        response.getWriter().write(returnMsg.toString());

    }

    private JSONObject matchBillUpdateResolved(HttpServletRequest request) {
        JSONObject returnMsg = new JSONObject();

        Long bill_id = Long.valueOf(request.getParameter("propID"));
        PrintedBills printedBill = landPropService.findPrintedBillsById(bill_id);

        printedBill.setIsDeleted(Boolean.TRUE);
        printedBill.setUpdateStatus("UPDATE TREATED");
        printedBill.setIsUpdateRequired(Boolean.FALSE);

        if (billService.saveUpdateBillInformation(printedBill, null)) {
            returnMsg.put("successMessage", "Bill information updated successfully!");
        } else {
            returnMsg.put("errorMessage", "Could not update bill information, please try again later or contact support!");
        }

        return returnMsg;
    }

    private JSONObject matchPaymentToBill(HttpServletRequest request) {
        JSONObject returnMsg = new JSONObject();

        String bill_id = request.getParameter("fileid");
        String payment_id = request.getParameter("pyid");

        PrintedBills printedBill;
        BillPayments payments;

        if (bill_id != null && !bill_id.isEmpty() && payment_id != null && !payment_id.isEmpty()) {

            printedBill = landPropService.findPrintedBillsById(Long.valueOf(bill_id));
            payments = billService.findBillsPaymentById(Long.valueOf(payment_id), null);

            if ((printedBill != null) && (payments != null)) { //this should always be the case
                printedBill.setHasMatchPayment(Boolean.TRUE);
                printedBill.setIsDelivered(Boolean.TRUE);
                payments.setConsultantId(printedBill.getConsultantId());
                payments.setPaymentStatus(PaymentStatusEnum.MATCHBILL.toString());
                payments.setIsProcessed(Boolean.FALSE);
                payments.setIsDuplicate(Boolean.FALSE);
                payments.setPrintedBillId(printedBill);

                payments = billService.saveBillPaymentInformation(payments, printedBill, Boolean.FALSE);
                if (payments != null) {
                    returnMsg.put("successMessage", "Record matched!");
                } else {
                    returnMsg.put("errorMessage", "Failed to match records, please try again later or contact support!");
                }

            } else {
                returnMsg.put("errorMessage", "Bill or Payment not found!");
            }
        }

        return returnMsg;
    }

    public JSONObject deletePropertyFile(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMsg = new JSONObject();

        String file_id = request.getParameter("fileid");

        FileUploads fileUpload = landPropService.findFileByUploadId(Long.valueOf(file_id));

        if (fileUpload != null) {
            if (loginBean != null) {
                fileUpload.setDeletedById(loginBean.getPerson().getLogindetailId());
            }
            fileUpload.setIsDeleted(true);
            fileUpload.setDeleteDate(new Date());

            if (landPropService.updateFileUploads(fileUpload)) {
                try {
                    returnMsg.put("successMessage", "Delete Success!");
                } catch (JSONException ex) {
                    Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    returnMsg.put("errorMessage", "Delete Fail!");
                } catch (JSONException ex) {
                    Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } else {
            try {
                returnMsg.put("errorMessage", "No File found!");
            } catch (JSONException ex) {
                Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return returnMsg;

    }

    public JSONObject changeUserActiveStatus(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMessage = new JSONObject();

        String user_id = request.getParameter("user");
        String msg_action = request.getParameter("actn");

        try {
            try {
                Long loginId = Long.valueOf(user_id);
                loginPerson = registeredService.find(loginId);

                if (loginPerson != null) {
                    if (loginPerson.getPerson().getOrganization().getId().equals(loginBean.getPerson().getOrganization().getId())) {
                        loginPerson.setActive((msg_action.equalsIgnoreCase("Enable User?")));

                        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDLOGINSTATUS.toString(), new Date(), loginBean.getPerson().getFullName()
                                + " changed " + loginPerson.getPerson().getFullName() + " account active status", loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null,
                                loginBean.getPerson().getLogindetailId(), null, null);

                        activity.setEntityName(ApplicationEntityNames.LOGINDETAILS.toString());
                        activity.setEntityId(loginPerson.getId());

                        if (registeredService.update(loginPerson, activity)) {
                            returnMessage.put("success", "User account has been " + ((msg_action.equalsIgnoreCase("Enable User?")) ? "enabled" : "disabled") + " successfully");
                        } else {
                            returnMessage.put("errorMessage", "Update Failed");
                        }

                    } else {
                        returnMessage.put("errorMessage", "Permission Violation");
                    }
                } else {
                    returnMessage.put("errorMessage", "ID is invalid");
                }

            } catch (NumberFormatException ne) {
                returnMessage.put("errorMessage", "ID is not a valid number.");
                Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, "ID is not a valid number: " + user_id, ne);
            }
        } catch (JSONException ex) {
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnMessage;
    }

    private JSONObject deleteUnverifiedStreet(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMsg = new JSONObject();

        String file_id = request.getParameter("fileid");

        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.DELETESTREET.toString(), new Date(), loginBean.getPerson().getFullName() + " has deleted an unverified street, "
                + "the street ID is: " + file_id, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

        activity.setEntityName(ApplicationEntityNames.WARDSTREET.toString());
        activity.setEntityId(Long.valueOf(file_id));

        //Boolean deleted = lcdaService.deleteUnverifiedField(Long.valueOf(file_id), activity);
        try {
            if (lcdaService.deleteUnverifiedField(Long.valueOf(file_id), activity)) {

                returnMsg.put("retMessage", "Delete Success!");

            } else {
                returnMsg.put("retMessage", "Delete Fail!");
            }
        } catch (JSONException ex) {
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnMsg;
    }

    public JSONObject verifyNewStreetData(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMsg = new JSONObject();

        String file_id = request.getParameter("fileid");

        if ((file_id != null) && (!file_id.isEmpty())) {
            UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.DELETESTREET.toString(), new Date(), loginBean.getPerson().getFullName() + " has deleted an unverified street, "
                    + "the street ID is: " + file_id, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

            activity.setEntityName(ApplicationEntityNames.WARDSTREET.toString());
            activity.setEntityId(Long.valueOf(file_id));

            WardStreets street = lcdaService.findWardStreetByStreetId(Long.valueOf(file_id));
            street.setIsVerified(true);
            street.setVerifiedById(loginBean.getPerson().getLogindetailId());

            street = lcdaService.updateWardStreetInfo(street, activity);
            try {
                if (street != null) {
                    returnMsg.put("retMessage", "Verify Success!");
                } else {
                    returnMsg.put("retMessage", "verify Fail!");
                }
            } catch (JSONException ex) {
                Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);

            }

        }

        return returnMsg;
    }

    private JSONObject cleanupPropertiesStreet(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMsg = new JSONObject();

        String file_id = request.getParameter("properties");

        if ((file_id != null) && (!file_id.isEmpty())) {

            WardLandProperties property;
            Long streetId = Long.valueOf(request.getParameter("strid"));
            Map<String, ArrayList<String>> jsonMsg = new HashMap<>();
            ArrayList<String> succesMsg = new ArrayList<>();
            ArrayList<String> errorMsg = new ArrayList<>();

            UsersLastActivities activity;

            try {
                WardStreets street = lcdaService.findWardStreetByStreetId(streetId);
                if (street != null) {
                    JSONObject descrObj = new JSONObject(file_id);

                    for (Iterator<String> iterator = descrObj.keys(); iterator.hasNext();) {
                        String key = iterator.next();

                        property = landPropService.findWardPropertyById(descrObj.getLong(key));

                        if (property != null) {
                            activity = new UsersLastActivities(null, UserActitiyName.UPDATEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " has attached properties to verified street, "
                                    + "the street ID is: " + file_id, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

                            activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
                            activity.setEntityId(descrObj.getLong(key));
                            property.setIsIrregularAddress(false);
                            property.setIrregularAddress(null);
                            property.setWardStreetId(street);
                            property.setLastUpdatedById(loginBean.getPerson().getLogindetailId());

                            if (landPropService.updateWardLandPropertyInfo(property, null, activity)) {
                                succesMsg.add("Property with id: " + descrObj.getString(key) + " has been saved successfully");
                            } else {
                                errorMsg.add("Failed to update property with id: " + descrObj.getString(key));
                            }

                        } else {
                            errorMsg.add("Could not find property with id: " + descrObj.getString(key));
                        }
                    }
                } else {
                    errorMsg.add("Street not found!"); //this should never happen
                }

            } catch (JSONException ex) {
                Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            jsonMsg.put("successMessage", succesMsg);
            jsonMsg.put("errorMessage", errorMsg);

            returnMsg = new JSONObject(jsonMsg);

        }

        return returnMsg;
    }

    public JSONObject updatePropertyToVerified(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMsg = new JSONObject();

        String prop_id = request.getParameter("propID");
        WardLandProperties property;
        UsersLastActivities activity;

        try {
            if ((prop_id != null) && (!prop_id.isEmpty())) {

                property = landPropService.findWardPropertyById(Long.valueOf(prop_id));

                if (property != null) {
                    property.setIsVerified(Boolean.TRUE);
                    property.setIsInitsSynced(Boolean.TRUE);
                    property.setVerifiedById(loginBean.getPerson().getLogindetailId());
                    property.setSyncedById(loginBean.getPerson().getLogindetailId());
                    property.setVerifiedDate(new Date());
                    property.setInitsSyncDate(new Date());
                    //property.setPushStatus(ValuationStatusEnum.READYINITS.toString());
                    //property.setPropertyValuationStatus(ValuationStatusEnum.READYINITS.toString());
                    property.setPushStatus(ValuationStatusEnum.INITSPUSHED.toString());
                    property.setPropertyValuationStatus(ValuationStatusEnum.INITSPUSHED.toString());
                    property.setAdminComment(null);

                    activity = new UsersLastActivities(null, UserActitiyName.UPDATEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " has marked a property as verified,"
                            + " the property PIN is: " + property.getPropertyId(), loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null, loginBean.getPerson().getLogindetailId(),
                            null, null);

                    activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());
                    activity.setEntityId(property.getId());

                    if (landPropService.updateWardLandProperty(property, activity)) {
                        returnMsg.put("sucMessage", "Sync successful");
                        Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.INFO, "{0} marked verified by: {1}", new Object[]{property.getPropertyId(), loginBean.getPerson().getFullName()});
                    } else {
                        returnMsg.put("retMessage", "Failed to update property information");
                    }

                } else {
                    returnMsg.put("retMessage", "unknown property");
                }

            } else {
                returnMsg.put("retMessage", "unknown request");
            }
        } catch (JSONException ex) {
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);

        }

        return returnMsg;
    }

    public JSONObject delectProperty(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMsg = new JSONObject();

        String prop_id = request.getParameter("fileid");
        //WardLandProperties property;
        UsersLastActivities activity;

        try {
            if ((prop_id != null) && (!prop_id.isEmpty())) {
                if ((loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equalsIgnoreCase("ADMINISTRATOR"))
                        || (loginBean.getuRoles().getAuthenticationRoleId().getRoleName().equalsIgnoreCase("GEO-CODING OFFICER"))) {
                    activity = new UsersLastActivities(null, UserActitiyName.DELETEDPROPERTY.toString(), new Date(), loginBean.getPerson().getFullName() + " Deleted property information, "
                            + "property ID is: " + prop_id, loginBean.getPerson().getLogindetailId().getLastLoginIp(), null, null, null,
                            loginBean.getPerson().getLogindetailId(), null, null);

                    activity.setEntityName(ApplicationEntityNames.WARDLANDPROPERTIES.toString());

                    if (landPropService.deletePropertyData(Long.valueOf(prop_id), activity)) {
                        returnMsg.put("sucMessage", "Delete successful");
                    } else {
                        returnMsg.put("retMessage", "Delete Failed");
                    }
                } else {
                    returnMsg.put("retMessage", "Permission Error");
                }
            } else {
                returnMsg.put("retMessage", "unknown property");
            }
        } catch (JSONException ex) {
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnMsg;
    }

    public JSONObject markPaymentAsDuplicate(HttpServletRequest request) throws IOException, ServletException {
        JSONObject returnMsg = new JSONObject();
        String payment_id = request.getParameter("fileid");

        if ((payment_id != null) && (!payment_id.isEmpty())) {
            BillPayments payment = billService.findBillsPaymentById(Long.valueOf(payment_id), null);

            if (payment != null) { //this should always be the case

                try {
                    payment.setIsDuplicate(Boolean.TRUE);
                    payment = billService.saveBillPaymentInformation(payment, null, Boolean.FALSE);

                    if (payment.getIsProcessed()) { //this payment has been applied to the property, we need to remove it from the bill
                        //TODO I need to re-calculate the bill information
                    }
                    returnMsg.put("sucMessage", "Duplicate successful");
                } catch (JSONException ex) {
                    Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return returnMsg;
    }

    public JSONObject markDuplBillDeleted(HttpServletRequest request) throws IOException, ServletException {
        JSONObject returnMsg = new JSONObject();
        String bill_id = request.getParameter("fileid");

        if ((bill_id != null) && (!bill_id.isEmpty())) {
            PrintedBills printedBill = landPropService.findPrintedBillsById(Long.valueOf(bill_id));
            if (printedBill != null) { //this should always be the case here
                printedBill.setIsDeleted(Boolean.TRUE);
                try {
                    billService.saveUpdateBillInformation(printedBill, null);
                    returnMsg.put("sucMessage", "Duplicate successful");
                } catch (JSONException ex) {
                    Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return returnMsg;
    }

    public JSONObject signVisitorOut(HttpServletRequest request) throws IOException, ServletException {
        JSONObject returnMsg = new JSONObject();
        String bill_id = request.getParameter("fileid");

        if ((bill_id != null) && (!bill_id.isEmpty())) {

            Visitors visitor = complService.findVisitorById(Long.valueOf(bill_id));

            if (visitor != null) {
                visitor.setTimeOut(new Date());
            }

            visitor = complService.saveNewVisitorInformation(visitor, null, Boolean.FALSE);

            if (visitor != null) {
                returnMsg.put("sucMessage", "Success");
            } else {
                returnMsg.put("errMessage", "Error");
            }
        }

        return returnMsg;
    }

    public JSONObject changeDeviceAbleStatus(HttpServletRequest request) throws IOException, ServletException {

        JSONObject returnMessage = new JSONObject();

        String user_id = request.getParameter("user");
        String msg_action = request.getParameter("actn");

        try {

            RegisteredDevices device = registeredService.findRegisteredDeviceById(Long.valueOf(user_id));

            if (device != null) {

                device.setIsEnabled((msg_action.equalsIgnoreCase("Enable User?")));

                UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.UPDATEDLOGINSTATUS.toString(), new Date(), "Device enabled status changed",
                        null, null, null, null, loginBean.getPerson().getLogindetailId(), null, null);

                activity.setEntityName(ApplicationEntityNames.LOGINDETAILS.toString());
                activity.setEntityId(device.getId());

                if (registeredService.saveNewRegisteredDevice(device, activity)) {
                    returnMessage.put("success", "User device has been " + ((msg_action.equalsIgnoreCase("Enable User?")) ? "enabled" : "disabled") + " successfully");
                } else {
                    returnMessage.put("errorMessage", "Update Failed");
                }
            } else {
                returnMessage.put("errorMessage", "ID is invalid");
            }

        } catch (NumberFormatException ne) {
            returnMessage.put("errorMessage", "ID is not a valid number.");
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, "ID is not a valid number: " + user_id, ne);
        } catch (JSONException ex) {
            Logger.getLogger(AjaxSupportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnMessage;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
