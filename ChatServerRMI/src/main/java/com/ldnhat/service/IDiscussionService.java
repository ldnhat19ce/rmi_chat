package com.ldnhat.service;

import com.ldnhat.model.Discussion;

public interface IDiscussionService {

    Discussion save(Discussion discussion);
    Discussion findOne(int id);
}
