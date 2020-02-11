package net.icsl.ledar.web.converters;

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
import net.icsl.ledar.ejb.model.CommercialTypes;

@ManagedBean(name = "commerceTypeConverterBean")
@FacesConverter(value = "commerceTypeConverter")
public class CommerceTypeConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<CommercialTypes> criteria = cb.createQuery(CommercialTypes.class);
        Root<CommercialTypes> commrObj = criteria.from(CommercialTypes.class);
        if (!value.contains("Select")) {
            criteria = criteria.multiselect(commrObj.get("id"), commrObj.get("commercialTypeName"), commrObj.get("created")).where(cb.equal(commrObj.get("id"), new Long(value)));
            return entManager.createQuery(criteria).getSingleResult();
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        CommercialTypes commrObj = ((CommercialTypes) o);
        return (commrObj != null) ? commrObj.getId().toString() : null;
    }
}
