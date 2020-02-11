
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
 

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "user_roles")
@AttributeOverride(name = "id", column = @Column(name = "user_role_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "UserRoles.findAll", query = "SELECT u FROM UserRoles u"),
    @NamedQuery(name = "UserRoles.findUserId", query = "SELECT u FROM UserRoles u WHERE u.logindetailId.id = :userId"),
    @NamedQuery(name = "UserRoles.findByUserRoleId", query = "SELECT u FROM UserRoles u WHERE u.id = :userRoleId")})
public class UserRoles extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @JoinColumn(name = "authentication_role_id", referencedColumnName = "authentication_role_id")
    @ManyToOne(optional = false)
    private AuthenticationRoles authenticationRoleId;
    @JoinColumn(name = "logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails logindetailId;

    public UserRoles() {
    }

    public UserRoles(Long userRoleId,String description) {
        this.id = userRoleId;
        this.description = description;
    }
    
    public UserRoles(Logindetails logindetailId, AuthenticationRoles authenticationRoleId) {
        super();
        this.logindetailId = logindetailId;
        this.authenticationRoleId = authenticationRoleId;
    }
    
    /**public UserRoles(Long userRoleId,) {
    } */

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AuthenticationRoles getAuthenticationRoleId() {
        return authenticationRoleId;
    }

    public void setAuthenticationRoleId(AuthenticationRoles authenticationRoleId) {
        this.authenticationRoleId = authenticationRoleId;
    }

    public Logindetails getLogindetailId() {
        return logindetailId;
    }

    public void setLogindetailId(Logindetails logindetailId) {
        this.logindetailId = logindetailId;
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
        if (!(object instanceof UserRoles)) {
            return false;
        }
        UserRoles other = (UserRoles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.UserRoles[ userRoleId=" + id + " ]";
    }

}
