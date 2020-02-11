/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.service.PrintedBillsService;

/**
 *
 * @author sfagade
 */
@Stateless
@LocalBean
public class PrintedBillsDao {

    @Inject
    PrintedBillsService billService;

    public List<PrintedBillsDto> deliveredBillsList(Map<String, String> criteria) {

        List<PrintedBills> printedBills = billService.fetchAllBillsForProcessing(criteria);
        List<PrintedBillsDto> billsDtos = null;
        PrintedBillsDto dto;
        String address, delivery_person;

        if (printedBills != null && printedBills.size() > 0) {
            billsDtos = new ArrayList<>();

            for (PrintedBills bill : printedBills) {
                address = (bill.getHouseNo() != null && !bill.getHouseNo().isEmpty()) ? bill.getHouseNo() + ", " : "";
                address += (bill.getPlotNo() != null && !bill.getPlotNo().isEmpty()) ? bill.getPlotNo() + ", " : "";
                address += (bill.getBlockNo() != null && !bill.getBlockNo().isEmpty()) ? bill.getBlockNo() + ", " : "";
                address += (bill.getFlatNo() != null && !bill.getFlatNo().isEmpty()) ? bill.getFlatNo() + ", " : "";

                address += bill.getStreetName() + ", " + bill.getDistrictName() + ", " + bill.getLga();
                delivery_person = (bill.getDeliveryLogindetailId() != null) ? bill.getDeliveryLogindetailId().getPerson().getFullName() : "";

                dto = new PrintedBillsDto(bill.getId(), bill.getPropertyIdn(), bill.getPayerId(), bill.getLga(), bill.getBankPaymentCode(), address, bill.getLongitude(), bill.getLatitude(),
                        bill.getHasMatchPayment(), bill.getIsDelivered(), delivery_person, bill.getTaxCategory(),bill.getCreated());
                billsDtos.add(dto);
            }
        }

        return billsDtos;
    }
}
