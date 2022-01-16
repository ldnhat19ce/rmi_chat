package com.ldnhat.dao.impl;

import com.ldnhat.dao.IStickerDAO;
import com.ldnhat.mapper.impl.StickerMapper;
import com.ldnhat.model.StickerModel;

import java.util.List;

public class StickerDAO extends AbstractDAO<StickerModel> implements IStickerDAO {

    @Override
    public List<StickerModel> findAll() {
        StringBuilder sql = new StringBuilder("SELECT * FROM sticks");

        return query(sql.toString(), new StickerMapper());
    }
}
