package com.ldnhat.service.impl;

import com.ldnhat.dao.impl.DiscussionDetailDAO;
import com.ldnhat.dao.IDiscussionDetailDAO;
import com.ldnhat.model.ClientModel;
import com.ldnhat.model.DiscussionDetail;
import com.ldnhat.service.IDiscussionDetailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DiscussionDetailService implements IDiscussionDetailService {

    private IDiscussionDetailDAO discussionDetailDAO;

    public DiscussionDetailService() {
        discussionDetailDAO = new DiscussionDetailDAO();
    }

    @Override
    public List<DiscussionDetail> findByUserId(int userId) {
        return discussionDetailDAO.findByUserId(userId);
    }

    @Override
    public List<DiscussionDetail> findByDiscussionId(int discussionId) {
        return discussionDetailDAO.findByDiscussionId(discussionId);
    }

    @Override
    public DiscussionDetail findByDiscussionIdAndUserId(int discussionId, int userId) {
        return discussionDetailDAO.findByDiscussionIdAndUserId(discussionId, userId);
    }

    @Override
    public DiscussionDetail findOne(int id) {
        return discussionDetailDAO.findOne(id);
    }

    @Override
    public DiscussionDetail save(DiscussionDetail discussionDetail) {
        Long id = discussionDetailDAO.save(discussionDetail);
        return findOne(Math.toIntExact(id));
    }

    @Override
    public DiscussionDetail findUserPrivate(int userId) {
        return discussionDetailDAO.findUserPrivate(userId);
    }

    @Override
    public boolean checkDiscussionPrivate(int userSender, int userReceive) {
        return (findUserPrivate(userSender) != null && findUserPrivate(userReceive) != null);
    }

    @Override
    public DiscussionDetail findOneByUserId(int userId, String type) {
        return discussionDetailDAO.findOneByUserId(userId, type);
    }

    @Override
    public int findDiscussionPrivate(int userSender, int userReceive) {
        return discussionDetailDAO.findDiscussionPrivate(userSender, userReceive);
    }

    //kiểm tra user[0] lần lượt với các user còn lại
    @Override
    public boolean checkDiscussionGroup(List<Integer> users) {

        List<Integer> checks = new ArrayList<>();
        for (Integer user : users){
            if (discussionDetailDAO.findDiscussionGroup(users.get(0), user) != -1){
                checks.add(user);
            }
        }

        return checks.size() < users.size();
    }

    @Override
    public int findDiscussionGroup(List<Integer> users){
        List<Integer> checks = new ArrayList<>();
        for (Integer user : users){
            if (discussionDetailDAO.findDiscussionGroup(users.get(0), user) != -1){
                checks.add(user);
            }
        }

        if (checks.size() < users.size()){
            return -1;
        }else{
            List<Integer> checkDiscussions = new ArrayList<>();
            for (Integer check : checks){
                List<Integer> lists = discussionDetailDAO.findDiscussionGroups(checks.get(0), check);
                checkDiscussions.addAll(lists);
            }
           return mostFrequent(checkDiscussions);
        }
    }

    private int mostFrequent(List<Integer> users){
        Collections.sort(users);
        int maxCount = 1;
        int res = users.get(0);
        int count = 1;

        for (int i = 1; i < users.size(); i++){
            if (users.get(i).equals(users.get(i - 1))){
                count++;
            }else{
                if (count > maxCount){
                    res = users.get(i - 1);
                }
                count = 1;
            }
        }

        if (count > maxCount){
            maxCount = count;
            res = users.get((users.size()) - 1);
        }
        return res;
    }

    public static void main(String[] args) {
        DiscussionDetailService service = new DiscussionDetailService();
        List<Integer> users = Arrays.asList(1, 2, 5);
        System.out.println(service.findDiscussionGroup(users));
    }
}
