package com.klx.ebookbackend.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public  class OrderInfo {
    private String address;
    private String receiver;
    private String tel;
    private List<Integer> itemIds;

    // Getters and Setters

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}