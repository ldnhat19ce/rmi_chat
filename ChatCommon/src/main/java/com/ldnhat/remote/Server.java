package com.ldnhat.remote;

import com.ldnhat.model.*;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {

    ClientModel findByUsernameAndPassword(String username, String password) throws RemoteException;
    ClientModel save(ClientModel clientModel) throws RemoteException;
    String updateProfilePicture(ClientRemote clientRemote, File file) throws RemoteException;
    void connectClient(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException;
    void disconnect(ClientRemote clientRemote) throws RemoteException;
    void updateDiscussion(DiscussionDetail discussionDetail, MessageModel messageModel) throws RemoteException;
    void saveDiscussion(DiscussionDetail discussionDetail) throws RemoteException;
    void createCall(VideoCallModel videoCallModel) throws RemoteException;
    boolean isCalling() throws RemoteException;
    void addActiveMember(List<ClientModel> clientModels, ClientModel activeMember) throws RemoteException;
    void removeActiveMember(List<ClientModel> clientModels, ClientModel activeMember) throws RemoteException;
    boolean checkDiscussionPrivate(int userSender, int userReceive) throws RemoteException;
    int checkDiscussionGroup(List<ClientModel> clientModels) throws RemoteException;
    DiscussionDetail findDiscussionId(int userSender, int userReceive) throws RemoteException;
    DiscussionDetail saveDiscussionPrivate(DiscussionDetail discussionDetail, MessageModel messageModel)
            throws RemoteException;
    void sendFile(File file, DiscussionDetail discussionDetail, MessageModel messageModel)
            throws RemoteException;
    List<StickerDetailModel> findAllStickers() throws RemoteException;
    void sendIcon(DiscussionDetail discussionDetail, MessageModel messageModel)
        throws RemoteException;
    List<File> findAllFileByUserId(int userId, String fileType) throws RemoteException;
}
