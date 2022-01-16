package com.ldnhat.service;

import com.ldnhat.model.MessageModel;

import java.util.List;

public interface IMessageService {

    List<MessageModel> findByDiscussionId(int discussionId);
    MessageModel save(MessageModel messageModel);
    MessageModel findOne(int id);
}
