package com.ldnhat.mapper.impl;

import com.ldnhat.mapper.RowMapper;
import com.ldnhat.model.Discussion;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscussionMapper implements RowMapper<Discussion> {
    @Override
    public Discussion mapRow(ResultSet rs) throws SQLException {

        Discussion discussion = new Discussion();

        discussion.setId(rs.getInt("discussion.discussion_id"));
        discussion.setName(rs.getString("discussion.discussion_name"));

        return discussion;
    }
}
