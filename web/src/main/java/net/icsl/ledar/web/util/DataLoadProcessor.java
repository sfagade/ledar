package net.icsl.ledar.web.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;

import javax.inject.Inject;
import javax.inject.Named;

import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.web.bean.LoginBean;
import net.icsl.ledar.web.bean.UploadedFilesBean;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author sfagade Sep 29, 2015
 */
@Named(value = "dataProcesssor")
@ConversationScoped
public class DataLoadProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    LcdaWardsDataServices lcdaService;
    @Inject
    PrintedBillsService billService;
    @Inject
    private UploadedFilesBean uploadBean;
    @Inject
    private LoginBean loginBean;

    private final FacesContext context = FacesContext.getCurrentInstance();

    /**
     * @EJB AgentsService agentsService;
     *
     * @EJB InventoryService inventoryService;
     */
    private HSSFWorkbook workbook;

    /**
     * @return the uploadBean
     */
    public UploadedFilesBean getUploadBean() {
        return uploadBean;
    }

    /**
     * @param uploadBean the uploadBean to set
     */
    public void setUploadBean(UploadedFilesBean uploadBean) {
        this.uploadBean = uploadBean;
    }
}
