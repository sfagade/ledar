/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import net.icsl.ledar.ejb.dao.PrintedBillsDao;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.ejb.model.LcdaWards;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.web.util.FacesSupportUtil;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author sfagade
 * @author Dec 14, 2017
 */
@Named(value = "mapViewBean")
@SessionScoped
public class MapViewBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    private LcdaWardsDataServices lcdaService;
    @Inject
    private PrintedBillsDao billsDao;
    @Inject
    private LoginBean loginBean;
    @Inject
    private PrintedBillsService billsService;

    private MapModel deliveryModel;
    private LocalCouncilDevArea selectedLga, defaultLga;
    private PrintedBillsDto billDistrict;
    private LcdaWards selectedWard;
    private Marker marker;

    private final ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

    private List<PrintedBillsDto> billDistricts;
    private Map<String, String> mapCriteria;

    private String mapCenter;

    /**
     * Creates a new instance of MapViewBean
     */
    public MapViewBean() {
    }

    @PostConstruct
    public void init() {
        deliveryModel = new DefaultMapModel();
    }

    public void filterMapViewResult() {

        if (billDistrict != null) { //filtering set to district
            getMapCriteria().put("district", billDistrict.getDistrictName());
        } else if (selectedLga != null) {
            getMapCriteria().put("lga_name", selectedLga.getLcdaName());
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please select which area you want to reload", ""));
        }
    }

    public void onMarkerSelect(OverlaySelectEvent event) {
        marker = (Marker) event.getOverlay();
        System.out.println("Click even happened: " + marker);
    }

    public void initAllPropertyMaps() {

        //mapCriteria = new HashMap<>();
        String message_tip, icon_path;
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        deliveryModel = new DefaultMapModel();

        if (getMapCriteria() == null || mapCriteria.size() <= 0) {
            mapCriteria = new HashMap<>();
            getMapCriteria().put("lga_name", "Eti-Osa");
            getMapCriteria().put("is_deleted", "no");
        }

        if (mapCriteria.containsKey("has_payment")) {
            mapCriteria.remove("has_payment");
        }
        if (mapCriteria.containsKey("delivered")) {
            mapCriteria.remove("delivered");
        }

        defaultLga = lcdaService.fetchLocalGovtByCriteria(null, (selectedLga != null) ? selectedLga.getLcdaName() : getMapCriteria().get("lga_name"), null, null);
        mapCenter = defaultLga.getLatitude() + "," + defaultLga.getLongitude();

        //getMapCriteria().put("lga_name", "Eti-Osa");
        //getMapCriteria().put("is_deleted", "no");
        String server_path = origRequest.getScheme() + "://" + origRequest.getServerName();
        if (origRequest.getServerPort() > 0) {
            server_path += ":" + origRequest.getServerPort();
        }
        server_path += origRequest.getContextPath() + "/";

        List<PrintedBillsDto> billsDto = billsDao.deliveredBillsList(getMapCriteria()); //fetch every bill that has payment on them

        if (billsDto != null && billsDto.size() > 0) {
            for (PrintedBillsDto dto : billsDto) {
                if (dto.getLatitude() != null && !dto.getLatitude().isEmpty()) { //check if the bill has gps co-ordinates
                    message_tip = dto.getPropertyIdn() + " - " + dto.getHouseAddress() + "\n" + dto.getTaxCategory();
                    icon_path = server_path + "resources/img/icons/enum_bill.png";

                    deliveryModel.addOverlay(new Marker(new LatLng(Double.valueOf(dto.getLatitude()), Double.valueOf(dto.getLongitude())), message_tip, "img/icons/delivery.png", icon_path));
                }
            }
        }

    }

    public void initMapReportOverview() {

        String[] accepted_roles = {"APPLICATION ADMINISTRATOR", "ADMINISTRATOR", "HEAD OF OPERATIONS"};

        if (!FacesSupportUtil.isUserInRole(accepted_roles)) {
            try {
                extContext.redirect(extContext.getRequestContextPath() + "/app/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(MapViewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String message_tip, icon_path;
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        deliveryModel = new DefaultMapModel();

        if (getMapCriteria() == null || mapCriteria.size() <= 0) {
            mapCriteria = new HashMap<>();
            getMapCriteria().put("lga_name", "Eti-Osa");
            getMapCriteria().put("is_deleted", "no");
        }

        if (mapCriteria.containsKey("has_payment")) {
            mapCriteria.remove("has_payment");
        }
        if (mapCriteria.containsKey("delivered")) {
            mapCriteria.remove("delivered");
        }

        defaultLga = lcdaService.fetchLocalGovtByCriteria(null, (selectedLga != null) ? selectedLga.getLcdaName() : getMapCriteria().get("lga_name"), null, null);
        mapCenter = defaultLga.getLatitude() + "," + defaultLga.getLongitude();

        String server_path = origRequest.getScheme() + "://" + origRequest.getServerName();
        if (origRequest.getServerPort() > 0) {
            server_path += ":" + origRequest.getServerPort();
        }
        server_path += origRequest.getContextPath() + "/";

        List<PrintedBillsDto> billsDto = billsDao.deliveredBillsList(getMapCriteria()); //fetch every bill that has payment on them

        if (billsDto != null && billsDto.size() > 0) {
            for (PrintedBillsDto dto : billsDto) {
                if (dto.getLatitude() != null && !dto.getLatitude().isEmpty()) { //check if the bill has gps co-ordinates
                    message_tip = dto.getPropertyIdn() + " - " + dto.getHouseAddress() + "\n" + dto.getTaxCategory() + "\nOfficer: " + dto.getName();

                    if (dto.getHasMatchPayment()) { //Bill has been paid for
                        icon_path = server_path + "resources/img/icons/paid_bill.png";
                    } else if (dto.getIsDelivered()) { //bill is delivered, but has not matching payment 
                        icon_path = server_path + "resources/img/icons/delivered_bill.png";
                    } else { //bill exist, but does not have any delivery and payment info 
                        icon_path = server_path + "resources/img/icons/enum_bill.png";
                    }

                    deliveryModel.addOverlay(new Marker(new LatLng(Double.valueOf(dto.getLatitude()), Double.valueOf(dto.getLongitude())), message_tip, "delivery.png", icon_path));
                }
            }
        }

    }

    public void initDeliveredPropertyMaps() {

        //Map<String, String> criteria = new HashMap<>();
        String message_tip, icon_path;
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        deliveryModel = new DefaultMapModel();

        if (getMapCriteria() == null || mapCriteria.size() <= 0) {
            mapCriteria = new HashMap<>();
            getMapCriteria().put("lga_name", "Eti-Osa");
            getMapCriteria().put("is_deleted", "no");
        }
        defaultLga = lcdaService.fetchLocalGovtByCriteria(null, (selectedLga != null) ? selectedLga.getLcdaName() : getMapCriteria().get("lga_name"), null, null);
        mapCenter = defaultLga.getLatitude() + "," + defaultLga.getLongitude();

        //mapCriteria.put("delivered", "yes");
        if (mapCriteria.containsKey("has_payment")) {
            mapCriteria.remove("has_payment");
        }

        String server_path = origRequest.getScheme() + "://" + origRequest.getServerName();
        if (origRequest.getServerPort() > 0) {
            server_path += ":" + origRequest.getServerPort();
        }
        server_path += origRequest.getContextPath() + "/";

        List<PrintedBillsDto> billsDto = billsDao.deliveredBillsList(mapCriteria); //fetch every bill that has payment on them

        if (billsDto != null && billsDto.size() > 0) {
            for (PrintedBillsDto dto : billsDto) {
                if (dto.getLatitude() != null && !dto.getLatitude().isEmpty()) { //check if the bill has gps co-ordinates
                    message_tip = dto.getPropertyIdn() + " - " + dto.getHouseAddress() + "\n" + dto.getTaxCategory() + "\nOfficer: " + dto.getName();
                    if (dto.getIsDelivered()) { //bill is delivered, but has not matching payment 
                        icon_path = server_path + "resources/img/icons/delivered_bill.png";
                    } else {
                        icon_path = server_path + "resources/img/icons/enum_bill.png";
                    }

                    deliveryModel.addOverlay(new Marker(new LatLng(Double.valueOf(dto.getLatitude()), Double.valueOf(dto.getLongitude())), message_tip, "img/icons/delivery.png", icon_path));
                }
            }
        }
    }

    public void initPaidPropertyMaps() {
        //Map<String, String> criteria = new HashMap<>();
        String message_tip, icon_path;
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        deliveryModel = new DefaultMapModel();

        if (getMapCriteria() == null || mapCriteria.size() <= 0) {
            mapCriteria = new HashMap<>();
            getMapCriteria().put("lga_name", "Eti-Osa");
            getMapCriteria().put("is_deleted", "no");
        }
        defaultLga = lcdaService.fetchLocalGovtByCriteria(null, (selectedLga != null) ? selectedLga.getLcdaName() : getMapCriteria().get("lga_name"), null, null);
        mapCenter = defaultLga.getLatitude() + "," + defaultLga.getLongitude();

        mapCriteria.put("has_payment", "true");

        String server_path = origRequest.getScheme() + "://" + origRequest.getServerName();
        if (origRequest.getServerPort() > 0) {
            server_path += ":" + origRequest.getServerPort();
        }
        server_path += origRequest.getContextPath() + "/";

        List<PrintedBillsDto> billsDto = billsDao.deliveredBillsList(mapCriteria); //fetch every bill that has payment on them

        if (billsDto != null && billsDto.size() > 0) {
            for (PrintedBillsDto dto : billsDto) {
                if (dto.getLatitude() != null && !dto.getLatitude().isEmpty()) { //check if the bill has gps co-ordinates
                    message_tip = dto.getPropertyIdn() + " - " + dto.getHouseAddress() + "\n" + dto.getTaxCategory() + "\nOfficer: " + dto.getName();
                    icon_path = server_path + "resources/img/icons/paid_bill.png";

                    deliveryModel.addOverlay(new Marker(new LatLng(Double.valueOf(dto.getLatitude()), Double.valueOf(dto.getLongitude())), message_tip, "img/icons/delivery.png", icon_path));
                }
            }
        }
    }

    public void changedLcda() {

        if (selectedLga != null) {

            billDistricts = new ArrayList<>();

            if ((selectedLga != null)) {
                List<Object[]> districts = billsService.fetchBillsDistrictByLgName(selectedLga.getLcdaName());

                if ((districts != null) && (districts.size() > 0)) {
                    for (Object[] bill : districts) {
                        billDistricts.add(new PrintedBillsDto(null, null, null, bill[0].toString(), bill[1].toString(), null));
                    }
                }
            }
        }

    }

    public MapModel getDeliveryModel() {
        return deliveryModel;
    }

    public void setDeliveryModel(MapModel deliveryModel) {
        //   this.deliveryModel = deliveryModel;
    }

    public LocalCouncilDevArea getDefaultLga() {
        return defaultLga;
    }

    public void setDefaultLga(LocalCouncilDevArea defaultLga) {
        this.defaultLga = defaultLga;
    }

    public LocalCouncilDevArea getSelectedLga() {
        return selectedLga;
    }

    public void setSelectedLga(LocalCouncilDevArea selectedLga) {
        this.selectedLga = selectedLga;
    }

    public String getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(String mapCenter) {
        this.mapCenter = mapCenter;
    }

    /* @return the billDistrict
     */
    public PrintedBillsDto getBillDistrict() {
        return billDistrict;
    }

    /**
     * @param billDistrict the billDistrict to set
     */
    public void setBillDistrict(PrintedBillsDto billDistrict) {
        this.billDistrict = billDistrict;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the billDistricts
     */
    public List<PrintedBillsDto> getBillDistricts() {
        return billDistricts;
    }

    /**
     * @param billDistricts the billDistricts to set
     */
    public void setBillDistricts(List<PrintedBillsDto> billDistricts) {
        this.billDistricts = billDistricts;
    }

    /**
     * @return the mapCriteria
     */
    public Map<String, String> getMapCriteria() {
        return mapCriteria;
    }

    /**
     * @param mapCriteria the mapCriteria to set
     */
    public void setMapCriteria(Map<String, String> mapCriteria) {
        this.mapCriteria = mapCriteria;
    }

    /* @return the selectedWard
     */
    public LcdaWards getSelectedWard() {
        return selectedWard;
    }

    /**
     * @param selectedWard the selectedWard to set
     */
    public void setSelectedWard(LcdaWards selectedWard) {
        this.selectedWard = selectedWard;
    }

    public Marker getMarker() {
        return marker;
    }
}
