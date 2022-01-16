package com.ldnhat.dao.impl;

import com.ldnhat.dao.IDiscussionDAO;
import com.ldnhat.mapper.impl.DiscussionMapper;
import com.ldnhat.model.Discussion;

import java.util.List;

public class DiscussionDAO extends AbstractDAO<Discussion> implements IDiscussionDAO {


    @Override
    public Long save(Discussion discussion) {

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("discussion(discussion_name) VALUES(?)");

        Object[] params = {discussion.getName()};

        return insert(sql.toString(), params);
    }

    @Override
    public Discussion findOne(int id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM discussion ");
        sql.append("WHERE discussion.discussion_id = ?");

        Object[] params = {id};

        List<Discussion> discussions = query(sql.toString(), new DiscussionMapper(), params);
        return discussions.isEmpty() ? null : discussions.get(0);
    }
}
