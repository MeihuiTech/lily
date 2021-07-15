package com.mei.hui.util;

public enum PlatFormEnum {
    web("后台管理系统"),
    api("对外sdk调用"),
    app("android 和 IOS 端");

    private String des;

    PlatFormEnum(String des){
        this.des = des;
    }


    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
