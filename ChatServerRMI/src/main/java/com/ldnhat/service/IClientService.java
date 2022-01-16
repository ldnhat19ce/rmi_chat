package com.ldnhat.service;

import com.ldnhat.model.ClientModel;

import java.util.List;

public interface IClientService {

    ClientModel findByUsernameAndPassword(String username, String password);
    ClientModel save(ClientModel clientModel);
    ClientModel findOne(int id);
    ClientModel updateUser(ClientModel clientModel);

}
