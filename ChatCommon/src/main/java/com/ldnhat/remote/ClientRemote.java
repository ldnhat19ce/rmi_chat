package com.ldnhat.remote;

import com.ldnhat.model.ClientModel;
import com.ldnhat.model.DiscussionDetail;
import com.ldnhat.model.DiscussionModel;
import com.ldnhat.model.VideoCallModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {

    ClientModel getClient() throws RemoteException;
    void UpdateDiscussion() throws RemoteException;
    void setOnline(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException;
    void disconnect(ClientRemote clientRemote) throws RemoteException;
    void updateUser(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException;
    void saveDiscussionDetail(DiscussionDetail discussionDetail) throws RemoteException;
    void updateDiscussionDetail(DiscussionDetail discussionDetail) throws RemoteException;
    void deleteDiscussionDetail(DiscussionDetail discussionDetail) throws RemoteException;
    void requestCall(VideoCallModel videoCallModel) throws RemoteException;
    void addActiveMember(ClientModel clientModel) throws RemoteException;
    void removeActiveMember(ClientModel clientModel) throws RemoteException;
}
