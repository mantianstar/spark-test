package com.jumao.java.bean;

import java.io.Serializable;

public class FuturesCodeBean implements Serializable {

    private static final long serialVersionUID = 6760686901667027637L;
    private String id;
    private String name;
    private String abbreviation;
    /*'三大期货交易所：1--上海期货交易所；2--郑州商品交易所；3--大连商品交易所；*/
    private String platId;
    private String platIndex;

    public FuturesCodeBean(String id, String name, String abbreviation,
                           String platId, String platIndex) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.platId = platId;
        this.platIndex = platIndex;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getPlatId() {
        return platId;
    }

    public void setPlatId(String platId) {
        this.platId = platId;
    }

    public String getPlatIndex() {
        return platIndex;
    }

    public void setPlatIndex(String platIndex) {
        this.platIndex = platIndex;
    }
}
