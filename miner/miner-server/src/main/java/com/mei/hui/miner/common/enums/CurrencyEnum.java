package com.mei.hui.miner.common.enums;

/**
 * 币种枚举类
 */
public enum CurrencyEnum {

    FIL(1L,"fil币"),
    XCH(2L,"起亚币");

    private Long currencyId;
    private String des;

    CurrencyEnum(Long currencyId,String des){
        this.currencyId = currencyId;
        this.des = des;
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
