/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.icsl.ledar.ejb.enums.ApplicationEntityNames;
import net.icsl.ledar.ejb.enums.UserActitiyName;
import net.icsl.ledar.ejb.model.ComplainerRelationship;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.People;
import net.icsl.ledar.ejb.model.UsersLastActivities;
import net.icsl.ledar.ejb.model.VisitPurpose;
import net.icsl.ledar.ejb.model.Visitors;
import net.icsl.ledar.ejb.service.ComplaintDataService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.primefaces.json.JSONObject;

/**
 *
 * @author sfagade
 */
@WebServlet(name = "VisitorsRemoteServlet", urlPatterns = {"/visitorsService"})
public class VisitorsRemoteServlet extends HttpServlet {

    @Inject
    private ReferenceDataService refService;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private ComplaintDataService compliService;

    private People person;

    private final DateFormat shortDateFormat = new SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH);
    private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS", Locale.ENGLISH);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject returnMsg = new JSONObject();
        String formAction = request.getParameter("form_action");

        if ((formAction != null) && (!formAction.isEmpty())) {
            if (formAction.equalsIgnoreCase("saveVisitor")) {
                returnMsg = this.saveNewVisitorData(request);
            }
        } else {
            returnMsg.put("errorMessage", "You need to tell me what u want to do dude!");
        }

        response.setContentType("application/json");
        response.getWriter().write(returnMsg.toString());

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

    private JSONObject saveNewVisitorData(HttpServletRequest request) throws IOException, ServletException {
        JSONObject returnMessage = new JSONObject();
        Date owner_dob = null, timeIn = null;
        List<ComplainerRelationship> relationships;
        List<VisitPurpose> purposes;
        LcdaWards district;

        String property_id = request.getParameter("property_id");
        String v_last_name = request.getParameter("last_name");
        String v_first_name = request.getParameter("first_name");
        Long purpose_id = (request.getParameter("purpose_id") != null && !request.getParameter("purpose_id").isEmpty()) ? Long.valueOf(request.getParameter("purpose_id")) : null;
        Long district_id = (request.getParameter("district_id") != null && !request.getParameter("district_id").isEmpty()) ? Long.valueOf(request.getParameter("district_id")) : null;
        String address_str = request.getParameter("address");
        String property_no = request.getParameter("property_no");
        String email_addr = request.getParameter("email_address");
        Long relationship_id = (request.getParameter("relationship_id") != null && !request.getParameter("relationship_id").isEmpty()) ? Long.valueOf(request.getParameter("relationship_id")) : null;
        String owner_last_name = request.getParameter("owner_last_name");
        String owner_first_name = request.getParameter("owner_first_name");
        String owner_phone = request.getParameter("owner_phone_no");
        String owner_email = request.getParameter("owner_email_address");
        String pword = request.getParameter("pwd");
        String username = request.getParameter("username");
        if ((username != null) && (pword != null)) {

            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, pword, false));
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

            if ((currentUser.hasRole("CUSTOMER SERVICE OFFICER")) || (currentUser.hasRole("COMPLAINTS ADMINISTRATOR"))) {
                person = registeredService.findUserByUsername(username);
                try {
                    owner_dob = (request.getParameter("owner_dob") != null && !request.getParameter("owner_dob").isEmpty()) ? shortDateFormat.parse(request.getParameter("owner_dob")) : null;
                    timeIn = (request.getParameter("time_in") != null && !request.getParameter("time_in").isEmpty()) ? dateFormat.parse(request.getParameter("time_in")) : null;
                } catch (ParseException ex) {
                    Logger.getLogger(VisitorsRemoteServlet.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (relationship_id != null && purpose_id != null && district_id != null && timeIn != null) {
                    relationships = refService.fetchAllRelationships(relationship_id);
                    purposes = refService.fetchAllVisitPurposes(purpose_id);
                    district = refService.findLcdaWardsById(district_id);

                    if (relationships != null && relationships.size() > 0 && purposes != null && purposes.size() > 0 && district != null) {
                        if (!relationships.get(0).getRelationshipName().equals("OWNER")) { //owner information is required @ this point
                            if (owner_first_name == null || owner_first_name.isEmpty() || owner_last_name == null || owner_last_name.isEmpty()) {
                                returnMessage.put("errorMessage", "Owner information is missing!");
                                return returnMessage;
                            }
                        }

                        Visitors currentVisitor = new Visitors(null, v_first_name, property_id, v_last_name, email_addr, property_no, address_str, timeIn, null, person.getLogindetailId(), purposes.get(0),
                                person.getOrganization(), district, owner_last_name, owner_first_name, owner_phone, owner_email, owner_dob, relationships.get(0), null, null);

                        UsersLastActivities activity = new UsersLastActivities(null, UserActitiyName.SAVEVISIT.toString(), new Date(), person.getFullName() + " saved new visitor  information",
                                person.getLogindetailId().getLastLoginIp(), null, null, null, person.getLogindetailId(), null, null);

                        activity.setEntityName(ApplicationEntityNames.VISITOR.toString());

                        currentVisitor = compliService.saveNewVisitorInformation(currentVisitor, activity, Boolean.TRUE);

                        if (currentVisitor != null) {
                            returnMessage.put("successMessage", "Visit has been saved successfully!");
                            Logger.getLogger(VisitorsRemoteServlet.class.getName()).log(Level.INFO, "Visit saved successfully for {0}", person.getFullName());
                        } else {
                            returnMessage.put("errorMessage", "Failed to save visit, please try again later!");
                            Logger.getLogger(VisitorsRemoteServlet.class.getName()).log(Level.SEVERE, "Failed to save visit");
                        }

                    } else {
                        returnMessage.put("errorMessage", "Entity not found with ID!");
                    }

                } else {
                    returnMessage.put("errorMessage", "Required validation failed!");
                    Logger.getLogger(VisitorsRemoteServlet.class.getName()).log(Level.SEVERE, "Validation failed");
                }
            } else {
                returnMessage.put("errorMessage", "Officer does not have the appropriate role to perform this action");
            }
        } else {
            returnMessage.put("errorMessage", "Officer username or password is missing");
        }

        return returnMessage;
    }
}
