package com.ldnhat.service;

import com.ldnhat.model.DiscussionDetail;

import java.util.List;

public interface IDiscussionDetailService {

    List<DiscussionDetail> findByUserId(int userId);
    List<DiscussionDetail> findByDiscussionId(int discussionId);
    DiscussionDetail findByDiscussionIdAndUserId(int discussionId, int userId);
    DiscussionDetail save(DiscussionDetail discussionDetail);
    DiscussionDetail findOne(int id);
    DiscussionDetail findUserPrivate(int userId);
    boolean checkDiscussionPrivate(int userSender, int userReceive);
    DiscussionDetail findOneByUserId(int userId, String type);
    int findDiscussionPrivate(int userSender, int userReceive);
    boolean checkDiscussionGroup(List<Integer> users);
    int findDiscussionGroup(List<Integer> users);
}
