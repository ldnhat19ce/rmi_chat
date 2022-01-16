package com.ldnhat.remote;

import com.ldnhat.model.MessageModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DiscussionRemote extends Remote {

    void addMessage(MessageModel messageModel) throws RemoteException;
}
