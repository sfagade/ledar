package net.icsl.ledar.web.lazyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * @author sfagade
 * @date Mar 1, 2016
 */
public class PrintedBillsLazy extends LazyDataModel<PrintedBills> {

    private static final long serialVersionUID = 1L;

    private PrintedBillsService printedService;

    private List<PrintedBills> printedBillsList;

    private Long consultantId;
    private Boolean deleted;
    private Map<String, String> criteria;
    private List<String> sorting;
    private String orderField;

    public PrintedBillsLazy() {
    }

    public PrintedBillsLazy(Long contractor_id, PrintedBillsService pService, Map<String, String> criteria, List<String> sorting, String order_field, Boolean deleted_) {
        this.printedService = pService;
        this.consultantId = contractor_id;
        this.deleted = deleted_;
        this.criteria = criteria;
        this.sorting = sorting;
        orderField = order_field;
    }

    @Override
    public PrintedBills getRowData(String rowKey) {
        for (PrintedBills bill : printedBillsList) {
            if (bill.getId().equals(rowKey)) {
                return bill;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(PrintedBills bill) {
        return bill.getId();
    }

    @Override
    public List<PrintedBills> load(int startingAt, int maxPerPage, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<PrintedBills> data = new ArrayList<>();

        try {
            if (criteria != null) {
                printedBillsList = printedService.fetchPrintedBillsFilter(criteria, startingAt, maxPerPage, sorting, orderField);
            } else {
                printedBillsList = printedService.fetchAllPrintedBills(consultantId, startingAt, maxPerPage, deleted);
            }
        } catch (Exception ex) {
            Logger.getLogger(PrintedBillsLazy.class.getName()).log(Level.SEVERE, "Error while lazy loading: ", ex);
        }

        //sort
        if (sortField != null) {
            Collections.sort(data, new LazySorter(sortField, sortOrder));
        }

        if (getRowCount() <= 0) {
        	if (criteria != null) {
        		this.setRowCount((int)printedService.countFilteredBillResults(criteria));
        	} else {
        	this.setRowCount(printedService.countBillsTotal(consultantId, null));
        	}
        }

        setPageSize(maxPerPage);

        return printedBillsList;
    }
}
