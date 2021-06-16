package com.mei.hui.util;

/**
 * 币种枚举类
 */
public enum CurrencyEnum {

    FIL(1L,"fil币"),
    XCH(2L,"起亚币"),
    BZZ(3L,"swarm币");;

    private Long currencyId;
    private String des;

    CurrencyEnum(Long currencyId, String des){
        this.currencyId = currencyId;
        this.des = des;
    }

    /**
     * 判断是否是枚举名字，是返回 true,否则返回false
     * @param name
     * @return
     */
    public static boolean exist(String name){
        for(CurrencyEnum currency : CurrencyEnum.values()){
            if(currency.name().equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * 名字不存在返回 true
     * @param name
     * @return
     */
    public static boolean isNotExist(String name){
        return exist(name) ? false : true;
    }

    /**
     * 根据币种id查询币种枚举类实体
     * @param currencyId
     * @return
     */
    public static CurrencyEnum getCurrency(Long currencyId){
        for(CurrencyEnum currency : CurrencyEnum.values()){
            if(currency.getCurrencyId().equals(currencyId)){
                return currency;
            }
        }
        return null;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

}
