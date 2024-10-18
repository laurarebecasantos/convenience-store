package com.api.rest.conveniencestore.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String formatDate(LocalDateTime dateTime) { //args local dtetime
        return dateTime.format(DATE_FORMAT);
    }
}


