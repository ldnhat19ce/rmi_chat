package com.ldnhat.controller;

import com.jfoenix.controls.JFXButton;
import com.ldnhat.remote.ClientRemoteImpl;
import com.ldnhat.utils.SoundRecorder;
import com.ldnhat.view.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class RecordController implements Initializable {

    @FXML
    private JFXButton btnStartRecord;

    @FXML
    private JFXButton btnStopRecord;

    private SoundRecorder soundRecorder;

    private ClientRemoteImpl currentClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentClient = Main.getClient();
    }

    @FXML
    private void startRecord(MouseEvent event){

        soundRecorder = new SoundRecorder();
        Thread thread = new Thread(soundRecorder);
        thread.start();
        btnStopRecord.setVisible(true);
    }

    @FXML
    private void stopRecord(MouseEvent event) throws RemoteException {
        soundRecorder.finish();
        soundRecorder.cancel();

        btnStopRecord.setVisible(false);
    }
}
