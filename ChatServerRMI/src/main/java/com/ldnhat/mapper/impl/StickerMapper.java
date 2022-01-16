package com.ldnhat.mapper.impl;

import com.ldnhat.mapper.RowMapper;
import com.ldnhat.model.StickerModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StickerMapper implements RowMapper<StickerModel> {

    @Override
    public StickerModel mapRow(ResultSet rs) throws SQLException {

        StickerModel stickerModel = new StickerModel();
        stickerModel.setId(rs.getInt("stickers.id"));
        stickerModel.setName(rs.getString("stickers.name"));

        return stickerModel;
    }
}
