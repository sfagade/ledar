package net.icsl.ledar.web.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import net.icsl.ledar.ejb.dto.PrintedBillsDto;
import net.icsl.ledar.ejb.model.LocalCouncilDevArea;
import org.apache.shiro.SecurityUtils;
import org.primefaces.model.UploadedFile;

/**
 * @author sfagade 2015
 */
public class FacesSupportUtil {

    public static final DateFormat SHORTFORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public static boolean setDateRangeCustomError(boolean b, boolean select_date_range, boolean b2, boolean b3) {
        if (b && select_date_range) {
            if ((b2) || (b3)) { //both fields are needed to continue
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "First and second date fields are needed to complete this search", ""));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                return true;
            }
        }
        return false;
    }

    public static  Map<String, Date> fetchAndSetStartAndEndDate(String selectedRange) {

        Calendar calendar = Calendar.getInstance();
        Map<String, Date> dateMap = new HashMap<>();
        switch (selectedRange) {
            case "TODAY":
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dateMap.put("start", calendar.getTime());
                dateMap.put("end", new Date());
                break;
            case "YESTERDAY":
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dateMap.put("end", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dateMap.put("start", calendar.getTime());

                break;
            case "PAST WEEK":
                calendar.add(Calendar.DAY_OF_MONTH, -8);
                dateMap.put("start", calendar.getTime());
                dateMap.put("end", new Date());
                break;
            case "PAST MONTH":
                calendar.add(Calendar.MONTH, -1);
                dateMap.put("start", calendar.getTime());
                dateMap.put("end", new Date());
                break;
            default:
                throw new AssertionError();
        }
return dateMap;
    }

    public static void setFilterCriteriaArea(PrintedBillsDto billStreets, PrintedBillsDto billDistrict, LocalCouncilDevArea selectedLcda, Map<String, String> filterCriteria) {

        if (billStreets != null && billStreets.getStreetName() != null) {
            filterCriteria.put("street_name", billStreets.getStreetName());
            filterCriteria.put("district", billStreets.getDistrictName());
            filterCriteria.put("lga_name", billStreets.getLga());
        } else if (billDistrict != null && billDistrict.getDistrictName() != null) {
            filterCriteria.put("district", billDistrict.getDistrictName());
            filterCriteria.put("lga_name", billDistrict.getLga());
        } else if (selectedLcda != null && selectedLcda.getLcdaName() != null) {
            filterCriteria.put("lga_name", selectedLcda.getLcdaName());
        }
    }

    public static final String getClientIpAddress() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    public static final String getClientAgent() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getRemoteHost();

        return ipAddress;
    }

    public static String getFileNameFromPart(Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                String fileName = content.substring(content.indexOf('=') + 1)
                        .trim().replace("\"", "");
                return fileName;
            }
        }
        return null;
    }

    public static String getTodayWithoutTime() {

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.AM_PM);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.MONTH, 1);

        String dateValue = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

        return dateValue;
    }

    public static Date getYesterdaysDate() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.getTime();
    }

    public static ByteArrayOutputStream resizeImage(UploadedFile uploadedFile) throws IOException {

        BufferedImage originalImage = ImageIO.read(uploadedFile.getInputstream());
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        System.out.println("File type is: " + type);

        BufferedImage resizedImage = new BufferedImage(600, 400, type);//set width and height of image
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 600, 400, null);
        g.dispose();

        // BufferedImage to ByteArrayInputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", os);

        return os;
    }

    public static Boolean isUserInRole(String[] user_roles) {

        if ((user_roles != null) && (user_roles.length > 0)) {

            org.apache.shiro.subject.Subject presentUser = SecurityUtils.getSubject();

            for (String user_role : user_roles) {
                if (presentUser.hasRole(user_role)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    public static void writeFilterMessage(String msg, FacesMessage.Severity msg_type, String parent) {
        //error_message = "Failed to upload file, please contact ADMIN";
        FacesMessage message = new FacesMessage(msg_type, msg, " ");
        FacesContext.getCurrentInstance().addMessage(parent, message);
    }

}
