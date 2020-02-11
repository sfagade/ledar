/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.data;

import java.util.List;
import javax.inject.Inject;
import net.icsl.ledar.ejb.model.Notifications;
import net.icsl.ledar.ejb.service.NotificationDataService;
import net.icsl.ledar.ejb.service.ReferenceDataService;
import net.icsl.ledar.ejb.util.Resources;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author sfagade
 * @date Jul 22, 2017
 */
@RunWith(Arquillian.class)
public class NotificationServiceTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(NotificationDataService.class)
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml", "test-ds.xml");
    }

    @Inject
    NotificationDataService noteService;

    @Test
    public void findUserNotificationsTest() {
        List<Notifications> notifi = noteService.findUserNotifications(1L, null, 10);
        System.out.println("Total records {0}" + notifi.size());
        Assert.assertTrue("Completed Successfully", true);
    }

}
