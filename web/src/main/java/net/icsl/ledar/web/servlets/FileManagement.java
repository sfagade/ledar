/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;

/**
 *
 * @author sfagade
 */
@WebServlet(name = "FileManagement", urlPatterns = {"/fileManagement"})
public class FileManagement extends HttpServlet {

    @Inject
    private LandPropertiesDataService landPropService;
    
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
        response.setContentType("text/html;charset=UTF-8");
        
        String fileId = request.getParameter("fileid");
        FileUploads fileUpload = landPropService.findFileByUploadId(Long.valueOf(fileId));
        
        response.setContentType(fileUpload.getFileType());
        response.setContentLength(fileUpload.getFileContent().length);
        response.setHeader("Content-Disposition", "inline;filename=\"" + URLEncoder.encode(fileUpload.getFileName(), "UTF-8") + "\"");
        response.getOutputStream().write(fileUpload.getFileContent());
        
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
