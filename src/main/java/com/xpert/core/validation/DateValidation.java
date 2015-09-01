package com.xpert.core.validation;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author arnaldo
 */
public class DateValidation {

    public static boolean validateDateRange(Date dataStart, Date dataEnd) {

        boolean status = true;

        if (dataStart != null && dataEnd != null) {
            if (dataStart.compareTo(dataEnd) > 0) {
                status = false;
            }
        } else {
            status = false;
        }

        return status;
    }

    public static boolean validateDateRange(Calendar dataStart, Calendar dataEnd) {

        if (dataStart != null && dataEnd != null) {
            return validateDateRange(dataStart.getTime(), dataEnd.getTime());
        } else {
            return false;
        }

    }
}
