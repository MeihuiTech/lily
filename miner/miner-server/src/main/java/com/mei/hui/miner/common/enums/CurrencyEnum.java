package com.mei.hui.miner.common.enums;

/**
 * 币种枚举类
 */
public enum CurrencyEnum {

    FIL(1L,"fil币","FIL"),
    CHIA(2L,"起亚币","XCH");

    private Long currencyId;
    private String des;
    /*币种代币单位名称*/
    private String unit;

    CurrencyEnum(Long currencyId,String des,String unit){
        this.currencyId = currencyId;
        this.des = des;
        this.unit = unit;
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
     * 根据币种区块链项目名称type返回币种代币单位名称
     * @param type 别的表里的type：货币种类,FIL,CHIA
     * @return
     */
    public static String getCurrencyNameByProjectName(String type){
        for(CurrencyEnum currency : CurrencyEnum.values()){
            if(currency.name().equals(type)){
                return currency.getUnit();
            }
        }
        return null;
    }

    /**
     * 根据币种代币单位名称查出来币种区块链项目名称type
     * @param projectName
     * @return
     */
    public static String getProjectNameByCurrencyName(String currencyUnit){
        for(CurrencyEnum currency : CurrencyEnum.values()){
            if(currency.getUnit().equals(currencyUnit)){
                return currency.name();
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
