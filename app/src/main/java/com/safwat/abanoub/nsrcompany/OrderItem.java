package com.safwat.abanoub.nsrcompany;

class OrderItem {
    public String pushID;
    public String productPushID;
    public String quantity;

    public OrderItem() {
    }

    public OrderItem(String productPushID, String quantity) {
        this.productPushID=productPushID;
        this.quantity=quantity;
    }
}
