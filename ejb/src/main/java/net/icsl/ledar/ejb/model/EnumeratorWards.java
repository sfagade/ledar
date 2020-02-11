package net.icsl.ledar.ejb.model;

import java.io.Serializable;
import java.util.Date;
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
 * @date Jan 29, 2016
 */
@Entity
@Table(name = "enumerator_wards")
@AttributeOverride(name = "id", column = @Column(name = "enumerator_ward_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "EnumeratorWards.findAll", query = "SELECT e FROM EnumeratorWards e"),
    @NamedQuery(name = "EnumeratorWards.findByEnumeratorWardId", query = "SELECT e FROM EnumeratorWards e WHERE e.id = :enumeratorWardId")})
public class EnumeratorWards extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 2000)
    @Column(name = "remarks")
    private String remarks;

    @JoinColumn(name = "lcda_ward_id", referencedColumnName = "lcda_ward_id")
    @ManyToOne(optional = false)
    private LcdaWards lcdaWardId;
    @JoinColumn(name = "logindetail_id", referencedColumnName = "logindetail_id")
    @ManyToOne(optional = false)
    private Logindetails logindetailId;

    public EnumeratorWards() {
    }

    public EnumeratorWards(Long enumeratorWardId) {
        this.id = enumeratorWardId;
    }

    public EnumeratorWards(Long enumWardId, LcdaWards lcdaWardId, Logindetails logindetailId, Date created_) {
        this.lcdaWardId = lcdaWardId;
        this.logindetailId = logindetailId;
        this.id = enumWardId;
        this.created = created_;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LcdaWards getLcdaWardId() {
        return lcdaWardId;
    }

    public void setLcdaWardId(LcdaWards lcdaWardId) {
        this.lcdaWardId = lcdaWardId;
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
        if (!(object instanceof EnumeratorWards)) {
            return false;
        }
        EnumeratorWards other = (EnumeratorWards) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.EnumeratorWards[ enumeratorWardId=" + id + " ]";
    }

}
