/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.icsl.ledar.ejb.util.EptsApiDataProcessor;
import net.icsl.ledar.ejb.util.LedarOutsideGateway;
import net.icsl.ledar.web.bean.LoginBean;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author sfagade
 */
@WebServlet(name = "EptsIntegrationServlet", urlPatterns = {"/eptsIntegration"})
public class EptsIntegrationServlet extends HttpServlet {

    @Inject
    private LoginBean loginBean;
    @Inject
    private EptsApiDataProcessor outsideGateway;

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

        if (formAction != null && !formAction.isEmpty()) {
            if (formAction.equals("eptsBId")) {
                returnMsg = fetchEptsNoticeId(request);
            }
        } else {
            returnMsg.put("errorMessage", "You need to tell me what u want to do dude!");
        }
        
        response.setContentType("application/json");
        response.getWriter().write(returnMsg.toString());
    }

    private JSONObject fetchEptsNoticeId(HttpServletRequest request) {

        JSONObject returnMsg = new JSONObject();

        String noticeNo = request.getParameter("bid");
        if (noticeNo != null && !noticeNo.isEmpty()) {

            try {

                String noticeId = outsideGateway.fetchEptsNoticeId(noticeNo);

                if (noticeId != null) {

                    returnMsg.put("successMessage", noticeId);

                } else {
                    returnMsg.put("errorMessage", "Record not found!");
                }
            } catch (IOException | JSONException ex) {
                returnMsg.put("errorMessage", "Network error");
                Logger.getLogger(EptsIntegrationServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            returnMsg.put("errorMessage", "Unknown number!");
        }

        return returnMsg;
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
