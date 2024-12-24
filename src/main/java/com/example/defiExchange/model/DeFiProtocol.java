package com.example.defiExchange.model;

public class DeFiProtocol {
    private String name;
    private String url;
    private String description;
    private double fee;

    // Constructor
    public DeFiProtocol(String name, String url, String description, double fee) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.fee = fee;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
