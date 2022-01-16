package com.ldnhat.service.impl;

import com.ldnhat.dao.IClientDAO;
import com.ldnhat.dao.impl.ClientDAO;
import com.ldnhat.model.ClientModel;
import com.ldnhat.service.IClientService;

public class ClientService implements IClientService {

    private IClientDAO clientDAO;

    public ClientService() {
        clientDAO = new ClientDAO();
    }

    @Override
    public ClientModel findByUsernameAndPassword(String username, String password) {
        return clientDAO.findByUsernameAndPassword(username, password);
    }

    @Override
    public ClientModel save(ClientModel clientModel) {

        Long id = clientDAO.save(clientModel);

        return findOne(Math.toIntExact(id));
    }

    @Override
    public ClientModel findOne(int id) {
        return clientDAO.findOne(id);
    }

    @Override
    public ClientModel updateUser(ClientModel clientModel) {

        clientDAO.updateUser(clientModel);

        return clientDAO.findOne(clientModel.getId());
    }
}
