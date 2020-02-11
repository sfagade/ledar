package net.icsl.ledar.web.lazyModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.icsl.ledar.ejb.model.BillPayments;
import net.icsl.ledar.ejb.service.PrintedBillsService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * @author sfagade
 * @date Mar 1, 2016
 */
public class BillsPaymentLazy extends LazyDataModel<BillPayments> {

    private static final long serialVersionUID = 1L;

    private PrintedBillsService billsService;

    private List<BillPayments> billsPayment;

    private Map<String, String> criteria;
    private List<String> sorting;
    private String orderField;
    private Boolean duplicate;
    private Long consultantId;

    public BillsPaymentLazy() {
    }

    public BillsPaymentLazy(Long consultant, PrintedBillsService pService, Map<String, String> criteria, List<String> sorting, String order_field, Boolean duplicate_) {
        this.billsService = pService;
        this.criteria = criteria;
        this.sorting = sorting;
        orderField = order_field;
        this.duplicate = duplicate_;
        this.consultantId = consultant;
    }

    @Override
    public BillPayments getRowData(String rowKey) {
        for (BillPayments bill : billsPayment) {
            if (bill.getId().equals(rowKey)) {
                return bill;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(BillPayments bill) {
        return bill.getId();
    }

    @Override
    public List<BillPayments> load(int startingAt, int maxPerPage, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        try {
            if (criteria != null) {
                billsPayment = billsService.fetchAllBillsPayments(criteria, startingAt, maxPerPage, sorting, orderField);
            } else {

            }
        } catch (Exception ex) {
            Logger.getLogger(BillsPaymentLazy.class.getName()).log(Level.SEVERE, "Error while lazy loading: ", ex);
        }

        //sort
        if (sortField != null) {
            Collections.sort(billsPayment, new BillsPaymentSorter(sortField, sortOrder));
        }

        if (getRowCount() <= 0) {
            this.setRowCount(billsService.countTotalPayments(criteria));
        }

        setPageSize(maxPerPage);

        return billsPayment;
    }
}
