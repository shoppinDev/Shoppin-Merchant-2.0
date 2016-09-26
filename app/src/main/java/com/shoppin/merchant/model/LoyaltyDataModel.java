package com.shoppin.merchant.model;

/**
 * Created by Jaimin Patel on 20-Jun-16.
 */
public class LoyaltyDataModel {

    String loyaltyId,merchantId,shopId,loyaltyname,pincount;

    public LoyaltyDataModel() {
    }

    public LoyaltyDataModel(String loyaltyId, String merchantId, String shopId, String loyaltyname, String pincount) {

        this.loyaltyId = loyaltyId;
        this.merchantId = merchantId;
        this.shopId = shopId;
        this.loyaltyname = loyaltyname;
        this.pincount = pincount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getLoyaltyname() {
        return loyaltyname;
    }

    public void setLoyaltyname(String loyaltyname) {
        this.loyaltyname = loyaltyname;
    }

    public String getPincount() {
        return pincount;
    }

    public void setPincount(String pincount) {
        this.pincount = pincount;
    }

    public String getLoyaltyId() {

        return loyaltyId;
    }

    public void setLoyaltyId(String loyaltyId) {
        this.loyaltyId = loyaltyId;
    }
}
