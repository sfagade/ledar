package net.icsl.ledar.web.converters;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.icsl.ledar.ejb.model.WardStreets;

@ManagedBean(name = "wardStreetConverterBean")
@FacesConverter(value = "wardStreetConverter")
public class WardStreetConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<WardStreets> criteria = cb.createQuery(WardStreets.class);
        Root<WardStreets> wardStreet = criteria.from(WardStreets.class);
        if ((value != null) && (!value.isEmpty()) && (!value.contains("Select"))) {
            try {
                Long str_id = new Long(value);
                criteria = criteria.multiselect(wardStreet.get("id"), wardStreet.get("streetName"), wardStreet.get("offStreetOne"), wardStreet.get("nearestBusStop"), wardStreet.get("estateName"), wardStreet.get("created")).
                        where(cb.equal(wardStreet.get("id"), str_id));
                return entManager.createQuery(criteria).getSingleResult();
            } catch (NumberFormatException nex) {
                Logger.getLogger(WardStreetConverter.class.getName()).log(Level.SEVERE, "Invalid street value: ", nex);
                return null;
            } catch (Exception ex) {
                Logger.getLogger(WardStreetConverter.class.getName()).log(Level.SEVERE, "Unexpected street exception: ", ex);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object obj) {
        //This will return view-friendly output for the dropdown menu
        WardStreets dpts = null;
        if (obj instanceof String || obj instanceof ArrayList) {
        } else {
            dpts = ((WardStreets) obj);
        }
        return (dpts != null) ? dpts.getId().toString() : null;
    }
}
