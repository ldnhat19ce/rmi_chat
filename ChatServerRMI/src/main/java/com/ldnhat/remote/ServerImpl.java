package com.ldnhat.remote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldnhat.model.*;
import com.ldnhat.service.*;
import com.ldnhat.service.impl.*;
import com.ldnhat.util.Constant;
import com.ldnhat.util.RegexYT;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class ServerImpl extends UnicastRemoteObject implements Server {

    private final IClientService clientService;

    private final IDiscussionDetailService discussionDetailService;

    private final IMessageService messageService;

    private final IDiscussionService discussionService;

    private final IStickerDetailService stickerDetailService;

    private final HashMap<ClientRemote, ClientModel> clients;

    private final ObservableList<DiscussionImpl> onlineDiscussions;
    private boolean isCalling;

    public ServerImpl() throws RemoteException {
        isCalling = false;
        clientService = new ClientService();

        discussionDetailService = new DiscussionDetailService();
        messageService = new MessageService();
        stickerDetailService = new StickerDetailService();

        discussionService = new DiscussionService();

        clients = new HashMap<>();

        onlineDiscussions = FXCollections.observableArrayList();

        discussionListener();
    }


    @Override
    public void connectClient(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException {
        ClientModel c = clientService.findOne(clientModel.getId());

        //thông báo đến tất cả user online
        if (c != null){

            //truy cập danh sách những người online
            for (ClientRemote remote : clients.keySet()){

                //gửi user đăng ký online cho các user khác
                remote.setOnline(clientRemote, c);

                //gửi user đã online cho user vừa đăng ký
                clientRemote.setOnline(remote, clients.get(remote));
            }

            //put user đăng ký vào list
            clients.put(clientRemote, c);

            //tìm tất cả dicussion mà client này tham gia
            findAllUserDiscussion(clientRemote, clientModel);
        }
    }

    @Override
    public void disconnect(ClientRemote clientRemote) throws RemoteException {

        if (!onlineDiscussions.isEmpty()){
            onlineDiscussions.removeIf(discussion ->
                    discussion.getDiscussionDetail().getClientModel().getId() ==
                    clients.get(clientRemote).getId());
        }

        //find all user online và remove user disconnect
        for (ClientRemote c : clients.keySet()){
            c.disconnect(clientRemote);
        }

        clients.remove(clientRemote);
    }

    @Override
    public ClientModel findByUsernameAndPassword(String s, String s1) throws RemoteException {
        return clientService.findByUsernameAndPassword(s, s1);
    }

    @Override
    public ClientModel save(ClientModel clientModel)throws RemoteException {
        System.out.println(clientModel.getName());
        clientModel = clientService.save(clientModel);
        return clientService.findOne(clientModel.getId());
    }

    @Override
    public String updateProfilePicture(ClientRemote clientRemote, File file) throws RemoteException {
        System.out.println(file.getName());
        //lấy số lượng tệp trong thư mục cộng thêm 1 để đặt tên file
        int number = Objects.requireNonNull(new File(Constant.USER_PICTURE).list()).length + 1;

        //lấy tên tệp mà không có phần mở rộng
        String fileName = StringUtils.substring(file.getName(), 0, file.getName().lastIndexOf("."));

        //lấy phần mở rộng của file
        String extensionOfFile = StringUtils.substring(file.getName(),
                file.getName().lastIndexOf("."), file.getName().length());

        //gộp file
        String path = Constant.USER_PICTURE + fileName + "_"+number+extensionOfFile;
        System.out.println(path);
        //copy file lên server
        Path source = Paths.get(file.getAbsolutePath());
        Path dest = Paths.get(path);

        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (clients.containsKey(clientRemote)){
            ClientModel clientModel = clients.get(clientRemote);
            clientModel.setImage(path);

            clientService.updateUser(clientModel);

            for (ClientRemote remote : clients.keySet()){
                remote.updateUser(remote, clientModel);
            }
        }

        return path;
    }
    
    private void findAllUserDiscussion(ClientRemote clientRemote, ClientModel clientModel) throws RemoteException {

        //tìm danh sách những nhóm mà user tham gia
        if (!discussionDetailService.findByUserId(clientModel.getId()).isEmpty()){
            List<DiscussionDetail> discussionDetails =
                    discussionDetailService.findByUserId(clientModel.getId());


            for (DiscussionDetail discussionDetail : discussionDetails){

                //tìm tất cả thành viên của nhóm mà user tham gia
                List<DiscussionDetail> listGroups =
                        discussionDetailService.findByDiscussionId(discussionDetail.getDiscussion().getId());

                //thêm list client
                List<ClientModel> clientModels = new ArrayList<>();

                for (DiscussionDetail detail : listGroups){
                    ClientModel client = clientService.findOne(detail.getClientModel().getId());
                    clientModels.add(client);
                    System.out.println(client.getUsername());
                }

                //lưu danh sách nhóm
                DiscussionModel discussionModel = new DiscussionModel();

                discussionModel.setDiscussionDetails(listGroups);

                //set danh sách thành viên
                discussionModel.setMembers(clientModels);

                //set list chat
                discussionModel.setChats(messageService.findByDiscussionId(
                        discussionDetail.getDiscussion().getId()));

                DiscussionImpl discussion = new DiscussionImpl();

                discussionDetail.setDiscussionModel(discussionModel);

                discussion.setDiscussionDetail(discussionDetail);

                //observable discussionImpl
                onlineDiscussions.add(discussion);
            }
        }
    }

    @Override
    public void updateDiscussion(DiscussionDetail discussionDetail, MessageModel messageModel) throws RemoteException {
        DiscussionImpl discussion = new DiscussionImpl();
        discussion.setDiscussionDetail(discussionDetail);

        System.out.println("discussion detail id: "+discussionDetail.getId());

        //filter list onlineDiscussions xem discussionDetail có tồn tại không
        List<DiscussionImpl> filterDiscussion = onlineDiscussions.stream().filter(
                s -> s.getDiscussionDetail().getId() == discussion.getDiscussionDetail().getId()
                ).collect(Collectors.toList());

        if (!filterDiscussion.isEmpty()){
            Matcher matcher = RegexYT.youtubePattern.matcher(messageModel.getContent());
            if (matcher.matches()){
                messageModel.setType("YT");
            }
            MessageModel msgUpdate = messageService.save(messageModel);

            onlineDiscussions.forEach(d -> {
                if (d.getDiscussionDetail().getId() == discussion.getDiscussionDetail().getId()){

                    int index = onlineDiscussions.indexOf(d);
                    System.out.println("index discussion: "+index);
                    discussion.getDiscussionDetail().getDiscussionModel().getChats().add(msgUpdate);
                    onlineDiscussions.set(index, discussion);
                }
            });
        }
    }

    @Override
    public void saveDiscussion(DiscussionDetail discussionDetail) throws RemoteException {
        List<Integer> memberIds = new ArrayList<>();
        List<ClientModel> clientModels = new ArrayList<>();
        for (ClientModel clientModel : discussionDetail.getDiscussionModel().getMembers()){
            memberIds.add(clientModel.getId());
            clientModels.add(clientModel);
        }
        Discussion discussion;
        if (discussionDetailService.findDiscussionGroup(memberIds) == -1){
            discussion = discussionService.save(discussionDetail.getDiscussion());

            for (ClientModel clientModel : discussionDetail.getDiscussionModel().getMembers()){
                DiscussionDetail d = new DiscussionDetail();
                d.setClientModel(clientModel);
                d.setDiscussion(discussion);
                d.setType(Constant.DISCUSSION_GROUP);

                discussionDetailService.save(d);
            }

            for (ClientModel clientModel : discussionDetail.getDiscussionModel().getMembers()){
                DiscussionImpl discussionImpl = new DiscussionImpl();
                System.out.println("client model: "+clientModel.getId());
                discussionDetail = discussionDetailService.findByDiscussionIdAndUserId(discussion.getId(), clientModel.getId());
                discussionDetail.setClientModel(clientModel);
                DiscussionModel discussionModel = new DiscussionModel();
                discussionModel.setChats(messageService.
                        findByDiscussionId(discussionDetail.getDiscussion().getId()));

                discussionModel.setMembers(clientModels);
                discussionDetail.setDiscussionModel(discussionModel);


                discussionImpl.setDiscussionDetail(discussionDetail);

                onlineDiscussions.add(discussionImpl);
            }
        }

    }

    @Override
    public int checkDiscussionGroup(List<ClientModel> list) throws RemoteException {
        List<Integer> users = new ArrayList<>();
        for (ClientModel clientModel : list){
            users.add(clientModel.getId());
        }
        return discussionDetailService.findDiscussionGroup(users);
    }

    private void discussionListener(){
        onlineDiscussions.addListener((ListChangeListener.Change<? extends DiscussionImpl> c) ->{
            while (c.next()){
                if (c.wasReplaced()){
                    System.out.println("replace");
                    for (int i = c.getFrom(); i < c.getTo(); ++i){
                        for (ClientRemote cm : clients.keySet()){
                            for (ClientModel clientModel : onlineDiscussions.get(i)
                                    .getDiscussionDetail().getDiscussionModel().getMembers()){

                                if (clientModel.getId() == clients.get(cm).getId()){
                                    System.out.println("client model: "+clientModel.getName());
                                    try {
                                        cm.updateDiscussionDetail(onlineDiscussions.get(i).getDiscussionDetail());
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }else if (c.wasAdded()){
                    System.out.println("add");
                    for (int i = c.getFrom(); i < c.getTo(); ++i){
                        for (ClientRemote cm : clients.keySet()){

                            if (onlineDiscussions.get(i).getDiscussionDetail()
                                    .getClientModel().getId() == clients.get(cm).getId()){
                                try {
                                    cm.saveDiscussionDetail(onlineDiscussions.get(i).getDiscussionDetail());
                                    //cm.saveDiscussion(onlineDiscussions.get(i).getDiscussionModel());
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    System.out.println("discussion size: "+onlineDiscussions.size());
                }else if (c.wasRemoved()){
                    System.out.println("remove");
                    for (int j = 0; j < c.getRemovedSize(); j++){
                        for (ClientRemote cm : clients.keySet()){

                        }
                    }
                }
            }
        } );
    }

    @Override
    public void createCall(VideoCallModel videoCallModel) throws RemoteException {
        for (ClientModel clientModel : videoCallModel.getMembers()){
            for (ClientRemote remote : clients.keySet()){
                if (clients.get(remote).equals(clientModel)){
                    remote.requestCall(videoCallModel);
                    break;
                }
            }
        }
    }

    @Override
    public boolean isCalling() throws RemoteException {
        return isCalling;
    }

    @Override
    public boolean checkDiscussionPrivate(int userSender, int userReceive) throws RemoteException {

        return discussionDetailService.checkDiscussionPrivate(userSender, userReceive) &&
                (discussionDetailService.findDiscussionPrivate(userSender, userReceive) != -1);
    }

    @Override
    public DiscussionDetail findDiscussionId(int userSender, int userReceive) throws RemoteException {

        int discussionId = discussionDetailService.findDiscussionPrivate(userSender, userReceive);
        System.out.println("discussionId: "+discussionId);
        //tìm cuộc trò chuyện của 2 user
        DiscussionDetail discussionDetail =
                discussionDetailService.findByDiscussionIdAndUserId(discussionId, userSender);

        //tìm tin nhắn theo discussionId
        List<MessageModel> messageModels = messageService.findByDiscussionId(
                discussionDetail.getDiscussion().getId());

        //tìm thành viên còn lại
        List<DiscussionDetail> listGroups =
                discussionDetailService.findByDiscussionId(discussionDetail.getDiscussion().getId());

        //thêm list client
        List<ClientModel> clientModels = new ArrayList<>();

        for (DiscussionDetail detail : listGroups){
            ClientModel client = clientService.findOne(detail.getClientModel().getId());
            clientModels.add(client);
        }

        DiscussionModel discussionModel = new DiscussionModel();
        discussionModel.setChats(messageModels);
        discussionModel.setMembers(clientModels);

        discussionDetail.setDiscussionModel(discussionModel);

        return discussionDetail;
    }

    @Override
    public DiscussionDetail saveDiscussionPrivate(DiscussionDetail discussionDetail, MessageModel messageModel)
            throws RemoteException {
        System.out.println("client model size: "+discussionDetail.getDiscussionModel().getMembers().size());
        if (checkDiscussionPrivate(discussionDetail.getDiscussionModel().getMembers().get(0).getId(),
                discussionDetail.getDiscussionModel().getMembers().get(1).getId()
                )){
            DiscussionImpl discussionImpl = new DiscussionImpl();
            discussionImpl.setDiscussionDetail(discussionDetail);

            int discussionId = discussionDetailService.findDiscussionPrivate(
                    discussionDetail.getDiscussionModel().getMembers().get(0).getId(),
                    discussionDetail.getDiscussionModel().getMembers().get(1).getId()
            );

            Discussion discussion = new Discussion();
            discussion.setId(discussionId);

            messageModel.setDiscussion(discussion);

            //filter list onlineDiscussions xem discussionDetail có tồn tại không
            List<DiscussionImpl> filterDiscussion = onlineDiscussions.stream().filter(
                    s -> s.getDiscussionDetail().getId() == discussionImpl.getDiscussionDetail().getId()
            ).collect(Collectors.toList());

            if (!filterDiscussion.isEmpty()){

                MessageModel msgUpdate = messageService.save(messageModel);

                onlineDiscussions.forEach(d -> {
                    if (d.getDiscussionDetail().getId() == discussionImpl.getDiscussionDetail().getId()){

                        int index = onlineDiscussions.indexOf(d);
                        System.out.println("index discussion: "+index);
                        discussionImpl.getDiscussionDetail().getDiscussionModel().getChats().add(msgUpdate);
                        onlineDiscussions.set(index, discussionImpl);
                    }
                });
            }

        }else{
            System.out.println("private save");
            Discussion discussion = new Discussion();

            String name = discussionDetail.getDiscussionModel().getMembers().get(0).getUsername() + "_" +
                    discussionDetail.getDiscussionModel().getMembers().get(1).getUsername();

            discussion.setName(name);

            discussion = discussionService.save(discussion);

            //add user vào nhóm
            for (ClientModel clientModel : discussionDetail.getDiscussionModel().getMembers()){
                DiscussionDetail d = new DiscussionDetail();
                d.setType(Constant.DISCUSSION_PRIVATE);
                d.setClientModel(clientModel);
                d.setDiscussion(discussion);

                discussionDetailService.save(d);
            }

            messageModel.setDiscussion(discussion);
            Matcher matcher = RegexYT.youtubePattern.matcher(messageModel.getContent());
            if (matcher.matches()){
                messageModel.setType("YT");
            }
            messageService.save(messageModel);

            List<MessageModel> messageModels = messageService.findByDiscussionId(discussion.getId());

            for(MessageModel m : messageModels){
                System.out.println("message: "+m.getContent());
            }

            DiscussionModel discussionModel = new DiscussionModel();
            discussionModel.setChats(messageModels);
            discussionModel.setMembers(discussionDetail.getDiscussionModel().getMembers());


            for (ClientModel clientModel : discussionDetail.getDiscussionModel().getMembers()){
                DiscussionImpl d = new DiscussionImpl();
                discussionDetail = discussionDetailService.findByDiscussionIdAndUserId(discussion.getId(),
                        clientModel.getId()
                );
                discussionDetail.setDiscussionModel(discussionModel);
                discussionDetail.setDiscussion(discussion);
                discussionDetail.setClientModel(clientModel);
                d.setDiscussionDetail(discussionDetail);

                onlineDiscussions.add(d);
            }
            discussionDetail = discussionDetailService.findByDiscussionIdAndUserId(discussion.getId(),
                    messageModel.getClientModel().getId()
                    );
            discussionDetail.setDiscussionModel(discussionModel);
            discussionDetail.setDiscussion(discussion);

        }
        return discussionDetail;
    }

    @Override
    public void sendFile(File file, DiscussionDetail discussionDetail, MessageModel messageModel)
            throws RemoteException {
        try {

            System.out.println("file name: "+file.getName());
            /*int number = Objects.requireNonNull(new File(Constant.MESSAGE_FILE).list()).length + 1;

            String name = file.getName().substring(0, file.getName().lastIndexOf("."));

            String extension = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());

            String path = Constant.MESSAGE_FILE + name+"_"+number+extension;

            Path source = Paths.get(file.getAbsolutePath());
            Path dest = Paths.get(path);
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            messageModel.setContent(path);
            synchronized (this){
                updateDiscussion(discussionDetail, messageModel);
            }*/
            String fileName = uploadFile(file, "http://192.168.1.72:8080/ServerSaveFile_war_exploded/api/files");
            messageModel.setContent(fileName);
            synchronized (this){
                updateDiscussion(discussionDetail, messageModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String uploadFile(File file, String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // builder.addBinaryBody(file.getName(), file);
        builder.addBinaryBody("multipartFile", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        HttpResponse httpResponse = client.execute(httpPost);
        System.out.println("Status:" + httpResponse.getStatusLine().toString());
        System.out.println("fileName:");
        String fileName = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
        System.out.println(fileName);

        return fileName.replaceAll("\"", "");
    }

    @Override
    public List<StickerDetailModel> findAllStickers() throws RemoteException {

        return stickerDetailService.findByStickerId(1);
    }

    @Override
    public void sendIcon(DiscussionDetail discussionDetail, MessageModel messageModel)
            throws RemoteException {
        updateDiscussion(discussionDetail, messageModel);
    }

    @Override
    public void addActiveMember(List<ClientModel> list, ClientModel clientModel) throws RemoteException {
        for (ClientModel member : list) {

            for (ClientRemote ci : clients.keySet()) {

                if (clients.get(ci).equals(member)) {

                    ci.addActiveMember(clientModel);

                    break;
                }

            }
        }

        isCalling = true;
    }

    @Override
    public void removeActiveMember(List<ClientModel> list, ClientModel clientModel) throws RemoteException {
        for (ClientModel member : list) {

            for (ClientRemote ci : clients.keySet()) {

                if (clients.get(ci).equals(member)) {

                    ci.removeActiveMember(clientModel);

                    break;
                }

            }
        }

        isCalling = false;
    }

    @Override
    public List<File> findAllFileByUserId(int userId, String fileType) throws RemoteException {

        return findAllFileTypeByUserId(userId, fileType);
    }

    public List<File> findAllFileTypeByUserId(int userId, String fileType){

        List<MessageModel> messageModels = new ArrayList<>();

        List<DiscussionDetail> discussionDetails =
                discussionDetailService.findByUserId(userId);


        for (DiscussionDetail discussionDetail : discussionDetails) {

            messageModels.addAll(messageService
                    .findByDiscussionId(discussionDetail.getDiscussion().getId()));

        }

        List<File> files = new ArrayList<>();
        for (MessageModel messageModel : messageModels){
            if (messageModel.getType().equals(Constant.MESSAGE_FILE_TYPE)){
                File file = new File(messageModel.getContent());
                files.add(file);
            }
        }

        return files.stream().filter(s -> s.getName()
                .substring(s.getName().lastIndexOf(".")).equals(fileType))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\Admin\\Videos\\h2.mp4");
        uploadFile(file, "http://192.168.1.72:8080/ServerSaveFile_war_exploded/api/files");
    }
}
