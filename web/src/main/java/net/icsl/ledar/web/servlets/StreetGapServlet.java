/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.servlets;

import net.icsl.ledar.ejb.enums.PropertyUpdateStatusEnum;
import net.icsl.ledar.ejb.model.*;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;
import net.icsl.ledar.web.bean.UploadedFilesBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.primefaces.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

/**
 * @author sfagade
 */
@WebServlet(name = "StreetGapServlet", urlPatterns = {"/streetGapServlet"})
public class StreetGapServlet extends HttpServlet {

    @Inject
    private ReferenceDataService refService;
    @Inject
    private RegisteredUsersDataService registeredService;
    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private UploadedFilesBean uploadBean;

    private final DateFormat shortDateFormat = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);

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

        if ((formAction != null) && (!formAction.isEmpty())) {
            if (formAction.equalsIgnoreCase("saveStreetGapInfo")) {
                returnMsg = this.saveStreetGapProperty(request);
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

    private JSONObject saveStreetGapProperty(HttpServletRequest request) throws IOException, ServletException {
        JSONObject returnMessage = new JSONObject();

        String username = request.getParameter("username"), password = request.getParameter("pwd");
        String irregular_address, house_no, remarks, latitude, longitude;
        String lga_name = request.getParameter("lga_name");
        Date deliveryDate;
        WardStreets street;
        List<StreetGapFiles> gapFiles = new ArrayList<>();

        if ((username != null && !username.isEmpty()) && (password != null && !password.isEmpty())) {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password, false));
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
            People person = registeredService.findUserByUsername(username);

            byte[] decoded;

            String imageStr = request.getParameter("frontView");
            String imageStr2 = request.getParameter("sideView");
            String imageStr3 = request.getParameter("rearView");

            String fvFilename = request.getParameter("fv_filename");
            String svFilename = request.getParameter("sv_filename");
            String rvFilename = request.getParameter("rv_filename");

            String fvMimetype = request.getParameter("fv_mimetype");
            String svMimetype = request.getParameter("sv_mimetype");
            String rvMimetype = request.getParameter("rv_mimetype");

            String download_dir = uploadBean.getProp().getProperty("imageURl") + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/gap_pictures/" + shortDateFormat.format(new Date()) + "/" + lga_name;

            File download_folder = new File(download_dir);

            if (!download_folder.exists()) { //this will be the case for the first upload done for the day
                download_folder.mkdirs(); //create folder
            }

            try {
                deliveryDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS", Locale.ENGLISH).parse(request.getParameter("discovery_date"));

                Long district_id = (request.getParameter("district_id") != null) ? Long.valueOf(request.getParameter("district_id")) : null;
                Long street_id = (request.getParameter("street_id") != null) ? Long.valueOf(request.getParameter("street_id")) : null;
                remarks = request.getParameter("remarks");
                Logger.getLogger(StreetGapServlet.class.getName()).log(Level.INFO, "About to save Gap Information -- {0} -- {1}", new Object[]{district_id, remarks});

                if (district_id != null && remarks != null && imageStr != null) { //this field is required
                    street = (street_id != null) ? refService.findStreetById(street_id) : null;
                    irregular_address = request.getParameter("irregular_address");
                    house_no = request.getParameter("house_no");
                    latitude = request.getParameter("latitude");
                    longitude = request.getParameter("longitude");

                    StreetGaps newGap = new StreetGaps(null, irregular_address, house_no, remarks, PropertyUpdateStatusEnum.STREETGAPINIT.toString(), latitude, longitude, person.getLogindetailId(), refService.findLcdaWardsById(district_id), street,
                            null, null);
                    newGap.setConsultantId(person.getOrganization());
                    newGap.setDiscoveryDate(deliveryDate);

                    File exist_file = new File(download_dir + "/" + fvFilename.trim());
                    if ((!exist_file.exists())) { //this should always be the case
                        decoded = Base64.decodeBase64(imageStr);
                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + fvFilename.trim())))) {
                            stream.write(decoded);
                        }

                        gapFiles.add(new StreetGapFiles(null, new FileUploads(null, null, fvFilename, fvMimetype, "Front View", null, person.getLogindetailId(), download_dir, null, null), newGap, null, null));
                    }

                    if (imageStr2 != null) {
                        exist_file = new File(download_dir + "/" + svFilename.trim());
                        if ((!exist_file.exists())) {
                            decoded = Base64.decodeBase64(imageStr2);

                            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + svFilename.trim())))) {
                                stream.write(decoded);
                            }

                            gapFiles.add(new StreetGapFiles(null, new FileUploads(null, null, svFilename, svMimetype, "Side View", null, person.getLogindetailId(), download_dir, null, null), newGap, null, null));
                        }
                    }

                    if (imageStr3 != null) {
                        exist_file = new File(download_dir + "/" + rvFilename.trim());
                        if ((!exist_file.exists())) {
                            decoded = Base64.decodeBase64(imageStr3);
                            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(download_dir + "/" + rvFilename.trim())))) {
                                stream.write(decoded);
                            }
                            gapFiles.add(new StreetGapFiles(null, new FileUploads(null, null, rvFilename, rvMimetype, "Rear View", null, person.getLogindetailId(), download_dir, null, null), newGap, null, null));
                        }
                    }

                    newGap = landPropService.saveUpdateStreetGapData(newGap, gapFiles, null, Boolean.TRUE);

                    if (newGap.getId() != null) {
                        Logger.getLogger(StreetGapServlet.class.getName()).log(Level.INFO, "{0} saved Gap Information ", new Object[]{person.getFullName()});
                        returnMessage.put("successMessage", "New Street Gap saved successfully!");
                    } else {
                        returnMessage.put("errorMessage", "Failed to save new Street Gap!");
                    }

                } else {
                    returnMessage.put("errorMessage", "District and Remarks are required!");
                    Logger.getLogger(StreetGapServlet.class.getName()).log(Level.SEVERE, "Validation failed District -> {0} -- remarks -> {1}", new Object[]{district_id, remarks});
                }
            } catch (ParseException ex) {
                Logger.getLogger(StreetGapServlet.class.getName()).log(Level.SEVERE, "Failed to convert date for Gap -> {0}", new Object[]{request.getParameter("discovery_date")});
                returnMessage.put("errorMessage", "Wrong date-time format!");
            }

        } else {
            returnMessage.put("errorMessage", "Username or Password not set!");
        }

        return returnMessage;
    }
}
