package com.ldnhat.controller;

import animatefx.animation.FadeOutUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.ldnhat.model.ClientModel;
import com.ldnhat.remote.ClientRemoteImpl;
import com.ldnhat.view.Main;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    private ClientRemoteImpl currentClient;

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private ImageView backToLogin;

    @FXML
    private JFXButton registerButton;

    private PublishSubject<String> rxName = PublishSubject.create();
    private PublishSubject<String> rxUsername = PublishSubject.create();
    private PublishSubject<String> rxPassword = PublishSubject.create();

    private PublishSubject<Boolean> rxRegisterButton = PublishSubject.create();

    private CompositeDisposable disposable = new CompositeDisposable();

    private String _name;
    private String _username;
    private String _password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        currentClient = Main.getClient();

        listenerTextField();
        validate();
        handleTextField();
        zip();
        handleButtonRegister();
        rxRegisterButton.onNext(false);
    }

    @FXML
    private void backToLogin(MouseEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

            Main.getStage().getScene().setRoot(root);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @FXML
    private void register(MouseEvent event) throws IOException {
        if (StringUtils.isNoneEmpty(_name, _username, _password)){
            ClientModel clientModel = new ClientModel();

            clientModel.setName(_name);
            clientModel.setUsername(_username);
            clientModel.setPassword(_password);
            clientModel.setImage("/assets/user_picture.png");

            ClientModel clientResponse = currentClient.getServer().save(clientModel);
            currentClient.setClientModel(clientResponse);
            if (clientResponse != null){

                //thông báo kết nối tới server
                currentClient.getServer().connectClient(currentClient, currentClient.getClient());

                Parent root = FXMLLoader.load(getClass().getResource("/fxml/ChatRoom.fxml"));
                new FadeOutUp(root);
                Main.getStage().setScene(new Scene(root));
            }
            assert clientResponse != null;
            System.out.println(clientResponse.getUsername());
        }
    }

    private void validate(){
        RequiredFieldValidator validName = new RequiredFieldValidator();
        validName.setMessage("không được bỏ trống tên!");
        name.getValidators().add(validName);
        name.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue){
                name.validate();
            }
        }));

        RequiredFieldValidator validUsername = new RequiredFieldValidator();
        validUsername.setMessage("không được bỏ trống tài khoản!");
        username.getValidators().add(validUsername);
        username.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue){
                username.validate();
            }
        }));

        RequiredFieldValidator validPassword = new RequiredFieldValidator();
        validPassword.setMessage("không được bỏ trống mật khẩu!");
        password.getValidators().add(validPassword);
        password.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue){
                password.validate();
            }
        }));
    }

    private void zip(){
        Disposable zipDisposable = Observable.combineLatest(rxName, rxUsername, rxPassword,
                (name, username, password) ->{
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password) ||
                StringUtils.isBlank(name)
            ){
                System.out.println("is blank");
                return "NONE_VALUE";
            }
            return "VALUE";
        }).subscribe(it -> rxRegisterButton.onNext(!it.equals("NONE_VALUE")),
                error -> System.out.println(error.getMessage()));

        disposable.add(zipDisposable);
    }

    private void listenerTextField(){
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            rxUsername.onNext(newValue);
        });
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            rxName.onNext(newValue);
        });
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            rxPassword.onNext(newValue);
        });
    }

    private void handleTextField(){
        Disposable disposableName = rxName.observeOn(Schedulers.computation())
                            .subscribeOn(Schedulers.io())
                .subscribe(it -> _name = it);
        Disposable disposableUsername = rxUsername.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> _username = it);
        Disposable disposablePassword = rxPassword.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> _password = it);

        disposable.addAll(disposableName, disposableUsername, disposablePassword);
    }

    private void handleButtonRegister(){
        Disposable disposableLogin = rxRegisterButton.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it ->{
                            System.out.println(it);
                            registerButton.setDisable(!it);
                        });
        disposable.add(disposableLogin);
    }


    @FXML
    private void closeApp(MouseEvent event) {
        disposable.clear();
        Platform.exit();
        System.exit(0);
    }
}
