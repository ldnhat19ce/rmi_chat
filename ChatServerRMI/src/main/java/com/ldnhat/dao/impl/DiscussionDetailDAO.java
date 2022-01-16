package com.ldnhat.dao.impl;

import com.ldnhat.dao.IDiscussionDetailDAO;
import com.ldnhat.mapper.impl.DiscussionDetailMapper;
import com.ldnhat.model.DiscussionDetail;
import com.ldnhat.util.Constant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDetailDAO extends AbstractDAO<DiscussionDetail>
    implements IDiscussionDetailDAO {

    @Override
    public List<DiscussionDetail> findByUserId(int userId) {

        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append("discussion_detail INNER JOIN discussion ");
        sql.append("ON discussion_detail.discussion_id = discussion.discussion_id ");
        sql.append("WHERE discussion_detail.user_id = ?");

        Object[] params = {userId};

        return query(sql.toString(), new DiscussionDetailMapper(), params);
    }

    @Override
    public List<DiscussionDetail> findByDiscussionId(int discussionId) {

        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append("discussion_detail INNER JOIN discussion ");
        sql.append("ON discussion_detail.discussion_id = discussion.discussion_id ");
        sql.append("WHERE discussion_detail.discussion_id = ?");

        Object[] params = {discussionId};

        return query(sql.toString(),
                new DiscussionDetailMapper(), params);
    }

    @Override
    public Long save(DiscussionDetail discussionDetail) {
        StringBuilder sql = new StringBuilder("INSERT INTO discussion_detail");
        sql.append("(user_id, discussion_id, type) ");
        sql.append("VALUES(?,?,?)");

        Object[] params = {discussionDetail.getClientModel().getId(), discussionDetail.getDiscussion().getId()
        , discussionDetail.getType()
        };
        return insert(sql.toString(), params);
    }

    @Override
    public DiscussionDetail findOne(int id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM discussion_detail ");
        sql.append("INNER JOIN discussion ON discussion_detail.discussion_id = discussion.discussion_id ");
        sql.append("WHERE discussion_detail.detail_id = ?");

        Object[] params = {id};

        List<DiscussionDetail> discussionDetails = query(sql.toString(), new DiscussionDetailMapper(), params);

        return discussionDetails.isEmpty() ? null : discussionDetails.get(0);
    }

    @Override
    public DiscussionDetail findByDiscussionIdAndUserId(int discussionId, int userId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM discussion_detail ");
        sql.append("INNER JOIN discussion ON discussion_detail.discussion_id = discussion.discussion_id ");
        sql.append("WHERE discussion_detail.discussion_id = ? AND ");
        sql.append("discussion_detail.user_id = ?");

        Object[] params = {discussionId, userId};

        List<DiscussionDetail> discussionDetails = query(sql.toString(), new DiscussionDetailMapper(), params);
        return discussionDetails.isEmpty() ? null : discussionDetails.get(0);
    }

    @Override
    public DiscussionDetail findUserPrivate(int userId) {

        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append("discussion_detail INNER JOIN discussion ");
        sql.append("ON discussion_detail.discussion_id = discussion.discussion_id ");
        sql.append("WHERE discussion_detail.user_id = ? AND type = ?");

        Object[] params = {userId, "PRIVATE"};

        List<DiscussionDetail> discussionDetails = query(sql.toString(), new DiscussionDetailMapper(), params);
        return discussionDetails.isEmpty() ? null : discussionDetails.get(0);
    }

    @Override
    public DiscussionDetail findOneByUserId(int userId, String type) {
        StringBuilder sql = new StringBuilder("SELECT * FROM discussion_detail ");
        sql.append("INNER JOIN discussion ON discussion_detail.discussion_id = discussion.discussion_id ");
        sql.append("WHERE discussion_detail.user_id = ? AND discussion_detail.type = ?");

        Object[] params = {userId, type};

        List<DiscussionDetail> discussionDetails = query(sql.toString(), new DiscussionDetailMapper(), params);

        return discussionDetails.isEmpty() ? null : discussionDetails.get(0);
    }

    //self join
    @Override
    public int findDiscussionPrivate(int userSender, int userReceive) {
        StringBuilder sql = new StringBuilder("SELECT a.discussion_id ");
        sql.append("FROM discussion_detail a, discussion_detail b ");
        sql.append("WHERE a.discussion_id = b.discussion_id ");
        sql.append("AND a.user_id = ? AND b.user_id = ? ");
        sql.append("AND a.type = ? AND b.type = ?");

        Connection conn = getConnection();

        try {
            PreparedStatement ptmt = conn.prepareStatement(sql.toString());
            ptmt.setInt(1, userSender);
            ptmt.setInt(2, userReceive);
            ptmt.setString(3, Constant.DISCUSSION_PRIVATE);
            ptmt.setString(4, Constant.DISCUSSION_PRIVATE);

            ResultSet rs = ptmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int findDiscussionGroup(int userSender, int userReceive) {
        StringBuilder sql = new StringBuilder("SELECT a.discussion_id ");
        sql.append("FROM discussion_detail a, discussion_detail b ");
        sql.append("WHERE a.discussion_id = b.discussion_id ");
        sql.append("AND a.user_id = ? AND b.user_id = ? ");
        sql.append("AND a.type = ? AND b.type = ?");

        Connection conn = getConnection();

        try {
            PreparedStatement ptmt = conn.prepareStatement(sql.toString());
            ptmt.setInt(1, userSender);
            ptmt.setInt(2, userReceive);
            ptmt.setString(3, Constant.DISCUSSION_GROUP);
            ptmt.setString(4, Constant.DISCUSSION_GROUP);

            ResultSet rs = ptmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Integer> findDiscussionGroups(int userSender, int userReceive) {
        List<Integer> lists = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT a.discussion_id ");
        sql.append("FROM discussion_detail a, discussion_detail b ");
        sql.append("WHERE a.discussion_id = b.discussion_id ");
        sql.append("AND a.user_id = ? AND b.user_id = ? ");
        sql.append("AND a.type = ? AND b.type = ?");

        Connection conn = getConnection();

        try {
            PreparedStatement ptmt = conn.prepareStatement(sql.toString());
            ptmt.setInt(1, userSender);
            ptmt.setInt(2, userReceive);
            ptmt.setString(3, Constant.DISCUSSION_GROUP);
            ptmt.setString(4, Constant.DISCUSSION_GROUP);

            ResultSet rs = ptmt.executeQuery();

            while (rs.next()){
                lists.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public static void main(String[] args) {
        IDiscussionDetailDAO dao = new DiscussionDetailDAO();
        System.out.println(dao.findDiscussionPrivate(2, 1));
    }


}
