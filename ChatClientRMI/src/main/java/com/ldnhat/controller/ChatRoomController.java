package com.ldnhat.controller;

import com.jfoenix.controls.JFXTextField;
import com.ldnhat.model.*;
import com.ldnhat.remote.ClientRemoteImpl;
import com.ldnhat.utils.*;
import com.ldnhat.view.Main;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ChatRoomController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;

    private ClientRemoteImpl currentClient;

    @FXML
    private AnchorPane root;

    @FXML
    private Label name;

    @FXML
    private Label username;

    @FXML
    private ListView<ClientModel> onlineUsers;

    @FXML
    private Circle profilePicture;

    @FXML
    private JFXTextField usersSearchBar;

    @FXML
    private ImageView go;

    @FXML
    private Label groupName;

    @FXML
    private VBox chatContainer;

    @FXML
    private JFXTextField textMessage;

    @FXML
    private JFXTextField txtDiscussionName;

    @FXML
    private ListView<DiscussionDetail> groupsList;

    @FXML
    private Pane chatPane;

    @FXML
    private VBox vboxContainer;

    @FXML
    private ListView<File> filesList;

    @FXML
    private ListView<StickerDetailModel> listSticker;

    @FXML
    private ScrollPane scrollMessage;

    @FXML
    private ImageView snipping;

    private ArrayList<ClientModel> currentUsers = new ArrayList<>();

    private BehaviorSubject<String> rxTextSearch = BehaviorSubject.create();

    private ObservableList<ClientModel> onlineClients;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<ClientModel> currentMembers = new ArrayList<>();

    private DiscussionDetail currentDiscussion;

    private ObservableList<DiscussionDetail> groups;

    private VideoCallModel videoCallModel;

    private boolean isSaveDiscussionPrivate = false;
    private boolean isSaveDiscussionGroup = false;

    private PublishSubject<Integer> rxIconClick = PublishSubject.create();

    private int iconClick = 0;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentClient = Main.getClient();

        dragScreen();

        try {
            init();
        } catch (RemoteException | FileNotFoundException e) {
            e.printStackTrace();
        }

        onlineUserListener();
        connectedUserListener();
        searchUsersListener();
        registryTextSearch();

        findAllGroups();
        registryGroups();
        groupListener();
        try {
            createListViewIcon();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        registryIconClick();
    }

    private void init() throws RemoteException, FileNotFoundException {

        currentClient.setChatPane(chatPane);
        name.setText(currentClient.getClient().getName());
        username.setText("@"+currentClient.getClient().getUsername());

        Image userImage;
        if (currentClient.getClient().getImage().equals("/assets/user_picture.png")){
            userImage = new Image(currentClient.getClient().getImage(), false);
        }else{
            userImage = new Image(new FileInputStream(currentClient.getClient().getImage()));
        }

        profilePicture.setFill(new ImagePattern(userImage));

    }

    private void registryIconClick(){
        Disposable disIconClick = rxIconClick.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> {
                    System.out.println("icon value: "+it);
                    if (it == 1){
                        listSticker.setVisible(true);

                    }else listSticker.setVisible(it != 2);
                });

        disposable.add(disIconClick);
    }

    private void createListViewIcon() throws RemoteException {
        listSticker.getItems().clear();

        listSticker.setCellFactory(listView -> new ListCell<StickerDetailModel>(){
            @Override
            protected void updateItem(StickerDetailModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null){
                    setGraphic(null);
                }else{
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);

                    //hBox.setSpacing(20);

                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(64);
                    imageView.setFitWidth(64);

                    try {
                        Image image = new Image(new FileInputStream(item.getUrl()));

                        imageView.setImage(image);

                        hBox.getChildren().addAll(imageView);

                        setGraphic(hBox);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imageView.setCursor(Cursor.HAND);
                    imageView.setOnMouseClicked(event -> {
                        MessageModel messageModel = new MessageModel();
                        messageModel.setType(Constant.MESSAGE_ICON);
                        messageModel.setContent(item.getUrl());
                        messageModel.setStatus(1);

                        ClientModel clientModel = new ClientModel();
                        try {
                            clientModel.setId(currentClient.getClient().getId());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        messageModel.setClientModel(clientModel);

                        Discussion discussion = new Discussion();
                        discussion.setId(currentDiscussion.getDiscussion().getId());
                        messageModel.setDiscussion(discussion);

                        try {
                            currentClient.getServer().sendIcon(currentDiscussion, messageModel);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

        ObservableList<StickerDetailModel> stickerDetailModels =
                FXCollections.observableArrayList(currentClient.getServer().findAllStickers());

        listSticker.setItems(stickerDetailModels);
    }

    @FXML
    private void logout() throws RemoteException {
        currentClient.getServer().disconnect(currentClient);
        compositeDisposable.clear();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void changePicture() throws RemoteException, FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpeg", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(Main.getStage());

        if (file != null){
            String path = currentClient.getServer().updateProfilePicture(currentClient, file);

            Image image = new Image(new FileInputStream(path));

            profilePicture.setFill(new ImagePattern(image));
        }
    }

    @FXML
    private void sendMessage(MouseEvent event) throws RemoteException {
        System.out.println("issave: "+isSaveDiscussionPrivate);
        if (!textMessage.getText().isEmpty()){
            if (groupsList.getSelectionModel().getSelectedItem() != null ||
                onlineUsers.getSelectionModel().getSelectedItem() != null
            ){
                MessageModel messageModel = new MessageModel();
                messageModel.setContent(textMessage.getText());
                messageModel.setType(Constant.MESSAGE_SMS);
                messageModel.setStatus(1);

                ClientModel clientModel = new ClientModel();
                clientModel.setId(currentClient.getClient().getId());
                messageModel.setClientModel(clientModel);

                if (isSaveDiscussionPrivate){
                    System.out.println("messageModel: "+messageModel.getContent());
                   currentDiscussion = currentClient.getServer().saveDiscussionPrivate(currentDiscussion, messageModel);
                    System.out.println("detail id: "+currentDiscussion.getClientModel().getId());
                    appendDiscussion(currentDiscussion);
                }else{
                    Discussion discussion = new Discussion();
                    discussion.setId(currentDiscussion.getDiscussion().getId());
                    messageModel.setDiscussion(discussion);

                    currentClient.getServer().updateDiscussion(currentDiscussion, messageModel);
                }
            }
            textMessage.setText("");
        }else if (!filesList.getItems().isEmpty()){
            if (groupsList.getSelectionModel().getSelectedItem() != null ||
                    onlineUsers.getSelectionModel().getSelectedItem() != null
            ){
                MessageModel messageModel = new MessageModel();
                messageModel.setType(Constant.MESSAGE_FILE);
                messageModel.setStatus(1);

                ClientModel clientModel = new ClientModel();
                clientModel.setId(currentClient.getClient().getId());
                messageModel.setClientModel(clientModel);

                Discussion discussion = new Discussion();
                discussion.setId(currentDiscussion.getDiscussion().getId());
                messageModel.setDiscussion(discussion);

                ObservableList<File> files = filesList.getItems();

                for (File file : files){
                    currentClient.getServer().sendFile(file, currentDiscussion, messageModel);
                }
                filesList.getItems().clear();
            }
            if (vboxContainer.getChildren().size() == 2){
                vboxContainer.getChildren().remove(1);
            }
        }
    }

    @FXML
    private void attachFile(MouseEvent e){
        if (onlineUsers.getSelectionModel().getSelectedItem() != null ||
            groupsList.getSelectionModel().getSelectedItem() != null
        ){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file");
            
            List<File> files = fileChooser.showOpenMultipleDialog(Main.getStage());

            if (!files.isEmpty()){
                fileListListener(files);
            }
        }
    }
    
    private void fileListListener(List<File> files){
        //vboxContainer.getChildren().add(filesList);

        ObservableList<File> selectedFiles = FXCollections.observableArrayList(files);

        filesList.setCellFactory(listView -> new ListCell<File>(){
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null){
                    setGraphic(null);
                }else{
                    String name = item.getName().substring(0, item.getName().lastIndexOf("."));

                    CustomFileLocal customFile = new CustomFileLocal(item, name);

                    Label label = new Label("X");
                    label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                    label.setAlignment(Pos.TOP_RIGHT);

                    label.setCursor(Cursor.HAND);

                    label.setOnMouseClicked(e -> {
                        getListView().getItems().remove(getItem());
                        if (getListView().getItems().isEmpty()){
                            vboxContainer.getChildren().remove(1);
                        }
                    });

                    customFile.getHbox().getChildren().addAll(label);
                    setGraphic(customFile.getHbox());
                }
            }
        });
        filesList.setItems(selectedFiles);
    }

    @FXML
    private void videoCall(MouseEvent event) throws RemoteException {
        if (!currentClient.getServer().isCalling()){
            videoCallModel = new VideoCallModel(currentMembers, currentClient.getClient());

            if (onlineUsers.getSelectionModel().getSelectedItem() != null ||
                groupsList.getSelectionModel().getSelectedItem() != null
            ){
                currentClient.getServer().createCall(videoCallModel);
            }
        }else{
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Thông báo");

            ButtonType buttonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.setContentText(Constant.NOTIFICATION_IS_CALLING);
            dialog.getDialogPane().getButtonTypes().add(buttonType);
            dialog.showAndWait();
        }

    }

    private void searchUsersListener(){
        usersSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            rxTextSearch.onNext(newValue);
        });
    }
    
    private void registryTextSearch(){
        Disposable disposable = rxTextSearch.observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(this::filterUsers);
        compositeDisposable.add(disposable);
    }

    private void filterUsers(CharSequence keyword){

        ObservableList<ClientModel> clientFilters = FXCollections.observableArrayList();

        if (StringUtils.isNotBlank(keyword)){
            for (ClientModel clientModel : onlineClients){
                String stringAccent = StringUtils.stripAccents(clientModel.getName());
                if (StringUtils.contains(StringUtils.toRootLowerCase(clientModel.getName()),
                        StringUtils.toRootLowerCase(keyword.toString())) ||
                        StringUtils.contains(StringUtils.toRootLowerCase(stringAccent),
                                StringUtils.toRootLowerCase(keyword.toString()))
                ){
                    clientFilters.add(clientModel);
                }
            }
        }else{
            clientFilters.setAll(onlineClients);
        }
        Platform.runLater(() -> onlineUsers.setItems(clientFilters));
    }

    private void handleConnectedUser(){
        onlineClients = Main.getClient().getUsers();

        onlineUsers.setCellFactory(listView -> new ListCell<ClientModel>(){

            @Override
            protected void updateItem(ClientModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null){
                    setGraphic(null);
                }else{
                   try {
                       HBox hBox = new HBox();
                       hBox.setAlignment(Pos.CENTER_LEFT);

                       hBox.setSpacing(20);

                       Circle circle = new Circle(20);

                       Image image;

                       if (item.getImage().equals("/assets/user_picture.png")){
                           image = new Image(item.getImage(), false);
                       }else{

                           image = new Image(new FileInputStream(item.getImage()));

                       }
                       circle.setFill(new ImagePattern(image));

                       hBox.getChildren().addAll(circle, new Label(item.getName()));

                       setGraphic(hBox);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                }
            }
        });

        onlineUsers.setItems(onlineClients);
    }



    private void connectedUserListener(){
        Main.getClient().getUsers().addListener((ListChangeListener.Change<? extends ClientModel> clientModel) -> {
            handleConnectedUser();
        });
    }

    private void onlineUserListener(){
        onlineUsers.setOnMouseClicked(event -> {
            try {
                currentMembers.clear();
                currentMembers.add(currentClient.getClient());
                if((onlineUsers.getSelectionModel().getSelectedItems().size()+1) > 2){

                    isSaveDiscussionPrivate = false;
                    currentMembers.add(onlineUsers.getSelectionModel().getSelectedItem());

                    currentDiscussion = new DiscussionDetail();

                    DiscussionModel discussionModel = new DiscussionModel();
                    discussionModel.setMembers(currentMembers);

                    System.out.println("member size: "+currentMembers.size());

                    if (currentClient.getServer().checkDiscussionGroup(currentMembers) != -1){
                        isSaveDiscussionGroup = false;
                        Discussion discussion = new Discussion();
                        discussion.setId(currentClient.getServer().checkDiscussionGroup(currentMembers));
                        DiscussionDetail discussionDetail = new DiscussionDetail();
                        discussionDetail.setDiscussion(discussion);

                        if (currentClient.getDiscussions().contains(discussionDetail)){
                            currentDiscussion = currentClient.getDiscussions()
                                    .get(currentClient.getDiscussions().indexOf(discussionDetail));
                            System.out.println("discussion id: "+currentDiscussion.getDiscussion().getId());

                            currentMembers.addAll(currentDiscussion.getDiscussionModel().getMembers());

                            appendDiscussion(currentDiscussion);
                        }
                    }else{
                        txtDiscussionName.setVisible(true);
                        isSaveDiscussionGroup = true;
                        System.out.println("group does not exis");
                    }

                }else{
                    isSaveDiscussionPrivate = false;
                    isSaveDiscussionGroup = false;
                    int user1 = currentClient.getClient().getId();
                    int user2 = onlineUsers.getSelectionModel().getSelectedItem().getId();

                    if (currentClient.getServer().checkDiscussionPrivate(user1, user2)){

                        DiscussionDetail discussionDetail = currentClient.getServer().findDiscussionId(user1, user2);

                        currentDiscussion = new DiscussionDetail();
                        currentDiscussion.setDiscussion(
                                discussionDetail.getDiscussion());
                        currentDiscussion.setId(discussionDetail.getId());
                        currentDiscussion.setDiscussionModel(discussionDetail.getDiscussionModel());

                        if (currentClient.getDiscussions().contains(currentDiscussion)){
                            currentDiscussion = currentClient.getDiscussions().get(currentClient.getDiscussions().indexOf(currentDiscussion));

                            currentMembers.addAll(currentDiscussion.getDiscussionModel().getMembers());
                            //System.out.println("equal");
                            appendDiscussion(currentDiscussion);
                        }

                    }else{
                        currentMembers.add(onlineUsers.getSelectionModel().getSelectedItem());

                        currentDiscussion = new DiscussionDetail();

                        currentDiscussion.setClientModel(currentClient.getClient());

                        DiscussionModel discussionModel = new DiscussionModel();
                        discussionModel.setMembers(currentMembers);

                        currentDiscussion.setDiscussionModel(discussionModel);

                        isSaveDiscussionPrivate = true;
                        isSaveDiscussionGroup = false;

                        chatContainer.getChildren().removeAll(chatContainer.getChildren());

                        groupName.setText(onlineUsers.getSelectionModel().getSelectedItem().getUsername());

                    }
                }
            } catch (RemoteException | NullPointerException e) {
                System.out.println("unknown user");
            }
        });
    }

    @FXML
    private void createGroup(MouseEvent event) {
        onlineUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        go.setVisible(true);
    }

    @FXML
    private void groupSelected(MouseEvent event) throws RemoteException {
        if ((onlineUsers.getSelectionModel().getSelectedItems().size() + 1) > 2){

            DiscussionDetail discussionDetail = new DiscussionDetail();

            List<ClientModel> members = new ArrayList<>();
            members.add(currentClient.getClient());
            members.addAll(onlineUsers.getSelectionModel().getSelectedItems());


            Discussion discussion = new Discussion();
            discussion.setName(txtDiscussionName.getText());

            DiscussionModel discussionModel = new DiscussionModel();
            discussionModel.setMembers(members);

            ClientModel clientModel = new ClientModel();
            clientModel.setId(currentClient.getClient().getId());

            discussionDetail.setDiscussion(discussion);
            discussionDetail.setClientModel(clientModel);
            discussionDetail.setDiscussionModel(discussionModel);

            currentClient.getServer().saveDiscussion(discussionDetail);

            members.clear();
            txtDiscussionName.setText("");
            txtDiscussionName.setVisible(false);
            go.setVisible(false);
            currentMembers.clear();
            onlineUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            onlineUsers.getSelectionModel().clearSelection();
        }
    }

    private void registryGroups(){
        ObservableList<DiscussionDetail> discussionDetails = Main.getClient().getDiscussions();

        discussionDetails.addListener((ListChangeListener.Change<? extends DiscussionDetail> d) -> {
            while (d.next()){
                try {
                    if (d.wasReplaced()){
                        for (int i = d.getFrom(); i < d.getTo(); ++i){
                            System.out.println("replace");
                            //set lại giá trị cho discussion
                            groups.set(groups.indexOf(discussionDetails.get(i)), discussionDetails.get(i));

                            //nếu current discussion hiện tại == discussion
                            //cập nhật lại giá trị
                            if (currentDiscussion.equals(discussionDetails.get(i))){
                                currentDiscussion = discussionDetails.get(i);
                                appendDiscussion(currentDiscussion);
                            }
                        }
                    }else if (d.wasAdded()){
                        for (int i = d.getFrom(); i < d.getTo(); ++i){
                            System.out.println("add");
                            groups.add(discussionDetails.get(i));
                        }
                    }
                }catch (Exception e){
                    System.out.println("unknown replace");
                }

            }
        });
    }

    private void findAllGroups(){
       ObservableList<DiscussionDetail> discussionDetails = Main.getClient().getDiscussions();

       groups = FXCollections.observableArrayList(discussionDetails);

       for (DiscussionDetail discussionDetail : discussionDetails){
           System.out.println("client: "+discussionDetail.getDiscussion().getName());
       }

        groupsList.setCellFactory(listView -> new ListCell<DiscussionDetail>(){
            @Override
            protected void updateItem(DiscussionDetail item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null){
                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER_LEFT);
                    vBox.setSpacing(10);

                    Label label = new Label();
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    label.setText(item.getDiscussion().getName());
                    label.setCursor(Cursor.HAND);

                    label.setStyle("-fx-font-size: 14px;" + "-fx-font-weight: bold;");
                    label.setAlignment(Pos.CENTER);

                    vBox.getChildren().add(label);
                    vBox.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(vBox);
                }else{
                    setGraphic(null);
                }
            }
        });

        groupsList.setItems(groups);
    }

    private void groupListener(){
        groupsList.setOnMouseClicked(event -> {
            if (groupsList.getSelectionModel().getSelectedItem() != null){
                currentDiscussion = new DiscussionDetail();

                currentDiscussion.setDiscussion(
                        groupsList.getSelectionModel().getSelectedItem().getDiscussion());
                currentDiscussion.setId(groupsList.getSelectionModel().getSelectedItem().getId());
                currentDiscussion.setDiscussionModel(groupsList.getSelectionModel().getSelectedItem()
                .getDiscussionModel());

                for (ClientModel clientModel : currentDiscussion.getDiscussionModel().getMembers()){
                    System.out.println("choose: "+clientModel.getName());
                }

                if (currentClient.getDiscussions().contains(currentDiscussion)){
                    currentDiscussion = currentClient.getDiscussions().get(currentClient.getDiscussions().indexOf(currentDiscussion));

                    currentMembers.clear();
                    currentMembers.addAll(currentDiscussion.getDiscussionModel().getMembers());
                    //System.out.println("equal");
                    appendDiscussion(currentDiscussion);
                }
            }
        });
    }

    private void appendDiscussion(DiscussionDetail discussionDetail){
        Platform.runLater(() -> {
            try {
                chatContainer.getChildren().removeAll(chatContainer.getChildren());

                groupName.setText(discussionDetail.getDiscussion().getName());

//                for (ClientModel clientModel : discussionDetail.getDiscussionModel().getMembers()){
//                    System.out.println("append: "+clientModel.getUsername());
//                }

                for (MessageModel messageModel : discussionDetail.getDiscussionModel().getChats()){
                    Label username = new Label(messageModel.getClientModel().getName());
                    username.setTextAlignment(TextAlignment.RIGHT);
                    username.setTextFill(Paint.valueOf("gray"));

                    Circle circle = new Circle(20);

                    Image image;
                    if (messageModel.getClientModel().getImage().equals("/assets/user_picture.png")){
                        image = new Image(messageModel.getClientModel().getImage(), false);
                    }else{
                        image = new Image(new FileInputStream(messageModel.getClientModel().getImage()));

                    }
                    circle.setFill(new ImagePattern(image));
                    switch (messageModel.getType()) {
                        case Constant.MESSAGE_SMS:
                            printText(messageModel, circle, username);
                            break;
                        case Constant.MESSAGE_FILE:
                            printFiles(messageModel, circle, username);
                            break;
                        case Constant.MESSAGE_ICON:
                            printIcon(messageModel, circle, username);
                            break;
                        case Constant.MESSAGE_YT:
                            printYT(messageModel, circle, username);
                            break;
                    }
                    vboxContainer.heightProperty().addListener(observable -> scrollMessage.setVvalue(1D));
                }
            } catch (FileNotFoundException | RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    private void printText(MessageModel messageModel, Circle circle, Label username) throws RemoteException {
        HBox hBox = new HBox();
        VBox vBox = new VBox();

        Label content = new Label(messageModel.getContent());
        content.setPadding(new Insets(5, 10, 5, 10));
        content.setTextAlignment(TextAlignment.LEFT);

        vBox.getChildren().addAll(username, content);

        content.setWrapText(true);
        content.setMinHeight(Region.USE_PREF_SIZE);
        content.setMinWidth(0);
        content.setMaxWidth(200);

        if (messageModel.getClientModel().getName().equals(currentClient.getClient().getName())){
            hBox.setAlignment(Pos.BOTTOM_RIGHT);
            content.setStyle("-fx-background-radius :20;-fx-background-color : #00838e;-fx-text-fill:white");
            hBox.getChildren().addAll(vBox, circle);
        }else{
            hBox.setAlignment(Pos.BOTTOM_LEFT);
            content.setStyle("-fx-background-radius :20;-fx-background-color : #9e9e9e;-fx-text-fill:white");
            hBox.getChildren().addAll(circle, vBox);
        }

        hBox.setPadding(new Insets(10, 20, 10, 10));
        hBox.setSpacing(10);

        chatContainer.getChildren().add(hBox);
    }

    private void printFiles(MessageModel messageModel, Circle circle, Label username) throws RemoteException {
        //File file = new File(messageModel.getContent());

        String fileName = messageModel.getContent();
       // String fileName = file.getName().substring(0, file.getName().lastIndexOf("_"));

        CustomFile customFile = new CustomFile(fileName, fileName);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(username, customFile.getVbox());
        customFile.getHbox().getChildren().removeAll();

        String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length());

        System.out.println("extension: "+extension);
        switch (extension) {
            case Constant.MP3:

                customFile.getVbox().setOnMouseClicked(event -> {
                    Stage stage = new Stage();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlaySound.fxml"));
                        Parent root = fxmlLoader.load();

                        Scene scene = new Scene(root);
                        stage.setScene(scene);

                        stage.setOnCloseRequest(event1 -> {
                            stage.close();
                        });
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                break;
            case Constant.WAV:
                customFile.getVbox().setOnMouseClicked(event -> {
                    Stage stage = new Stage();
                    try {
                        DataRecord.recordName = fileName;
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlayRecord.fxml"));
                        Parent root = fxmlLoader.load();

                        Scene scene = new Scene(root);
                        stage.setScene(scene);

                        stage.setOnCloseRequest(event1 -> stage.close());
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                break;
            case Constant.MP4:
                customFile.getVbox().setOnMouseClicked(event -> {
                    Stage stage = new Stage();
                    try {
                        DataVideo.videoName = fileName;
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlayVideo.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);

                        stage.setOnCloseRequest(event1 -> {
                            // vvTODO Auto-generated method stub
                            stage.close();

                        });
                        stage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
                customFile.getVbox().setOnMouseClicked(event -> {
                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Choose File");
                fileChooser.setInitialFileName(fileName);

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("(*" + extension + ")",
                        "*"+extension
                );
                fileChooser.getExtensionFilters().add(filter);

                File f = fileChooser.showSaveDialog(Main.getStage());
                    try {
                        URL url = new URL(Constant.serverFile+fileName);

                        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

                        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                        fileOutputStream.getChannel()
                                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                        File file = new File(fileName);
                        if (f != null){
                            Path source = Paths.get(file.getAbsolutePath());
                            Path dest = Paths.get(f.getAbsolutePath());

                            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


                break;
        }

        customFile.setTextColor("white");

        if (messageModel.getClientModel().getName().equals(currentClient.getClient().getName())){
            customFile.getHbox().setAlignment(Pos.CENTER_RIGHT);
            customFile.getHbox().getChildren().addAll(vBox, circle);
            customFile.getVbox().setStyle("-fx-background-radius :10;"
                    + "-fx-background-color : #00838e;");
        }else{
            customFile.getHbox().getChildren().addAll(circle, vBox);
            customFile.getHbox().setAlignment(Pos.CENTER_LEFT);
            customFile.getVbox().setStyle("-fx-background-color : #9e9e9e;" + "-fx-background-radius :10;");
        }

        customFile.getHbox().setPadding(new Insets(10, 20, 10, 10));

        customFile.getVbox().setMaxHeight(100);
        customFile.getVbox().setMinHeight(100);

        customFile.getVbox().setMaxWidth(80);
        customFile.getVbox().setMinWidth(80);

        chatContainer.getChildren().add(customFile.getHbox());
    }

    private void printIcon(MessageModel messageModel, Circle circle, Label username) throws RemoteException, FileNotFoundException {

        HBox hBox = new HBox();
        VBox vBox = new VBox();

        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setMinWidth(60);
        vBox.setMaxWidth(60);


        ImageView imageView = new ImageView(new Image(new FileInputStream(messageModel.getContent())));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setCursor(Cursor.HAND);

        vBox.getChildren().addAll(username, imageView);

        if (messageModel.getClientModel().getName().equals(currentClient.getClient().getName())){
            hBox.setAlignment(Pos.BOTTOM_RIGHT);

            hBox.getChildren().addAll(vBox, circle);
        }else{
            hBox.setAlignment(Pos.BOTTOM_LEFT);

            hBox.getChildren().addAll(circle, vBox);
        }

        hBox.setPadding(new Insets(10, 20, 10, 10));
        hBox.setSpacing(10);

        chatContainer.getChildren().add(hBox);

    }

    private void printYT(MessageModel messageModel, Circle circle, Label username) throws RemoteException {
        HBox hBox = new HBox();
        VBox vBox = new VBox();

        Label content = new Label(messageModel.getContent());
        content.setPadding(new Insets(5, 10, 5, 10));
        content.setTextAlignment(TextAlignment.LEFT);

        vBox.getChildren().addAll(username, content);

        content.setWrapText(true);
        content.setMinHeight(Region.USE_PREF_SIZE);
        content.setMinWidth(0);
        content.setMaxWidth(200);

        if (messageModel.getClientModel().getName().equals(currentClient.getClient().getName())){
            hBox.setAlignment(Pos.BOTTOM_RIGHT);
            content.setStyle("-fx-background-radius :20;-fx-background-color : #0d30da;-fx-text-fill:white");
            hBox.getChildren().addAll(vBox, circle);
        }else{
            hBox.setAlignment(Pos.BOTTOM_LEFT);
            content.setStyle("-fx-background-radius :20;-fx-background-color : #0d30da;-fx-text-fill:white");
            hBox.getChildren().addAll(circle, vBox);
        }

        hBox.setCursor(Cursor.HAND);
        hBox.setPadding(new Insets(10, 20, 10, 10));
        hBox.setSpacing(10);

        hBox.setOnMouseClicked(event -> {
            Stage stage = new Stage();
            String replaceYT = messageModel.getContent()
                    .replace("watch?v=", "embed/");
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlayYT.fxml"));

            try {

                WebView webView = new WebView();

                webView.getEngine().load(replaceYT);
                System.out.println("data yt: "+replaceYT);
                VBox v = new VBox(webView);
                //Parent root = fxmlLoader.load();

                Scene scene = new Scene(v,640, 400);
                stage.setScene(scene);

                stage.setOnCloseRequest(event1 -> {
                    // vvTODO Auto-generated method stub
                    stage.close();

                });
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**/

        });

        chatContainer.getChildren().add(hBox);

    }

    @FXML
    private void attachIcon(MouseEvent event){
        if (iconClick > 2){
            iconClick = 0;
            iconClick++;
            rxIconClick.onNext(1);
        }else{
            iconClick++;
            rxIconClick.onNext(iconClick);
        }
    }

    @FXML
    private void snippingScreen(MouseEvent event){

    }

    @FXML
    private void audioRecorder(MouseEvent event) {
        if (onlineUsers.getSelectionModel().getSelectedItem() != null ||
                groupsList.getSelectionModel().getSelectedItem() != null
        ){
            SoundRecorder soundRecorder = new SoundRecorder();
            Thread thread = new Thread(soundRecorder);
            thread.start();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ghi âm");
            alert.setHeaderText("Đang ghi âm!");

            Button show = new Button("Kết thúc");
            show.setOnAction(evt -> {

            });

            alert.setGraphic(show);

            alert.getButtonTypes().removeAll();

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK){
                soundRecorder.finish();
                soundRecorder.cancel();
                List<File> files = Collections.singletonList(soundRecorder.getWavFile());
                fileListListener(files);
            }else if (option.get() == ButtonType.CANCEL){

            }
        }

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
