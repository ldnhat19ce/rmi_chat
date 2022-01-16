package com.ldnhat.service.impl;

import com.ldnhat.dao.IMessageDAO;
import com.ldnhat.dao.impl.MessageDAO;
import com.ldnhat.model.MessageModel;
import com.ldnhat.service.IMessageService;

import java.sql.Timestamp;
import java.util.List;

public class MessageService implements IMessageService {

    private IMessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    @Override
    public List<MessageModel> findByDiscussionId(int discussionId) {
        return messageDAO.findByDiscussionId(discussionId);
    }

    @Override
    public MessageModel save(MessageModel messageModel) {
        messageModel.setCreateDate(new Timestamp(System.currentTimeMillis()));

        Long id = messageDAO.save(messageModel);
        return findOne(Math.toIntExact(id));
    }

    @Override
    public MessageModel findOne(int id) {
        return messageDAO.findOne(id);
    }
}
