package com.nexorigin.simp;

public class CartItemModel {

    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String productID;
    private String productImage;
    private String productTitle;
    private Long freeCoupon;
    private String productPrice;
    private String cuttedPrice;
    private Long productQuantity;
    private Long productMaxQuantity;
    private Long offersApplied;
    private Long couponApplied;
    private boolean inStock;

    public CartItemModel(int type,String productID, String productImage, String productTitle, Long freeCoupon, String productPrice, String cuttedPrice, Long productQuantity, Long offersApplied, Long couponApplied,boolean inStock, Long productMaxQuantity) {
        this.type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.freeCoupon = freeCoupon;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.productMaxQuantity = productMaxQuantity;
        this.productQuantity = productQuantity;
        this.offersApplied = offersApplied;
        this.couponApplied = couponApplied;
        this.inStock = inStock;
    }

    public Long getProductMaxQuantity() {
        return productMaxQuantity;
    }

    public void setProductMaxQuantity(Long productMaxQuantity) {
        this.productMaxQuantity = productMaxQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Long getFreeCoupon() {
        return freeCoupon;
    }

    public void setFreeCoupon(Long freeCoupon) {
        this.freeCoupon = freeCoupon;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Long getOffersApplied() {
        return offersApplied;
    }

    public void setOffersApplied(Long offersApplied) {
        this.offersApplied = offersApplied;
    }

    public Long getCouponApplied() {
        return couponApplied;
    }

    public void setCouponApplied(Long couponApplied) {
        this.couponApplied = couponApplied;
    }

    public CartItemModel(int type) {
        this.type = type;
    }
}
