package com.example.autodialer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Tools {
    public boolean isValid(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
