package com.xpert.core.validation;

import com.xpert.utils.DateUtils;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author arnaldo
 */
public class DateValidation implements Serializable {

    private static final long serialVersionUID = -8141147905148517811L;

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

    public static boolean validateDateRange(LocalDateTime dataStart, LocalDateTime dataEnd) {
        if (dataStart != null && dataEnd != null) {
            Date inicio = DateUtils.locaDateTimeToDate(dataStart);
            Date fim = DateUtils.locaDateTimeToDate(dataEnd);
            return validateDateRange(inicio, fim);
        } else {
            return false;
        }
    }
}
