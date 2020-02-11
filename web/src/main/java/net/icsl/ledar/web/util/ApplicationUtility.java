package net.icsl.ledar.web.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.icsl.ledar.ejb.model.PrintedBills;
import net.icsl.ledar.ejb.util.EmailSender;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.primefaces.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import org.primefaces.model.UploadedFile;

/**
 * @author sfagade
 * @date Jan 29, 2016
 */
public class ApplicationUtility {

    public static String returnBillOwnerAddress(PrintedBills bill) {
        String ownerAddress = "";
        if (bill.getOwnerHouseNo() != null && !bill.getOwnerHouseNo().isEmpty()) {
            ownerAddress = bill.getOwnerHouseNo();
        } else if (bill.getOwnerPlotNo() != null && !bill.getOwnerPlotNo().isEmpty()) {
            ownerAddress = bill.getOwnerPlotNo();
        } else if (bill.getOwnerBlockNo() != null && !bill.getOwnerBlockNo().isEmpty()) {
            ownerAddress = bill.getOwnerBlockNo();
        } else if(bill.getFlatNo() != null && !bill.getFlatNo().isEmpty()){
            ownerAddress = bill.getFlatNo();
        }
        return ownerAddress;
    }

    public static String returnBillAddress(String address_, PrintedBills bill) {
        if (bill.getHouseNo() != null && !bill.getHouseNo().isEmpty()) {
            address_ += bill.getHouseNo();
        } else if (bill.getPlotNo() != null && (!bill.getPlotNo().isEmpty())) {
            address_ += bill.getPlotNo();
        } else if (bill.getBlockNo() != null && (!bill.getBlockNo().isEmpty())) {
            address_ = bill.getBlockNo();
        } else if(bill.getFlatNo() != null && !bill.getFlatNo().isEmpty()) {
            address_ += bill.getFlatNo();
        }
        return address_;
    }

    public static String formatPhoneNumber(String phone_number, String country) {

        String phoneNumber = null;

        if ((phone_number != null) && (!phone_number.isEmpty())) {

            if (!phone_number.substring(0, 1).equals("2")) {
                phoneNumber = "234" + phone_number;
            } else { //phone number is not already formatted, I need to format it
                phoneNumber = phone_number;
            }
        }

        return phoneNumber;
    }

    public static String currencyFormatter(String value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(value);
        System.out.println(moneyString);
        return moneyString;
    }

    public static String renameImageFile(String original_name, String append) {

        String new_name = null, file_ext;

        if ((original_name != null) && (!original_name.isEmpty())) {
            new_name = original_name.substring(0, original_name.lastIndexOf("."));
            file_ext = original_name.substring(original_name.lastIndexOf("."));
            if (append != null) {
                new_name += "_" + append + file_ext;
            } else {
                new_name += "_" + new java.util.Date().getTime() + file_ext;
            }
        }

        return new_name;
    }

    public static String pushMessageToDevices(JSONObject msg_) {

        Properties propy = new EmailSender(null).getProperties();
        String url_ = propy.getProperty("device.push");
        String return_msg = null;

        PostMethod post = new PostMethod(url_);
        post.setRequestHeader("Content-type", "application/json");
        //post.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.2.12)");
        //post.setRequestHeader("Accept-Encoding", "gzip,deflate");
        //post.setRequestHeader("Accept-Language", "en-us,en;q=0.5");
        post.setRequestHeader("Accept-Charset", "utf-8e");
        //post.setRequestHeader("Pragma", "no-cache");
        post.setRequestHeader("Authorization", propy.getProperty("device.key"));
        Logger.getLogger(ApplicationUtility.class.getName()).log(Level.INFO, "About to POST to device: {0} -- {1}", new Object[]{propy.getProperty("device.key"), msg_.toString()});
        post.setParameter("Body", msg_ + "");
        //post.setRequestBody(msg_+"");
        post.setParameter("Authorization", propy.getProperty("device.key"));

        HttpClient httpclient = new HttpClient();

        int result;
        try {
            result = httpclient.executeMethod(post);
            if (result != 200) { //bad request
                return_msg = "Failed";
            } else {
                return_msg = "Success";
            }
            Logger.getLogger(ApplicationUtility.class.getName()).log(Level.SEVERE, "Message returned is: {0}", post.getResponseBodyAsString());
        } catch (IOException ex) {
            Logger.getLogger(ApplicationUtility.class.getName()).log(Level.SEVERE, null, ex);
        }

        return return_msg;
    }

    public static CityResponse fetchLocationFromIp(String ip_address) {
        try {

            Properties prop = new EmailSender(null).getProperties();
            String folder_path = prop.getProperty("ip.folder");
// A File object pointing to your GeoIP2 or GeoLite2 database
            File database = new File(folder_path + "/location_db/GeoLite2-City.mmdb");

// This creates the DatabaseReader object, which should be reused across
// lookups.
            DatabaseReader reader = new DatabaseReader.Builder(database).build();

            InetAddress ipAddress = InetAddress.getByName(ip_address);

// Replace "city" with the appropriate method for your database, e.g.,
// "country".
            CityResponse response = reader.city(ipAddress);

            Country country = response.getCountry();
            System.out.println(country.getIsoCode());            // 'US'
            System.out.println(country.getName());               // 'United States'
            System.out.println(country.getNames().get("zh-CN")); // '美国'

            Subdivision subdivision = response.getMostSpecificSubdivision();
            System.out.println(subdivision.getName());    // 'Minnesota'
            System.out.println(subdivision.getIsoCode()); // 'MN'

            City city = response.getCity();
            System.out.println(city.getName()); // 'Minneapolis'

            Postal postal = response.getPostal();
            System.out.println(postal.getCode()); // '55455'

            Location location = response.getLocation();
            System.out.println(location.getLatitude());  // 44.9733
            System.out.println(location.getLongitude()); // -93.2323       // -93.2323

            return response;
        } catch (AddressNotFoundException ae) {
            Logger.getLogger(ApplicationUtility.class.getName()).log(Level.SEVERE, null, ae);
        } catch (IOException | GeoIp2Exception ex) {
            Logger.getLogger(ApplicationUtility.class.getName()).log(Level.SEVERE, "File not found: "+ip_address, ex.getMessage());
        }

        return null;
    }

    public static File uploadUserFile(String file_path, UploadedFile uploaded_file) {

        File upload_folder;

        File download_folder = new File(file_path);
        if (!download_folder.exists()) {
            download_folder.mkdirs(); //create folder
        }
        upload_folder = new File(file_path + "/" + ApplicationUtility.renameImageFile(uploaded_file.getFileName(), "UPLOADED"));

        BufferedOutputStream stream;
        
        try {
            stream = new BufferedOutputStream(new FileOutputStream(upload_folder));
            stream.write(uploaded_file.getContents());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ApplicationUtility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationUtility.class.getName()).log(Level.SEVERE, null, ex);
        }

        return upload_folder;
    }
}
