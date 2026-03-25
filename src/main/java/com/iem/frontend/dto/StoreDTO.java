package com.iem.frontend.dto;

public class StoreDTO {
    private Integer id;
    private String storeName;
    // Add other fields you need

    // IMPORTANT: No-args constructor, Getters, and Setters
    public StoreDTO() {}
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}