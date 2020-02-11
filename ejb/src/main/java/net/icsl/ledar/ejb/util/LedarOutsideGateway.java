package net.icsl.ledar.ejb.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author sfagade
 */
public class LedarOutsideGateway {

    public static String pushBillToAbc(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {

        //String string_url = "http://www.ebsmanager-online.com/LucWebServiceCall.asmx/wsPost_LucBill";
        String string_url = "https://www.services.ebs-rcm.com/LucWebServiceCall.asmx/wsPost_LucBill";
        HttpClient httpclient;
        PostMethod post = new PostMethod(string_url);

        JSONObject returned_msg;
        String response, out_data = null;

        Properties prop = new EmailSender(consultant_id).getProperties();

        initRequestHeaders(post);

        post.setParameter("dbName", prop.getProperty("abc.api.dbName"));
        post.setParameter("Usr", prop.getProperty("abc.api.usr"));
        post.setParameter("PayerType", api_params.get("PayerType"));
        post.setParameter("PayerID", api_params.get("PayerID"));
        post.setParameter("Bank_Payment_Code", api_params.get("Bank_Payment_Code"));
        post.setParameter("ipAddress", api_params.get("ipAddress"));
        post.setParameter("Amount", api_params.get("Amount"));
        post.setParameter("BillDate", api_params.get("BillDate"));
        post.setParameter("Addresstouse", api_params.get("Addresstouse"));
        post.setParameter("LgaName", api_params.get("LgaName"));
        post.setParameter("DistrictName", api_params.get("DistrictName"));
        post.setParameter("MerchantName", api_params.get("MachantName"));
        post.setParameter("NoticeNumber", api_params.get("NoticeNumber"));
        post.setParameter("ePaymentCode", api_params.get("ePaymentCode"));
        post.setParameter("fullpay_date", api_params.get("fullpay_date"));
        post.setParameter("start_latepaydate1", api_params.get("start_latepaydate1"));
        post.setParameter("end_latepaydate1", api_params.get("end_latepaydate1"));
        post.setParameter("amt_latepay1", api_params.get("amt_latepay1"));
        post.setParameter("start_latepaydate2", api_params.get("start_latepaydate2"));
        post.setParameter("end_latepaydate2", api_params.get("end_latepaydate2"));
        post.setParameter("amt_latepay2", api_params.get("amt_latepay2"));
        post.setParameter("start_latepaydate3", api_params.get("start_latepaydate3"));
        post.setParameter("end_latepaydate3", api_params.get("end_latepaydate3"));
        post.setParameter("amt_latepay3", api_params.get("amt_latepay3"));
        post.setParameter("AccessKey", prop.getProperty("abc.api.accessK"));

        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(post);

            if (result != 200) { //bad request
                out_data = "error request " + result;
                Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Error while trying to process Payer ID -> {0}", post.getResponseBodyAsString());
            } else {
                response = post.getResponseBodyAsString();
                String json_str = response.substring(response.indexOf("{"), response.indexOf("}") + 1);
                returned_msg = new JSONObject(json_str);
                if (returned_msg.has("EntryID")) {
                    out_data = returned_msg.getString("EntryID");
                } else if (returned_msg.has("Outputdata")) {
                    out_data = returned_msg.getString("Outputdata");
                } else {
                    out_data = json_str;
                }
            }
        } catch (java.net.UnknownHostException | JSONException ukn) {
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.INFO, "API call failed: {0}", new Object[]{api_params.get("Bank_Payment_Code")});
        }

