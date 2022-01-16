package com.ldnhat.service.impl;

import com.ldnhat.dao.IDiscussionDAO;
import com.ldnhat.dao.impl.DiscussionDAO;
import com.ldnhat.model.Discussion;
import com.ldnhat.service.IDiscussionService;

public class DiscussionService implements IDiscussionService {

    private IDiscussionDAO discussionDAO;

    public DiscussionService() {
        discussionDAO = new DiscussionDAO();
    }

    @Override
    public Discussion findOne(int id) {
        return discussionDAO.findOne(id);
    }

    @Override
    public Discussion save(Discussion discussion) {
        Long id = discussionDAO.save(discussion);
        return findOne(Math.toIntExact(id));
    }
}
