package com.axllblc.worlddays;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Utils {
    public static String firstLetterToUppercase(String string) {
        if (!string.isEmpty())
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        else
            return string;
    }

    public static String formatFullDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
    }
}
