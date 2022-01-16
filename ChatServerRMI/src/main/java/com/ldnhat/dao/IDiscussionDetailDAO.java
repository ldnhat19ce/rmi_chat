package com.ldnhat.dao;

import com.ldnhat.model.DiscussionDetail;

import java.util.List;

public interface IDiscussionDetailDAO extends GenericDAO<DiscussionDetail> {

    List<DiscussionDetail> findByUserId(int userId);
    List<DiscussionDetail> findByDiscussionId(int discussionId);
    DiscussionDetail findByDiscussionIdAndUserId(int discussionId, int userId);
    Long save(DiscussionDetail discussionDetail);
    DiscussionDetail findOne(int id);
    DiscussionDetail findUserPrivate(int userId);
    DiscussionDetail findOneByUserId(int userId, String type);
    int findDiscussionPrivate(int userSender, int userReceive);
    int findDiscussionGroup(int userSender, int userReceive);
    List<Integer> findDiscussionGroups(int userSender, int userReceive);

}
