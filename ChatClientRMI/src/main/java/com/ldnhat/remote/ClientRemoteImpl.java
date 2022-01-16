package com.ldnhat.remote;

import animatefx.animation.FadeInRight;
import com.ldnhat.controller.ChatRoomController;
import com.ldnhat.controller.WebCamController;
import com.ldnhat.model.ClientModel;
import com.ldnhat.model.DiscussionDetail;
import com.ldnhat.model.DiscussionModel;
import com.ldnhat.model.VideoCallModel;
import com.ldnhat.utils.Constant;
import com.ldnhat.view.Main;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ClientRemoteImpl extends UnicastRemoteObject implements ClientRemote {

    private ClientModel clientModel;

    private Server server;

    private static HashMap<ClientRemote, ClientModel> userConnected;

    private ObservableList<ClientModel> users;

    private ObservableList<DiscussionDetail> discussions;

    private Pane chatPane;
    private VideoCallModel videoCallModel;



    public ClientRemoteImpl() throws RemoteException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry("localhost", Constant.SERVER_PORT);

        server = (Server) registry.lookup("chat");

        userConnected = new HashMap<>();

        discussions = FXCollections.observableArrayList();

        users = FXCollections.observableArrayList();

    }



    @Override
    public ClientModel getClient() throws RemoteException {
        return clientModel;
    }

    @Override
    public void UpdateDiscussion() throws RemoteException {

    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public void setOnline(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException {
        userConnected.put(clientRemote, clientModel);

        Platform.runLater(() -> users.add(clientModel));

        for (ClientModel c : userConnected.values()){
            System.out.println("connected: "+c.getId());
        }
    }

    @Override
    public void disconnect(ClientRemote clientRemote) throws RemoteException {
        ClientModel cl = userConnected.remove(clientRemote);

        Platform.runLater(() -> users.remove(cl));
    }

    public ObservableList<ClientModel> getUsers() {
        return users;
    }

    public void setUsers(ObservableList<ClientModel> users) {
        this.users = users;
    }

    @Override
    public void updateUser(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException {
        userConnected.put(clientRemote, clientModel);

        System.out.println(clientModel.getImage());

        if (users.contains(clientModel)){
            Platform.runLater(() -> users.set(users.indexOf(clientModel), clientModel));
        }
    }

    public ObservableList<DiscussionDetail> getDiscussions() {
        return discussions;
    }

    public void setDiscussions(ObservableList<DiscussionDetail> discussions) {
        this.discussions = discussions;
    }

    @Override
    public void saveDiscussionDetail(DiscussionDetail discussionDetail) throws RemoteException {
        System.out.println("save discussion detail: "+discussionDetail.getDiscussion().getName());
        Platform.runLater(() -> this.discussions.add(discussionDetail));
    }

    @Override
    public void updateDiscussionDetail(DiscussionDetail discussionDetail) throws RemoteException {
        System.out.println("update discussion");

        for (DiscussionDetail discussionDetail1 : discussions){
            System.out.println("diss: "+discussionDetail1.getId());
        }
        System.out.println("discussion id: "+discussionDetail.getId());
        if (discussions.contains(discussionDetail)){
            System.out.println("position: "+this.discussions.indexOf(discussionDetail));
            int index = this.discussions.indexOf(discussionDetail);
            Platform.runLater(() -> this.discussions.set(index, discussionDetail));
        }

    }

    @Override
    public void deleteDiscussionDetail(DiscussionDetail discussionDetail) throws RemoteException {

    }

    @Override
    public void requestCall(VideoCallModel videoCallModel) throws RemoteException {
        Platform.runLater(() -> {
            try{
                WebCamController.setVideoCall(videoCallModel);
                this.videoCallModel = videoCallModel;

                Parent webcam = FXMLLoader.load(getClass().getResource("/fxml/webCam.fxml"));
                new FadeInRight(webcam).play();

                chatPane.getChildren().removeAll();
                chatPane.getChildren().setAll(webcam);

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void setChatPane(Pane chatPane) {
        this.chatPane = chatPane;
    }

    @Override
    public void addActiveMember(ClientModel clientModel) throws RemoteException {
        videoCallModel.getActiveMembers().add(clientModel);
        WebCamController.setVideoCall(videoCallModel);

        if (videoCallModel.getActiveMembers().size() > 1){
            WebCamController.getMyCamera().setReceiveActive(true);
        }
    }

    @Override
    public void removeActiveMember(ClientModel clientModel) throws RemoteException {
        if (videoCallModel.getActiveMembers().contains(clientModel)){
            videoCallModel.getActiveMembers().remove(clientModel);
        }
        WebCamController.setVideoCall(videoCallModel);
        if (videoCallModel.getActiveMembers().size() <= 1){
            WebCamController.getMyCamera().setReceiveActive(false);
            WebCamController.getMyCamera().setActive(false);

            if (WebCamController.getMyCamera() != null){
                WebCamController.getMyCamera().close();
            }
            videoCallModel = null;
            Platform.runLater(() -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ChatRoom.fxml"));
                    Main.getStage().getScene().setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }


}
