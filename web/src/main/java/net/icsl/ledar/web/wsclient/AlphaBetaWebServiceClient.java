/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.web.wsclient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.icsl.ledar.ejb.util.LedarOutsideGateway;
import org.primefaces.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author sfagade
 * @date Jun 19, 2017
 */
public class AlphaBetaWebServiceClient {

    private static final DateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

    public static Document fetchAbcDailyPayment(Date pay_date) {

        if (pay_date != null) {

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document payment_info = db.parse(new InputSource(new StringReader(LedarOutsideGateway.pullDailyPaymentAbcInformation(shortDateFormat.format(pay_date)))));

                return payment_info;
            } catch (IOException | JSONException | SAXException | ParserConfigurationException ex) {
                Logger.getLogger(AlphaBetaWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public static void printDocument(Node doc, OutputStream out) {
        TransformerFactory tf = TransformerFactory.newInstance();

        try {
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));

        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(AlphaBetaWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException | TransformerException ex) {
            Logger.getLogger(AlphaBetaWebServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
