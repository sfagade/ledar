
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author sfagade
 * @date Jan 25, 2016
 */
@Entity
@Table(name = "authentication_roles")
@AttributeOverride(name = "id", column = @Column(name = "authentication_role_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
@NamedQueries({
    @NamedQuery(name = "AuthenticationRoles.findAll", query = "SELECT a FROM AuthenticationRoles a"),
    @NamedQuery(name = "AuthenticationRoles.findByAuthenticationRoleId", query = "SELECT a FROM AuthenticationRoles a WHERE a.id = :authenticationRoleId"),
    @NamedQuery(name = "AuthenticationRoles.findByRoleName", query = "SELECT a FROM AuthenticationRoles a WHERE a.roleName = :roleName")})
public class AuthenticationRoles extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 55)
    @Column(name = "role_name")
    private String roleName;
    @Size(max = 225)
    @Column(name = "role_description")
    private String roleDescription;
    @Column(name = "role_type")
    private Integer roleType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_order")
    private int roleOrder;
    @Size(max = 35)
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "is_contractor_restricted")
    private Boolean isContractorRestricted;
    @Size(max = 15)
    @Column(name = "url_mapping")
    private String urlMapping;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authenticationRoleId")
    private List<UserRoles> userRolesList;

    public AuthenticationRoles() {
    }

    public AuthenticationRoles(Long authenticationRoleId) {
        this.id = authenticationRoleId;
    }

    public AuthenticationRoles(Long authenticationRoleId, String roleName, int roleOrder, Date created) {
        this.id = authenticationRoleId;
        this.roleName = roleName;
        this.roleOrder = roleOrder;
        this.created = created;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public int getRoleOrder() {
        return roleOrder;
    }

    public void setRoleOrder(int roleOrder) {
        this.roleOrder = roleOrder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUrlMapping() {
        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public List<UserRoles> getUserRolesList() {
        return userRolesList;
    }

    public void setUserRolesList(List<UserRoles> userRolesList) {
        this.userRolesList = userRolesList;
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
        if (!(object instanceof AuthenticationRoles)) {
            return false;
        }
        AuthenticationRoles other = (AuthenticationRoles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.AuthenticationRoles[ authenticationRoleId=" + id + " ]";
    }
    
    public static Comparator<AuthenticationRoles> authenticationRoleNameComparator = new Comparator<AuthenticationRoles>() {

        @Override
        public int compare(AuthenticationRoles authRole1, AuthenticationRoles authRole2) {

            String auth_name1 = authRole1.getRoleName().toUpperCase();
            String auth_name2 = authRole2.getRoleName().toUpperCase();

//ascending order
            return auth_name1.compareTo(auth_name2);

//descending order
//return fruitName2.compareTo(fruitName1);
        }

    };

    /**
     * @return the isContractorRestricted
     */
    public Boolean getIsContractorRestricted() {
        return isContractorRestricted;
    }

    /**
     * @param isContractorRestricted the isContractorRestricted to set
     */
    public void setIsContractorRestricted(Boolean isContractorRestricted) {
        this.isContractorRestricted = isContractorRestricted;
    }

}
