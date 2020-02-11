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
import net.icsl.ledar.ejb.model.AuthenticationRoles;


@ManagedBean(name = "userRolesConverterBean")
@FacesConverter(value = "userRolesConverter")
public class UserRolesConverter implements Converter {

    @PersistenceContext
    // I include this because you will need to 
    // lookup  your entities based on submitted values
    private transient EntityManager entManager;

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {

    	if (!value.contains("Select")) {
        CriteriaBuilder cb = entManager.getCriteriaBuilder();

        CriteriaQuery<AuthenticationRoles> criteria = cb.createQuery(AuthenticationRoles.class);
        Root<AuthenticationRoles> deptObj = criteria.from(AuthenticationRoles.class);

        criteria = criteria.multiselect(deptObj.get("id"), deptObj.get("roleName"), deptObj.get("roleOrder"), deptObj.get("created")).where(cb.equal(deptObj.get("id"), new Long(value)));

        return entManager.createQuery(criteria).getSingleResult();
    	} else {
    		return null;
    	}
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        //This will return view-friendly output for the dropdown menu
        AuthenticationRoles dpts = ((AuthenticationRoles) o);
        return (dpts != null) ? dpts.getId().toString() : null;
    }
}
