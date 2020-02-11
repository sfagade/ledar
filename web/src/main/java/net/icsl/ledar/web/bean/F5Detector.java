package net.icsl.ledar.web.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@SessionScoped
public class F5Detector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public F5Detector() {
		// TODO Auto-generated constructor stub
	}
	
	private String previousPage = null;
	 
	  public void checkF5() {
	    String msg = "";
	    UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
	    String id = viewRoot.getViewId();
	    if (previousPage != null && (previousPage.equals(id))) {
	       // It's a reload event
	    }
	    previousPage = id;
	  }

	public String getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

}
