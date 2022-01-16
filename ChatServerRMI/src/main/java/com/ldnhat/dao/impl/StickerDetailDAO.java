package com.ldnhat.dao.impl;

import com.ldnhat.dao.IStickerDetailDAO;
import com.ldnhat.mapper.impl.StickerDetailMapper;
import com.ldnhat.model.StickerDetailModel;

import java.util.List;

public class StickerDetailDAO extends AbstractDAO<StickerDetailModel> implements IStickerDetailDAO {

    @Override
    public List<StickerDetailModel> findByStickerId(int stickerId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM sticker_detail ");
        sql.append("INNER JOIN stickers ON sticker_detail.sticker_id = stickers.id ");
        sql.append("WHERE sticker_detail.sticker_id = ?");

        Object[] params = {stickerId};
        return query(sql.toString(), new StickerDetailMapper(), params);
    }
}
