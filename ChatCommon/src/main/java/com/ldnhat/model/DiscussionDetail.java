package com.ldnhat.model;

import java.io.Serializable;
import java.util.Objects;

public class DiscussionDetail implements Serializable {

    private int id;
    private ClientModel clientModel;
    private Discussion discussion;
    private DiscussionModel discussionModel;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClientModel getClientModel() {
        return clientModel;
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public DiscussionModel getDiscussionModel() {
        return discussionModel;
    }

    public void setDiscussionModel(DiscussionModel discussionModel) {
        this.discussionModel = discussionModel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscussionDetail that = (DiscussionDetail) o;
        return discussion.getId() == that.getDiscussion().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientModel, discussion, discussionModel);
    }
}
