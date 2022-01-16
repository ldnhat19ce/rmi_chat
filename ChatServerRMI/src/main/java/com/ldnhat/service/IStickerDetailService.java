package com.ldnhat.service;

import com.ldnhat.model.StickerDetailModel;

import java.util.List;

public interface IStickerDetailService {

    List<StickerDetailModel> findByStickerId(int stickerId);
}
