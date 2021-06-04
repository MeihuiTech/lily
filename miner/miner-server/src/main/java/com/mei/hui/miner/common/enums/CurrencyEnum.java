package com.mei.hui.miner.common.enums;

/**
 * 币种枚举类
 */
public enum CurrencyEnum {

    FIL(1L,"fil币","FIL"),
    CHIA(2L,"起亚币","XCH");

    private Long currencyId;
    private String des;
    /*币种代币名称*/
    private String name;

    CurrencyEnum(Long currencyId,String des,String name){
        this.currencyId = currencyId;
        this.des = des;
        this.name = name;
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

    /**
     * 根据币种区块链项目名称type返回币种代币名称name
     * @param type 别的表里的type：货币种类,FIL,CHIA
     * @return
     */
    public static String getCurrencyNameByProjectName(String type){
        for(CurrencyEnum currency : CurrencyEnum.values()){
            if(currency.name().equals(type)){
                return currency.getName().toString();
            }
        }
        return null;
    }

    /**
     * 根据币种代币名称name查出来币种区块链项目名称type
     * @param projectName
     * @return
     */
    public static String getProjectNameByCurrencyName(String currencyName){
        for(CurrencyEnum currency : CurrencyEnum.values()){
            if(currency.getName().equals(currencyName)){
                return currency.name().toString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
