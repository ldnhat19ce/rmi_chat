package com.ldnhat.mapper.impl;

import com.ldnhat.mapper.RowMapper;
import com.ldnhat.model.ClientModel;
import com.ldnhat.model.Discussion;
import com.ldnhat.model.MessageModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageMapper implements RowMapper<MessageModel> {
    @Override
    public MessageModel mapRow(ResultSet rs) throws SQLException {

        MessageModel messageModel = new MessageModel();

        messageModel.setId(rs.getInt("message.id"));

        ClientModel clientModel = new ClientModel();
        clientModel.setId(rs.getInt("user.user_id"));
        clientModel.setName(rs.getString("user.name"));
        clientModel.setImage(rs.getString("user.image"));
        messageModel.setClientModel(clientModel);

        Discussion discussion = new Discussion();
        discussion.setId(rs.getInt("message.discussion_id"));
        messageModel.setDiscussion(discussion);

        messageModel.setContent(rs.getString("message.content"));
        messageModel.setStatus(rs.getInt("message.status"));
        messageModel.setCreateDate(rs.getTimestamp("message.create_date"));
        messageModel.setType(rs.getString("message.type"));

        return messageModel;
    }
}
