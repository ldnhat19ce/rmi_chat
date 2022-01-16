package com.ldnhat.controller;

import com.github.sarxos.webcam.Webcam;
import com.ldnhat.model.ClientModel;
import com.ldnhat.model.VideoCallModel;
import com.ldnhat.remote.ClientRemote;
import com.ldnhat.remote.ClientRemoteImpl;
import com.ldnhat.utils.ActiveCamera;
import com.ldnhat.view.Main;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class WebCamController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label textCall;

    @FXML
    private GridPane grid;

    @FXML
    private HBox callCommand;

    @FXML
    private ImageView acceptCall;

    private ClientRemoteImpl currentClient;

    private static VideoCallModel videoCallModel;

    private static ActiveCamera myCamera;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentClient = Main.getClient();

        try {
            if (videoCallModel.getSender().equals(currentClient.getClient())){
                System.out.println("sender");
                myCamera = new ActiveCamera(grid, 0);

                callCommand.getChildren().remove(0);

                StringBuilder receivers = new StringBuilder();

                for (ClientModel clientModel : videoCallModel.getMembers()){
                    if (!clientModel.equals(videoCallModel.getSender())){
                        receivers.append(" ").append(clientModel.getName());
                    }
                }
                textCall.setText("Bạn đang gọi cho: "+receivers);


                myCamera.start();
            }else{
                textCall.setText(videoCallModel.getSender().getUsername()+" đang gọi bạn");
            }
        } catch (RemoteException | SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acceptCall() throws SocketException, UnknownHostException, RemoteException {
        myCamera = new ActiveCamera(grid, 1);

        currentClient.getServer().addActiveMember(videoCallModel.getMembers(), currentClient.getClient());
        myCamera.start();
        callCommand.getChildren().remove(0);
    }

    @FXML
    private void rejectCall() throws RemoteException {
        currentClient.getServer().removeActiveMember(videoCallModel.getMembers(),currentClient.getClient());
    }

    public static void setVideoCall(VideoCallModel videoCall){
        WebCamController.videoCallModel = videoCall;
    }

    public static ActiveCamera getMyCamera(){
        return myCamera;
    }
}
