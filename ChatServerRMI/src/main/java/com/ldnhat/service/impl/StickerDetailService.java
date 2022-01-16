package com.ldnhat.service.impl;

import com.ldnhat.dao.IStickerDetailDAO;
import com.ldnhat.dao.impl.StickerDetailDAO;
import com.ldnhat.model.StickerDetailModel;
import com.ldnhat.service.IStickerDetailService;

import java.util.List;

public class StickerDetailService implements IStickerDetailService {

    private IStickerDetailDAO stickerDetailDAO;

    public StickerDetailService() {
        stickerDetailDAO = new StickerDetailDAO();
    }

    @Override
    public List<StickerDetailModel> findByStickerId(int stickerId) {
        return stickerDetailDAO.findByStickerId(stickerId);
    }
}
