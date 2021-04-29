package com.mei.hui.util;

public enum PlatFormTypeEnum {
    admin("后台管理系统 端"),
    app("android 和 IOS 端");

    private String type;

    PlatFormTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
