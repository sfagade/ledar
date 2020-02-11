/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.listener;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import net.icsl.ledar.ejb.model.IcslLedarModelBase;

/**
 *
 * @author sfagade
 */
public class RecordUpdateListener {


    @PreUpdate
    //@PrePersist
    public void setUpdateDates(IcslLedarModelBase abso) {
        abso.setModified(new Date());
    }

    @PrePersist
    public void setCreateDates(IcslLedarModelBase abs) {
        abs.setModified(new Date());
        abs.setCreated(new Date());
    }
}