        return out_data;
    }

    public static String makeAbcApiPayerIdCall(Map<String, String> api_params) throws IOException, JSONException, java.net.UnknownHostException {

        String string_url = (api_params.get("payer_type").equals("Individuals")) ? "https://www.services.ebs-rcm.com/WbsPayerIDCreation.asmx/wsCreatePayerID_Indiv"
                : "https://www.services.ebs-rcm.com/WbsPayerIDCreation.asmx/wsCreatePayerID_Coy";
        HttpClient httpclient;
        PostMethod post = new PostMethod(string_url);

        JSONObject returned_msg;
        String response, out_data;
        Properties prop = new EmailSender(null).getProperties();

        initRequestHeaders(post);

        if (api_params.get("payer_type").equals("Individuals")) {
            post.setParameter("SurName", api_params.get("surName"));
            post.setParameter("Title", api_params.get("title"));
            post.setParameter("Sex", api_params.get("gender"));
            post.setParameter("MaritalStatus", api_params.get("mstatus"));
            post.setParameter("ResidenceID", prop.getProperty("abc.api.residence"));
            post.setParameter("DateOfBirth", api_params.get("dateOfBirth"));

        } else {
            post.setParameter("LocationID", prop.getProperty("abc.api.location"));
            post.setParameter("ShortName", api_params.get("ShortName"));
        }

        post.setParameter("dbName", prop.getProperty("abc.api.dbName"));
        post.setParameter("Usr", prop.getProperty("abc.api.usr"));
        post.setParameter("FullName", api_params.get("fullName"));
        post.setParameter("email", api_params.get("email"));
        post.setParameter("gsm", api_params.get("phone"));
        post.setParameter("AddNo", api_params.get("addNo"));
        post.setParameter("Address", api_params.get("address"));
        post.setParameter("AccessKey", prop.getProperty("abc.payerIDKey"));

        httpclient = new HttpClient();

        int result = httpclient.executeMethod(post);

        if (result != 200) { //bad request
            out_data = "error request " + result;
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Error while trying to process Payer ID -> {0}", post.getResponseBodyAsString());
        } else {
            response = post.getResponseBodyAsString();
            String json_str = response.substring(response.indexOf("{"), response.indexOf("}") + 1);
            returned_msg = new JSONObject(json_str);
            out_data = returned_msg.getString("outData");
        }

        return out_data;

    }

    public static String makeAbcBillValidationCall(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException, java.net.UnknownHostException {

        Properties prop = new EmailSender(consultant_id).getProperties();

        HttpClient httpclient;
        PostMethod post = new PostMethod(prop.getProperty("abc.validate"));

        String out_data;

        initRequestHeaders(post);

        post.setParameter("dbName", prop.getProperty("abc.api.dbName"));
        post.setParameter("Usr", prop.getProperty("abc.api.usr"));
        post.setParameter("Bank_Payment_Code", api_params.get("bank_code"));
        post.setParameter("AccessKey", prop.getProperty("abc.api.accessK"));

        httpclient = new HttpClient();

        int result = httpclient.executeMethod(post);

        if (result != 200) { //bad request
            out_data = "error request " + result;
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Error while trying validate bill registration status -> {0}", post.getResponseBodyAsString());
        } else {
            out_data = post.getResponseBodyAsString();
        }

        return out_data;
    }

    @Deprecated
    public static JSONObject pushPropertyToInits(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {
        Properties prop = new EmailSender(consultant_id).getProperties();

        String host = prop.getProperty("inits.host") + "post-property?token=" + prop.getProperty("inits.token");
        PostMethod post = new PostMethod(host);

        //String result_msg;
        initRequestHeaders(post);

        HttpClient httpclient = new HttpClient();
        try {
            post.setParameter("pcrvalue_id", api_params.get("pcrvalue_id"));
            post.setParameter("local_government_id", api_params.get("local_government_id")); //NOTICE this will faill for irregular_streets
            post.setParameter("property_usage_id", api_params.get("property_usage_id"));
            post.setParameter("property_idn", api_params.get("property_idn"));
            post.setParameter("enumeration_date", api_params.get("enumeration_date"));
            post.setParameter("parcel_idn", api_params.get("parcel_idn")); //TODO I need to fix this
            post.setParameter("parcel_sheet_number", api_params.get("parcel_sheet_number")); //TODO I need to fix this
            post.setParameter("house_name", api_params.get("house_name")); //TODO I need to fix this
            post.setParameter("flat_no", api_params.get("flat_no"));
            post.setParameter("house_no", api_params.get("house_no"));
            post.setParameter("block_no", api_params.get("block_no"));
            post.setParameter("plot_no", api_params.get("plot_no"));
            post.setParameter("deed_registration", api_params.get("deed_registration"));
            post.setParameter("mapping_type_id", api_params.get("mapping_type_id"));
            post.setParameter("street_name", api_params.get("street_name"));
            post.setParameter("district_name", api_params.get("district_name"));
            post.setParameter("enumerator", api_params.get("enumerator"));
            post.setParameter("postal_code", api_params.get("postal_code"));
            post.setParameter("property_verified", api_params.get("property_verified"));
            post.setParameter("supervisor", api_params.get("supervisor"));
            post.setParameter("land_area", api_params.get("land_area"));
            post.setParameter("total_building_footprint", api_params.get("total_building_footprint"));
            post.setParameter("enumerator", api_params.get("enumerator"));
            post.setParameter("supervisor", api_params.get("supervisor"));
            post.setParameter("enumeration_date", api_params.get("enumeration_date"));
            post.setParameter("comments", api_params.get("comments"));

            httpclient.executeMethod(post);

            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.INFO, "INITS API returned -> {0}", post.getResponseBodyAsString());

            return new JSONObject(post.getResponseBodyAsString());
        } catch (java.lang.IllegalArgumentException le) {
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Required Property field missing", le.getMessage());
        }
        return null;
    }

    @Deprecated
    public static JSONObject pushPropertyOwnerToInits(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {
        Properties prop = new EmailSender(consultant_id).getProperties();

        HttpClient httpclient;
        PostMethod post = new PostMethod(prop.getProperty("inits.host") + "post-property-owner?token=" + prop.getProperty("inits.token"));

        String result_msg;

        initRequestHeaders(post);

        httpclient = new HttpClient();
        try {
            post.setParameter("property_id", api_params.get("property_id"));
            post.setParameter("full_name", api_params.get("full_name"));
            post.setParameter("name_title_id", api_params.get("name_title_id"));
            post.setParameter("owner_name", api_params.get("owner_name"));
            post.setParameter("last_name", api_params.get("last_name"));
            post.setParameter("first_name", api_params.get("first_name"));
            post.setParameter("middle_name", api_params.get("middle_name"));
            post.setParameter("owner_type_id", api_params.get("owner_type_id")); //fix this
            post.setParameter("mobile_phone", api_params.get("mobile_phone"));
            post.setParameter("other_phones", api_params.get("other_phones"));
            post.setParameter("contact_email", api_params.get("contact_email"));
            post.setParameter("country_id", api_params.get("country_id")); //hard code nigeria ID from INITS
            post.setParameter("district_name", api_params.get("district_name"));
            post.setParameter("created", api_params.get("created"));
            post.setParameter("local_government_id", api_params.get("local_government_id")); //NOTICE this will faill for irregular_streets
            post.setParameter("house_no", api_params.get("house_no"));
            post.setParameter("block_no", api_params.get("block_no"));
            post.setParameter("flat_no", api_params.get("flat_no"));
            post.setParameter("street_name", api_params.get("street_name"));
            post.setParameter("owner_classification_id", api_params.get("owner_classification_id"));
            post.setParameter("token", prop.getProperty("inits.token"));

            httpclient.executeMethod(post);

            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.INFO, "Data returned from INITS API -> {0}", post.getResponseBodyAsString());

            result_msg = post.getResponseBodyAsString();

            return new JSONObject(result_msg);
        } catch (java.lang.IllegalArgumentException ie) {
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Required Owner field missing", ie.getCause());
        }
        return null;
    }

    @Deprecated
    public static JSONObject pushPropertyBuildingToInits(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {
        Properties prop = new EmailSender(consultant_id).getProperties();

        HttpClient httpclient;
        PostMethod post = new PostMethod(prop.getProperty("inits.host") + "post-property-building?token=" + prop.getProperty("inits.token"));

        String result_msg;

        initRequestHeaders(post);
        httpclient = new HttpClient();

        try {
            post.setParameter("building_state_id", api_params.get("building_state_id")); //2 for completed, 1 for uncompleted
            post.setParameter("building_usage_id", api_params.get("building_usage_id"));
            post.setParameter("property_id", api_params.get("property_id"));
            post.setParameter("building_idn", api_params.get("building_idn"));
            post.setParameter("building_footprint", api_params.get("building_footprint"));
            post.setParameter("floors", api_params.get("floors"));
            post.setParameter("comments", api_params.get("comments"));
            post.setParameter("token", prop.getProperty("inits.token"));

            httpclient.executeMethod(post);

            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.INFO, "INITS API returned -> {0}", post.getResponseBodyAsString());

            result_msg = post.getResponseBodyAsString();

            return new JSONObject(result_msg);
        } catch (java.lang.IllegalArgumentException ie) {
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Required Building field missing", ie.getCause());
        }
        return null;
    }

    public static JSONObject pushPropertyPictureToInits(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {
        Properties prop = new EmailSender(consultant_id).getProperties();

        HttpClient httpclient;
        PostMethod post = new PostMethod(prop.getProperty("inits.host") + "post-property-image-details?token=" + prop.getProperty("inits.token"));
        MultipartPostMethod method = new MultipartPostMethod(prop.getProperty("inits.host") + "post-property-image-details?token=" + prop.getProperty("inits.token"));
        String result_msg;

        initRequestHeaders(post);

        httpclient = new HttpClient();
        try {

            File file = new File(api_params.get("picture"));
            method.addPart(new FilePart("imageFile", file, "image/jpg", "ISO-8859-1"));
            method.addParameter("property_id", api_params.get("property_id"));

            httpclient.executeMethod(method);

            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.INFO, "INITS API returned -> {0}", method.getResponseBodyAsString());

            result_msg = method.getResponseBodyAsString();

            return new JSONObject(result_msg);
        } catch (java.io.FileNotFoundException fe) {
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "File Not Found: ", fe);
        }

        return null;
    }

    public static String pullDailyPaymentAbcInformation(String date) throws IOException, JSONException {
        Properties prop = new EmailSender(null).getProperties();

        HttpClient httpclient;
        PostMethod post = new PostMethod(prop.getProperty("abc.payment"));

        String result_msg;

        initRequestHeaders(post);

        httpclient = new HttpClient();
        try {
            post.setParameter("PaymentDate", date);
            post.setParameter("dbName", prop.getProperty("abc.api.dbName"));
            post.setParameter("Usr", prop.getProperty("abc.api.usr"));
            post.setParameter("AccessKey", prop.getProperty("abc.api.accessK"));

            httpclient.executeMethod(post);

            result_msg = post.getResponseBodyAsString();

            return result_msg;
        } catch (java.lang.IllegalArgumentException ie) {
            Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.SEVERE, "Required Owner field missing", ie.getCause());
        }
        return null;
    }

    public static JSONObject makeEptsApiPostCall(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {
        Properties prop = new EmailSender(consultant_id).getProperties();
        HttpClient httpclient;
        String method_call = api_params.get("api_method"), key_value;
        PostMethod post = new PostMethod(prop.getProperty("inits.host") + api_params.get("api_method") + "?token=" + prop.getProperty("inits.token"));

        initRequestHeaders(post);
        httpclient = new HttpClient();

        post.setParameter("token", prop.getProperty("inits.token"));

        api_params.remove("api_method");
        api_params.remove("task");

        for (Map.Entry<String, String> entry : api_params.entrySet()) {
            key_value = entry.getValue();
            if (key_value != null && !key_value.trim().isEmpty() && !key_value.trim().equalsIgnoreCase("null") && !key_value.equalsIgnoreCase("null ")) {
                //Logger.getLogger(LedarOutsideGateway.class.getName()).log(Level.INFO, "Adding field: {0} value: {1} for API: {2}", new Object[]{entry.getKey(), key_value, method_call});
                post.setParameter(entry.getKey(), key_value);
            }
        }

        httpclient.executeMethod(post);

        return new JSONObject(post.getResponseBodyAsString());

    }

    public static JSONObject makeEptsApiGetCall(Map<String, String> api_params, Long consultant_id) throws IOException, JSONException {

        Properties prop = new EmailSender(null).getProperties();
        String url_pattarn = prop.getProperty("inits.host") + api_params.get("api_method");

        if (api_params.get("task").equals("get property")) {
            url_pattarn += "?notice_no=" + api_params.get("notice_no") + "&token=" + prop.getProperty("inits.token");
        }

        GetMethod getMet = new GetMethod(url_pattarn);
        HttpClient httpclient = new HttpClient();

        httpclient.executeMethod(getMet);

        return new JSONObject(getMet.getResponseBodyAsString());
    }

    public static void initRequestHeaders(PostMethod post) {

        //http headers
        post.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        post.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.2.12)");
        post.setRequestHeader("Accept-Encoding", "gzip,deflate");
        post.setRequestHeader("Accept-Language", "en-us,en;q=0.5");
        post.setRequestHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        post.setRequestHeader("Pragma", "no-cache");

    }
}
