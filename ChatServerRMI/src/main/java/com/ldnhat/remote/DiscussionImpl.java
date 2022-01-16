package com.ldnhat.remote;

import com.ldnhat.model.DiscussionDetail;
import com.ldnhat.model.DiscussionModel;
import com.ldnhat.model.MessageModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class DiscussionImpl extends UnicastRemoteObject implements DiscussionRemote {

    private DiscussionDetail discussionDetail;
    //private DiscussionModel discussionModel;

    public DiscussionImpl() throws RemoteException {

    }

    @Override
    public void addMessage(MessageModel messageModel) throws RemoteException {

    }

    public DiscussionDetail getDiscussionDetail() {
        return discussionDetail;
    }

    public void setDiscussionDetail(DiscussionDetail discussionDetail) {
        this.discussionDetail = discussionDetail;
    }

//    public DiscussionModel getDiscussionModel() {
//        return discussionModel;
//    }
//
//    public void setDiscussionModel(DiscussionModel discussionModel) {
//        this.discussionModel = discussionModel;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DiscussionImpl that = (DiscussionImpl) o;
        return this.discussionDetail.getId() == that.getDiscussionDetail().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), discussionDetail);
    }
}
