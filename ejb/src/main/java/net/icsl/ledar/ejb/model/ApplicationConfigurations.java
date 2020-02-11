
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "application_configurations")
@AttributeOverride(name = "id", column = @Column(name = "app_config_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
@NamedQueries({
    @NamedQuery(name = "ApplicationConfigurations.findAll", query = "SELECT a FROM ApplicationConfigurations a"),
    @NamedQuery(name = "ApplicationConfigurations.findByAddressId", query = "SELECT a FROM ApplicationConfigurations a WHERE a.id = :addressId")})
public class ApplicationConfigurations extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "app_version_number")
    private String appVersionNumber;
    @Size(max = 25)
    @Column(name = "mobile_version_number")
    private String mobileVersionNumber;
    @Size(max = 25)
    @Column(name = "web_version_number")
    private String webVersionNumber;
    @Column(name = "remarks")
    private String remarks;
   

    public ApplicationConfigurations() {
    }

    public ApplicationConfigurations(Long addressId) {
        this.id = addressId;
    }

    public ApplicationConfigurations(Long config_id, String mobile_version, String app_version,Date created) {
        this.id = config_id;
        this.mobileVersionNumber = mobile_version;
        this.created = created;
        this.appVersionNumber = app_version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApplicationConfigurations)) {
            return false;
        }
        ApplicationConfigurations other = (ApplicationConfigurations) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ApplicationConfigurations[ addressId=" + appVersionNumber + " ]";
    }

    /**
     * @return the appVersionNumber
     */
    public String getAppVersionNumber() {
        return appVersionNumber;
    }

    /**
     * @param appVersionNumber the appVersionNumber to set
     */
    public void setAppVersionNumber(String appVersionNumber) {
        this.appVersionNumber = appVersionNumber;
    }

    /**
     * @return the mobileVersionNumber
     */
    public String getMobileVersionNumber() {
        return mobileVersionNumber;
    }

    /**
     * @param mobileVersionNumber the mobileVersionNumber to set
     */
    public void setMobileVersionNumber(String mobileVersionNumber) {
        this.mobileVersionNumber = mobileVersionNumber;
    }

    /**
     * @return the webVersionNumber
     */
    public String getWebVersionNumber() {
        return webVersionNumber;
    }

    /**
     * @param webVersionNumber the webVersionNumber to set
     */
    public void setWebVersionNumber(String webVersionNumber) {
        this.webVersionNumber = webVersionNumber;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
