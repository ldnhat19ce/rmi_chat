package com.ldnhat.controller;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
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
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private ClientRemoteImpl currentClient;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private AnchorPane root;

    @FXML
    private Pane contentPane;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton loginButton;

    @FXML
    private Label loginLog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private PublishSubject<String> rxUsername = PublishSubject.create();

    private PublishSubject<String> rxPassword = PublishSubject.create();

    private PublishSubject<Boolean> rxLoginButton = PublishSubject.create();

    private String _username = null;
    private String _password = null;

    private PublishSubject<String> rxLoginLog = PublishSubject.create();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        dragScreen();
        currentClient = Main.getClient();

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            rxUsername.onNext(newValue);
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            rxPassword.onNext(newValue);
        });

        handleTextField();
        handleButtonLogin();
        validate();
        zip();
        handleLoginlog();
        rxLoginButton.onNext(false);
    }

    @FXML
    private void login(MouseEvent event) throws IOException {
        if (StringUtils.isNoneEmpty(_username, _password)){

            //kiểm tra client có tồn tại hay không
            ClientModel clientModel = currentClient.getServer().findByUsernameAndPassword(_username, _password);
            currentClient.setClientModel(clientModel);
            if (clientModel != null){

                //thông báo kết nối tới server
                currentClient.getServer().connectClient(currentClient, currentClient.getClient());

                Parent root = FXMLLoader.load(getClass().getResource("/fxml/ChatRoom.fxml"));
                new FadeOutUp(root);
                Main.getStage().setScene(new Scene(root));
            }else{
                String log = "tài khoản không tồn tại hoặc mật khẩu không chính xác";
                loginLog.setVisible(true);
                rxLoginLog.onNext(log);
            }
        }
    }

    @FXML
    private void openRegistration(MouseEvent event) {
        try {
            Parent register = FXMLLoader.load(getClass().getResource("/fxml/Registration.fxml"));

            //tao animation
            new FadeInRight(register).play();
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(register);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    private void close(MouseEvent event) {
        compositeDisposable.clear();
        System.exit(0);
    }

    private void zip(){
        Disposable zipDisposable = Observable.combineLatest(rxUsername, rxPassword, (s, s2) ->{
            if (StringUtils.isBlank(s) || StringUtils.isBlank(s2)){
                System.out.println("is blank");
                return "NONE_VALUE";
            }
            return "VALUE";
        }).subscribe(it -> rxLoginButton.onNext(!it.equals("NONE_VALUE")),
                error -> System.out.println(error.getMessage()));

        compositeDisposable.add(zipDisposable);
    }

    private void handleTextField(){
        Disposable disposableUsername = rxUsername.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> _username = it);

        Disposable disposablePassword = rxPassword.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> _password = it);

        compositeDisposable.addAll(disposableUsername, disposablePassword);
    }

    private void handleButtonLogin(){
        Disposable disposableLogin = rxLoginButton.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> loginButton.setDisable(!it));
        compositeDisposable.add(disposableLogin);
    }

    private void handleLoginlog(){
        Disposable disposableLoginLog = rxLoginLog.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> Platform.runLater(() -> {
                    System.out.println(it);
                    loginLog.setText(it);
                }));

        compositeDisposable.add(disposableLoginLog);
    }

    private void validate(){
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("không bỏ trống tài khoản");

        username.getValidators().add(validator);

        //oldvalue: isfocus: false
        //newValue: isfocus: true
        username.focusedProperty().addListener((o, oldValue, newValue) -> {

            if (!newValue){
                username.validate();
            }else{
                username.resetValidation();
            }
        });

        RequiredFieldValidator validPassword = new RequiredFieldValidator();
        validPassword.setMessage("không bỏ trống mật khẩu");
        password.getValidators().add(validPassword);

        password.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue){
                password.validate();
            }else{
                password.resetValidation();
            }
        });
    }

    private void dragScreen(){
        root.setOnMousePressed((MouseEvent event) -> {

            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

        });

        // drag the screen depending ont the X and Y position
        root.setOnMouseDragged((MouseEvent event) -> {

            Main.getStage().setX(event.getScreenX() - xOffset);
            Main.getStage().setY(event.getScreenY() - yOffset);

        });
    }
}
