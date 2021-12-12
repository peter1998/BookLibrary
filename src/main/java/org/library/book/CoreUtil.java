package org.library.book;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CoreUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public Date parseDate(String rawDate) {
        try {
            return FORMAT.parse(rawDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date parameter");
        }
    }
}
