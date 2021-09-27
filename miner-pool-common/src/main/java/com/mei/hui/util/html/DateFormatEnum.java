package com.mei.hui.util.html;

public enum DateFormatEnum {
    yyyy_MM("yyyy-MM"),
    yyyy_MM_dd("yyyy-MM-dd"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss");


    DateFormatEnum(String format){
        this.format = format;
    }

    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
