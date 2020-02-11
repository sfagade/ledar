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
import net.icsl.ledar.ejb.model.OrganizationTypes;


@ManagedBean(name = "orgTypeConverterBean")
@FacesConverter(value = "orgTypeConverter")
public class OrganizationTypesConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<OrganizationTypes> criteria = cb.createQuery(OrganizationTypes.class);
        Root<OrganizationTypes> orgType = criteria.from(OrganizationTypes.class);
        if ((value != null) && (!value.isEmpty()) && (!value.contains("Select"))) {
        criteria = criteria.multiselect(orgType.get("id"), orgType.get("typeName"), orgType.get("created")).where(cb.equal(orgType.get("id"), new Long(value)));

        return entManager.createQuery(criteria).getSingleResult();
        } else {
        	return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        OrganizationTypes otype = ((OrganizationTypes) o);
        return (otype != null) ? otype.getId().toString() : null;
    }
}
