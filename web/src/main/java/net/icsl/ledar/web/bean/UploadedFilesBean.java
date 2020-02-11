/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;
import net.icsl.ledar.ejb.model.FileUploads;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.ejb.util.EmailSender;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author sfagade
 */
@Named("uploadedFilesBean")
@RequestScoped
public class UploadedFilesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private EmailSender emailSender;
    @Inject
    private LoginBean loginBean;

    private InputStream settingsStream;
    private Properties prop;

    /**
     * Creates a new instance of UploadedFilesBean
     */
    public UploadedFilesBean() {

        Long consultant_id = (loginBean != null) ? loginBean.getPerson().getOrganization().getId() : null;
        if (prop == null) {
            emailSender = new EmailSender(consultant_id);
        }
        prop = emailSender.getProperties();

    }
    
    @PostConstruct
    public void init() {
        /** if (emailSender == null) {
            emailSender = new EmailSender(loginBean.getPerson().getOrganization().getId());
            prop = emailSender.getProperties();
        } */
    }

    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String fileId = context.getExternalContext().getRequestParameterMap().get("fileId");
            FileUploads fileUpload = landPropService.findFileByUploadId(Long.valueOf(fileId));
            return new DefaultStreamedContent(new ByteArrayInputStream(fileUpload.getFileContent()));
        }
    }

    public boolean processFileUploadPictures(FileUploads fileUpload) {

        StreamedContent file_content;

//File webRoot = new File(absoluteWebPath);
        String file_name = getProp().getProperty("imageURl");
        File fileDir = new File(file_name);

        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        file_content = new DefaultStreamedContent(new ByteArrayInputStream(fileUpload.getFileContent()));

        File file = new File(file_name + "/" + fileUpload.getFileName());

        OutputStream out;
        try (InputStream inStream = file_content.getStream()) {
            out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();

            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(UploadedFilesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UploadedFilesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * @return the prop
     */
    public Properties getProp() {
        return prop;
    }

    /**
     * @param prop the prop to set
     */
    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public StreamedContent getLocalServerFile() throws IOException {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String fileId = context.getExternalContext().getRequestParameterMap().get("fileId");
            FileUploads fileUpload = landPropService.findFileByUploadId(Long.valueOf(fileId));
            InputStream targetStream;
            File file;

            if (fileUpload != null) {
                //this should always be the case 
                file = new File(fileUpload.getAbsolutePath() + "/" + fileUpload.getFileName());
                targetStream = new FileInputStream(file);

                return new DefaultStreamedContent(targetStream);
            }

            return null;
        }
    }
    
    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
}
