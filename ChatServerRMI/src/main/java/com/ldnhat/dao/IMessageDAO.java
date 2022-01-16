package com.ldnhat.dao;

import com.ldnhat.model.MessageModel;

import java.util.List;

public interface IMessageDAO extends GenericDAO<MessageModel> {

    List<MessageModel> findByDiscussionId(int discussionId);
    Long save(MessageModel messageModel);
    MessageModel findOne(int id);
}
