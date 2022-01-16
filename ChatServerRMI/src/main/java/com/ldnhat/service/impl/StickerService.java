package com.ldnhat.service.impl;

import com.ldnhat.dao.IStickerDAO;
import com.ldnhat.dao.impl.StickerDAO;
import com.ldnhat.model.StickerModel;
import com.ldnhat.service.IStickerService;

import java.util.List;

public class StickerService implements IStickerService {

    private IStickerDAO stickerDAO;

    public StickerService() {
        stickerDAO = new StickerDAO();
    }

    @Override
    public List<StickerModel> findAll() {
        return stickerDAO.findAll();
    }
}
