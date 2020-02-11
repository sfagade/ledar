/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.lazyModel;

import java.util.Comparator;
import net.icsl.ledar.ejb.model.PrintedBills;
import org.primefaces.model.SortOrder;

/**
 *
 * @author sfagade
 * @date Oct 10, 2017
 */
public class LazySorter implements Comparator<PrintedBills> {

    private String sortField;

    private SortOrder sortOrder;

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(PrintedBills car1, PrintedBills car2) {
        try {
            Object value1 = PrintedBills.class.getField(this.sortField).get(car1);
            Object value2 = PrintedBills.class.getField(this.sortField).get(car2);

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException();
        }
    }
}
