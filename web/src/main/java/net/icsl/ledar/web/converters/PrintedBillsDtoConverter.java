package net.icsl.ledar.web.converters;


import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.service.PrintedBillsService;
import net.icsl.ledar.ejb.dto.PrintedBillsDto;


@ManagedBean(name = "priBillsConverterBean")
@FacesConverter(value = "priBillConverter")
public class PrintedBillsDtoConverter implements Converter { //this converter is for the street dto

    
    @Inject
    private PrintedBillsService billsService;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

        if (!value.contains("Select")) {
        PrintedBills pBills = billsService.findSingleBillByArea(null, value, null);
        PrintedBillsDto pBillDto = new PrintedBillsDto(pBills.getId(),pBills.getPropertyIdn(),pBills.getStreetName(),pBills.getDistrictName(),pBills.getLga(),pBills.getCreated());
        return pBillDto;
        } else {
        	return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
    	PrintedBillsDto dpts = ((PrintedBillsDto) o);
        return (dpts != null) ? dpts.getStreetName() : null;
    }
}
