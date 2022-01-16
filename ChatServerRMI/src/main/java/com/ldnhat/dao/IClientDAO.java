package com.ldnhat.dao;

import com.ldnhat.model.ClientModel;

import java.util.List;

public interface IClientDAO extends GenericDAO<ClientModel> {

    ClientModel findByUsernameAndPassword(String username, String password);
    Long save(ClientModel clientModel);
    ClientModel findOne(int id);
    void updateUser(ClientModel clientModel);
    List<ClientModel> findById(int id);
}
