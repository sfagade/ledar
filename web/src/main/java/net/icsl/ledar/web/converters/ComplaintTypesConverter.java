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
import net.icsl.ledar.ejb.model.ComplaintTypes;


@ManagedBean(name = "compliTypeConverterBean")
@FacesConverter(value = "compliTypeConverter")
public class ComplaintTypesConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

    	if (!value.contains("Select")) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<ComplaintTypes> criteria = cb.createQuery(ComplaintTypes.class);
        Root<ComplaintTypes> deptObj = criteria.from(ComplaintTypes.class);

        criteria = criteria.multiselect(deptObj.get("id"), deptObj.get("typeName"), deptObj.get("created")).where(cb.equal(deptObj.get("id"), new Long(value)));

        return entManager.createQuery(criteria).getSingleResult();
    	} else {
    		return null;
    	}
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        ComplaintTypes dpts = ((ComplaintTypes) o);
        return (dpts != null) ? dpts.getId().toString() : null;
    }
}
