/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.util;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author sfagade
 */
@Singleton
//@Stateless
public class EmailSender implements Serializable {

    private static final long serialVersionUID = 1L;

    Properties prop;
    InputStream settingsStream;
    private Properties properties;
    Session session;
    private Long consultantId;

    public EmailSender() {
        initPropertiesAttribute();
    }

    public EmailSender(Long consultant_id) {
        this.consultantId = consultant_id;
        initPropertiesAttribute();
    }

    public void initPropertiesAttribute() {
        prop = new Properties();
        try {
            settingsStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/settings.properties");

            prop.load(settingsStream);
        } catch (IOException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, "Failed to load properties file from Classloader", ex);
        }

        try {
            if (prop != null) {
                //prop.load(settingsStream);
                properties = new Properties();

                properties.put("mail.smtp.host", prop.getProperty("smtp.host"));
                properties.put("mail.smtp.port", prop.getProperty("smtp.port"));
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.user", prop.getProperty("smtp.username"));
                properties.put("mail.password", prop.getProperty("smtp.password"));
                properties.put("imageURl", prop.getProperty("imageURl"));
                properties.put("abc.api.dbName", prop.getProperty("abc.api.dbName"));
                properties.put("abc.api.usr", prop.getProperty("abc.api.usr"));
                properties.put("abc.api.residence", prop.getProperty("abc.api.residence"));
                properties.put("abc.api.location", prop.getProperty("abc.api.location"));
                properties.put("abc.api.accessK", prop.getProperty("abc.api.accessK"));
                properties.put("bills.folder", prop.getProperty("abc.bill.files"));
                properties.put("abc.payerIDKey", prop.getProperty("abc.api.payerIDKey"));
                properties.put("abc.validate", prop.getProperty("abc.bill.validate"));
                properties.put("inits.host", prop.getProperty("inits.api.host"));
                properties.put("app.currentYear", prop.getProperty("ldr.app.currentYear"));
                if (consultantId != null) {
                    properties.put("inits.token", prop.getProperty("inits.api.token.desofit"));
                } else {
                    properties.put("inits.token", prop.getProperty("inits.api.token"));
                }
                properties.put("device.push", prop.getProperty("ldr.device.push"));
                properties.put("device.key", prop.getProperty("ldr.device.key"));
                properties.put("ip.folder", prop.getProperty("ldr.ip.files"));
                properties.put("abc.payment", prop.getProperty("abc.bill.payment"));
            } else {
                throw new FileNotFoundException("property file 'settings.properties' not found in the classpath");
            }
        } catch (IOException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void sendEmailWithAttachment(String[] to_addresses, String[] bc_addresses, String[] cc_addresses, String subject, String message, List<File> attachedFiles) throws AddressException,
            MessagingException {

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("smtp.username"), prop.getProperty("smtp.password"));
            }
        };

        session = Session.getInstance(getProperties(), auth);
        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(prop.getProperty("smtp.username")));

        InternetAddress[] bcAddresses, ccAddresses;
        InternetAddress[] toAddresses = new InternetAddress[to_addresses.length];

        if (bc_addresses != null) {
            bcAddresses = new InternetAddress[bc_addresses.length];
            for (int x = 0; x < bc_addresses.length; x++) {
                bcAddresses[x] = new InternetAddress(bc_addresses[x]);
            }
            msg.setRecipients(Message.RecipientType.BCC, bcAddresses);
        }

        if (cc_addresses != null) {
            ccAddresses = new InternetAddress[cc_addresses.length];
            for (int x = 0; x < cc_addresses.length; x++) {
                ccAddresses[x] = new InternetAddress(cc_addresses[x]);
            }
            msg.setRecipients(Message.RecipientType.CC, ccAddresses);
        }

        for (int x = 0; x < to_addresses.length; x++) {
            toAddresses[x] = new InternetAddress(to_addresses[x]);
        }

        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds attachments
        if (attachedFiles != null && attachedFiles.size() > 0) {
            for (File aFile : attachedFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();

                try {
                    attachPart.attachFile(aFile);
                } catch (IOException ex) {
                    Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, "Attching file failed", ex);
                }

                multipart.addBodyPart(attachPart);
            }
        }

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);

        // sends the e-mail
        Transport.send(msg);

    }

    @Asynchronous
    public void sendPlainEmailMessage(String message, String subject, String[] to_addresses) throws AddressException, MessagingException {

        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("smtp.username"), prop.getProperty("smtp.password"));
            }
        };

        session = Session.getInstance(getProperties(), auth);
        // creates a new e-mail message
        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(prop.getProperty("smtp.username")));
        InternetAddress[] toAddresses = new InternetAddress[to_addresses.length];

        for (int x = 0; x < to_addresses.length; x++) {
            toAddresses[x] = new InternetAddress(to_addresses[x]);
        }
        //toAddresses = {new InternetAddress(toAddress)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html;  charset=utf-8");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);

        // sends the e-mail
        Transport.send(msg);

    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
