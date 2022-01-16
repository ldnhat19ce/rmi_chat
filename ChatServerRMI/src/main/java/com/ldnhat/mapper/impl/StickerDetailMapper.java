package com.ldnhat.mapper.impl;

import com.ldnhat.mapper.RowMapper;
import com.ldnhat.model.StickerDetailModel;
import com.ldnhat.model.StickerModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StickerDetailMapper implements RowMapper<StickerDetailModel> {

    @Override
    public StickerDetailModel mapRow(ResultSet rs) throws SQLException {

        StickerDetailModel stickerDetailModel = new StickerDetailModel();

        stickerDetailModel.setId(rs.getInt("sticker_detail.id"));
        stickerDetailModel.setUrl(rs.getString("sticker_detail.url"));

        StickerModel stickerModel = new StickerModel();
        stickerModel.setId(rs.getInt("stickers.id"));
        stickerModel.setName(rs.getString("stickers.name"));
        stickerDetailModel.setStickerModel(stickerModel);

        return stickerDetailModel;
    }
}
