package com.ldnhat.mapper.impl;

import com.ldnhat.mapper.RowMapper;
import com.ldnhat.model.ClientModel;
import com.ldnhat.model.Discussion;
import com.ldnhat.model.DiscussionDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscussionDetailMapper implements RowMapper<DiscussionDetail> {

    @Override
    public DiscussionDetail mapRow(ResultSet rs) throws SQLException {

        DiscussionDetail discussionDetail = new DiscussionDetail();

        discussionDetail.setId(rs.getInt("discussion_detail.detail_id"));

        ClientModel clientModel = new ClientModel();
        clientModel.setId(rs.getInt("discussion_detail.user_id"));

        discussionDetail.setClientModel(clientModel);

        Discussion discussion = new Discussion();
        discussion.setId(rs.getInt("discussion.discussion_id"));
        discussion.setName(rs.getString("discussion.discussion_name"));

        discussionDetail.setDiscussion(discussion);
        discussionDetail.setType(rs.getString("discussion_detail.type"));
        return discussionDetail;
    }
}
