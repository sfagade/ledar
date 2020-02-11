package net.icsl.ledar.ejb.model;

import net.icsl.ledar.ejb.listener.RecordUpdateListener;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author sfagade
 */
@MappedSuperclass
@EntityListeners(RecordUpdateListener.class)
public abstract class IcslLedarModelBase implements Serializable {

    @Id
    @TableGenerator(name = "OnlinePkGen",
            table = "ID_GEN",
            pkColumnName = "GEN_NAME",
            pkColumnValue = "Online_Gen",
            valueColumnName = "GEN_VAL",
            initialValue = 0,
            allocationSize = 5
    )
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    protected Long id;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modified;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
