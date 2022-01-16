package com.ldnhat.dao.impl;

import com.ldnhat.dao.IMessageDAO;
import com.ldnhat.mapper.impl.MessageMapper;
import com.ldnhat.model.MessageModel;

import java.util.List;

public class MessageDAO extends AbstractDAO<MessageModel> implements IMessageDAO {

    @Override
    public List<MessageModel> findByDiscussionId(int discussionId) {

        StringBuilder sql = new StringBuilder("SELECT * FROM message ");
        sql.append("INNER JOIN user ON message.user_id = user.user_id ");
        sql.append("WHERE message.discussion_id = ? ");
        sql.append("ORDER BY message.create_date ASC");

        Object[] params = {discussionId};

        return  query(sql.toString(), new MessageMapper(), params);
    }

    @Override
    public Long save(MessageModel messageModel) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("message(content, status, create_date, user_id, discussion_id, type)" );
        sql.append("VALUES(?,?,?,?,?,?)");

        Object[] params = {messageModel.getContent(), messageModel.getStatus(),
            messageModel.getCreateDate(), messageModel.getClientModel().getId(),
                messageModel.getDiscussion().getId(), messageModel.getType()
        };
        return insert(sql.toString(), params);
    }

    @Override
    public MessageModel findOne(int id) {

        StringBuilder sql = new StringBuilder("SELECT * FROM message ");
        sql.append("INNER JOIN user ON message.user_id = user.user_id ");
        sql.append("WHERE message.id = ?");

        Object[] params = {id};

        List<MessageModel> messageModels = query(sql.toString(), new MessageMapper(), params);

        return messageModels.isEmpty() ? null : messageModels.get(0);
    }
}
