package com.ldnhat.dao;

import com.ldnhat.model.Discussion;

import java.util.List;

public interface IDiscussionDAO extends GenericDAO<Discussion> {

    Long save(Discussion discussion);
    Discussion findOne(int id);
}
