package com.ldnhat.dao;

import com.ldnhat.model.StickerDetailModel;

import java.util.List;

public interface IStickerDetailDAO extends GenericDAO<StickerDetailModel> {

    List<StickerDetailModel> findByStickerId(int stickerId);
}
