package com.ldnhat.model;

import java.io.Serializable;

public class StickerDetailModel implements Serializable {

    private int id;
    private String url;
    private StickerModel stickerModel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public StickerModel getStickerModel() {
        return stickerModel;
    }

    public void setStickerModel(StickerModel stickerModel) {
        this.stickerModel = stickerModel;
    }
}
