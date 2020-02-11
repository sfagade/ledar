
package net.icsl.ledar.ejb.model;

import java.io.Serializable;
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
@Table(name = "ref_settlement_types")
@AttributeOverride(name = "id", column = @Column(name = "settlement_type_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "SettlementTypes.findAll", query = "SELECT s FROM SettlementTypes s"),
    @NamedQuery(name = "SettlementTypes.findBySettlementTypeId", query = "SELECT s FROM SettlementTypes s WHERE s.id = :settlementTypeId"),
    @NamedQuery(name = "SettlementTypes.findBySettlementTypeName", query = "SELECT s FROM SettlementTypes s WHERE s.settlementTypeName = :settlementTypeName")})
public class SettlementTypes extends IcslLedarModelBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "settlement_type_name")
    private String settlementTypeName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "settlementTypeId")
    private List<WardStreets> wardStreetsList;

    public SettlementTypes() {
    }

    public SettlementTypes(Long settlementTypeId) {
        this.id = settlementTypeId;
    }

    public SettlementTypes(Long settlementTypeId, String settlementTypeName) {
        this.id = settlementTypeId;
        this.settlementTypeName = settlementTypeName;
    }

    public String getSettlementTypeName() {
        return settlementTypeName;
    }

    public void setSettlementTypeName(String settlementTypeName) {
        this.settlementTypeName = settlementTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     
    public List<WardStreets> getWardStreetsList() {
        return wardStreetsList;
    }

    public void setWardStreetsList(List<WardStreets> wardStreetsList) {
        this.wardStreetsList = wardStreetsList;
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
        if (!(object instanceof SettlementTypes)) {
            return false;
        }
        SettlementTypes other = (SettlementTypes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.SettlementTypes[ settlementTypeId=" + id + " ]";
    }

}
