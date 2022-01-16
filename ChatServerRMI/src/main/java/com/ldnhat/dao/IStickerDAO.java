package com.ldnhat.dao;

import com.ldnhat.model.StickerModel;

import java.util.List;

public interface IStickerDAO extends GenericDAO<StickerModel> {

    List<StickerModel> findAll();
}
