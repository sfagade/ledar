package net.icsl.ledar.web.lazyModel;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.icsl.ledar.ejb.model.WardLandProperties;
import net.icsl.ledar.ejb.service.LcdaWardsDataServices;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LandPropertiesLazy extends LazyDataModel<WardLandProperties> {

    private static final long serialVersionUID = 1L;

    private LcdaWardsDataServices landPropService;

    private List<WardLandProperties> wardProperties;

    private Long contractor, recordCount;
    private Boolean isVerified;

    public LandPropertiesLazy() {

    }

    public LandPropertiesLazy(Long contractor_id, LcdaWardsDataServices landPropService, Boolean verified) {
        this.landPropService = landPropService;
        contractor = contractor_id;
        this.isVerified = verified;
    }

    @Override
    public List<WardLandProperties> load(int startingAt, int maxPerPage, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
            // with datatable pagination limits
            wardProperties = landPropService.fetchAllLandProperties(contractor, isVerified, startingAt, maxPerPage);

        } catch (Exception ex) {
            Logger.getLogger(LandPropertiesLazy.class.getName()).log(Level.SEVERE, "Error while lazy loading: ", ex);
        }

        // set the total of wardProperties
        if (getRowCount() <= 0) {
            setRowCount(landPropService.countPropertiesTotal(contractor, false, null));
        }

        // set the page dize
        setPageSize(maxPerPage);

        return wardProperties;
    }

    @Override
    public Object getRowKey(WardLandProperties player) {
        return player.getId();
    }

    @Override
    public WardLandProperties getRowData(String playerId) {
        Integer id = Integer.valueOf(playerId);

        for (WardLandProperties player : wardProperties) {
            if (id.equals(player.getId())) {
                return player;
            }
        }

        return null;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
}
