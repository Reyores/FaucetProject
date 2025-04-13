package com.faucetproject.model;

public class FaucetRequest {
    private String address;

    public FaucetRequest() {}

    public FaucetRequest(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
