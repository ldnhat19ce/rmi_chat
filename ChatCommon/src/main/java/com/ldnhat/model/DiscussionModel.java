package com.ldnhat.model;

import java.io.Serializable;
import java.util.List;

public class DiscussionModel implements Serializable {

    private List<ClientModel> members;
    private List<MessageModel> chats;
    private List<DiscussionDetail> discussionDetails;

    public List<ClientModel> getMembers() {
        return members;
    }

    public void setMembers(List<ClientModel> members) {
        this.members = members;
    }

    public List<MessageModel> getChats() {
        return chats;
    }

    public void setChats(List<MessageModel> chats) {
        this.chats = chats;
    }

    public List<DiscussionDetail> getDiscussionDetails() {
        return discussionDetails;
    }

    public void setDiscussionDetails(List<DiscussionDetail> discussionDetails) {
        this.discussionDetails = discussionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null){
            return false;
        }
        DiscussionModel d = (DiscussionModel) o;
        //containsAll:  kiểm tra xem mảng arraylist có chứa tất cả các phần tử trong tập chỉ định hay không
        return this.members.containsAll(d.members) && d.members.containsAll(this.members);
    }
}
