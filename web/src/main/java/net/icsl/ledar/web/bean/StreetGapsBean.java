package net.icsl.ledar.web.bean;

import net.icsl.ledar.ejb.model.StreetGapFiles;
import net.icsl.ledar.ejb.model.StreetGaps;
import net.icsl.ledar.ejb.service.LandPropertiesDataService;
import net.icsl.ledar.web.util.FacesSupportUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("streetGapBean")
@RequestScoped
public class StreetGapsBean {

    @Inject
    private LandPropertiesDataService landPropService;
    @Inject
    private FacesContext facesContext;
    @Inject
    private LoginBean loginBean;

    private List<StreetGaps> streetGaps;
    private List<StreetGapFiles> streetGapFiles;

    private StreetGaps selectedGap;
    private long recordCount;
    private String gapId;

    public StreetGapsBean() {
    }

    public void initStreetGapDetails() {
        Map<String, String> criteria = new HashMap<>();
        if (gapId != null) {
            criteria.put("gapId", gapId);
            List<StreetGaps> streets = landPropService.fetchStreetGapsWithCriteria(criteria, null, null);

            if (streets != null && streets.size() > 0) {
                selectedGap = streets.get(0);
                streetGapFiles = landPropService.fecthStreetGapFilesByGapId(selectedGap.getId());
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not find street gap information, please try again later or contact support!", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("streetGapReport.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(BillingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    public void initStreetGapsView() {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("consultant", loginBean.getPerson().getOrganization().getId() + "");
        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "GEO-CODING OFFICER"};
        String order_dir = (FacesSupportUtil.isUserInRole(accepted_roles)) ? "desc" : "asc";
        
        List<String> sorting = new ArrayList<>();
        sorting.add("discoveryDate");
        
        streetGaps = landPropService.fetchStreetGapsWithCriteria(criteria,sorting, order_dir);
        recordCount = (streetGaps != null) ? streetGaps.size() : 0;
    }

    public List<StreetGaps> getStreetGaps() {
        return streetGaps;
    }

    public void setStreetGaps(List<StreetGaps> streetGaps) {
        this.streetGaps = streetGaps;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public StreetGaps getSelectedGap() {
        return selectedGap;
    }

    public void setSelectedGap(StreetGaps selectedGap) {
        this.selectedGap = selectedGap;
    }

    public String getGapId() {
        return gapId;
    }

    public void setGapId(String gapId) {
        this.gapId = gapId;
    }

    public List<StreetGapFiles> getStreetGapFiles() {
        return streetGapFiles;
    }

    public void setStreetGapFiles(List<StreetGapFiles> streetGapFiles) {
        this.streetGapFiles = streetGapFiles;
    }
}
