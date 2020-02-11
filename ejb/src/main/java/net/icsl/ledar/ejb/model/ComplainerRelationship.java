/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
 *
 * @author sfagade
 * @date Mar 31, 2017
 */
@Entity
@Table(name = "ref_complainer_relationship")
@AttributeOverride(name = "id", column = @Column(name = "relationship_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"))
 
@NamedQueries({
    @NamedQuery(name = "ComplainerRelationship.findAll", query = "SELECT c FROM ComplainerRelationship c")
    , @NamedQuery(name = "ComplainerRelationship.findByRelationshipId", query = "SELECT c FROM ComplainerRelationship c WHERE c.id = :relationshipId")
    , @NamedQuery(name = "ComplainerRelationship.findByRelationshipName", query = "SELECT c FROM ComplainerRelationship c WHERE c.relationshipName = :relationshipName")})
public class ComplainerRelationship extends IcslLedarModelBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 75)
    @Column(name = "relationship_name")
    private String relationshipName;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    

    public ComplainerRelationship() {
    }

    public ComplainerRelationship(Long relationshipId) {
        this.id = relationshipId;
    }

    public ComplainerRelationship(Long relationshipId, String relationshipName, Date created_) {
        this.id = relationshipId;
        this.relationshipName = relationshipName;
        this.created = created_;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof ComplainerRelationship)) {
            return false;
        }
        ComplainerRelationship other = (ComplainerRelationship) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.icsl.ledar.ejb.model.ComplainerRelationship[ relationshipId=" + id + " ]";
    }

}
