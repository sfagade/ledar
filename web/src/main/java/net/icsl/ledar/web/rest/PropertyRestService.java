/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.web.bean.LoginBean;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 * REST Web Service
 *
 * @author sfagade
 */
@Path("propertyService")
@RequestScoped
public class PropertyRestService {

    @Context
    private UriInfo context;

    @Inject
    private LandPropertiesDataService landPropService;

    @Inject
    private LoginBean loginBean;

    /**
     * Creates a new instance of PropertyRestService
     */
    public PropertyRestService() {
    }

    @GET
    @Path("/deletePropertyFile/{fileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject deletePropertyFile(@PathParam("fileId") String file_id) {

        JSONObject returnMsg = new JSONObject();

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
                    Logger.getLogger(PropertyRestService.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    returnMsg.put("errorMessage", "Delete Fail!");
                } catch (JSONException ex) {
                    Logger.getLogger(PropertyRestService.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } else {
            try {
                returnMsg.put("errorMessage", "No File found!");
            } catch (JSONException ex) {
                Logger.getLogger(PropertyRestService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return returnMsg;

    }
}
