/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icsl.ledar.ejb.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author sfagade
 */
@Stateless
@LocalBean
public class DataProcessorUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * This method convert string var to decimal
     *
     * @param value
     * @return
     */
    public BigDecimal parseStringToBigDecimal(String value) {

        if ((value != null) && (!value.isEmpty())) {
            try {
                // Create a DecimalFormat that fits your requirements
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');
                String pattern = "#,##0.0#";
                DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
                decimalFormat.setParseBigDecimal(true);

                // parse the string
                BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(value);

                return bigDecimal;
            } catch (ParseException ex) {
                Logger.getLogger(DataProcessorUtil.class.getName()).log(Level.SEVERE, "Failed to convert to BigDecimal: " + value, ex);
            }
        }

        return null;
    }

    public String renameImageFile(String original_name, String append) {

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

    public String formartBigDecimanToString(BigDecimal amount) {
        if (amount != null) {

            amount = amount.setScale(2, BigDecimal.ROUND_DOWN);

            DecimalFormat decFormat = new DecimalFormat();

            decFormat.setMaximumFractionDigits(2);

            decFormat.setMinimumFractionDigits(0);

            decFormat.setGroupingUsed(false);

            return decFormat.format(amount);
        }
        return "0.0";
    }
}
