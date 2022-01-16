package com.ldnhat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoCallModel implements Serializable {

    private ArrayList<ClientModel> members;
    private volatile List<ClientModel> activeMembers;
    private ClientModel sender;

    public VideoCallModel(List<ClientModel> members, ClientModel sender) {
        this.activeMembers = new ArrayList<>();

        this.activeMembers.add(sender);

        this.members = new ArrayList<>(members);
        this.sender = sender;
    }

    public ArrayList<ClientModel> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<ClientModel> members) {
        this.members = members;
    }

    public List<ClientModel> getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(List<ClientModel> activeMembers) {
        this.activeMembers = activeMembers;
    }

    public ClientModel getSender() {
        return sender;
    }

    public void setSender(ClientModel sender) {
        this.sender = sender;
    }
}
