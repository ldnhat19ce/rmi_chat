package com.ldnhat.dao.impl;

import com.ldnhat.dao.IClientDAO;
import com.ldnhat.mapper.impl.ClientMapper;
import com.ldnhat.model.ClientModel;

import java.util.List;

public class ClientDAO extends AbstractDAO<ClientModel> implements IClientDAO {

    @Override
    public ClientModel findByUsernameAndPassword(String username, String password) {

        StringBuilder sql = new StringBuilder("SELECT * FROM user ");
        sql.append("WHERE username = ? AND password = ?");

        Object[] params = {username, password};

        List<ClientModel> clientModels = query(sql.toString(), new ClientMapper(), params);
        return clientModels.isEmpty() ? null : clientModels.get(0);
    }

    @Override
    public Long save(ClientModel clientModel) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("user(name, username, password, image) ");
        sql.append("VALUES(?,?,?,?)");

        Object[] params = {clientModel.getName(), clientModel.getUsername(), clientModel.getPassword(),
            clientModel.getImage()
        };

        return insert(sql.toString(), params);
    }

    @Override
    public ClientModel findOne(int id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user ");
        sql.append("WHERE user_id = ?");

        Object[] params = {id};

        List<ClientModel> clientModels = query(sql.toString(), new ClientMapper(), params);
        return clientModels.isEmpty() ? null : clientModels.get(0);
    }

    @Override
    public void updateUser(ClientModel clientModel) {
        StringBuilder sql = new StringBuilder("UPDATE user SET name = ?, ");
        sql.append("username = ?, password = ?, image = ? ");
        sql.append("WHERE user_id = ?");

        Object[] params = {clientModel.getName(), clientModel.getUsername(),
                clientModel.getPassword(), clientModel.getImage(), clientModel.getId()
        };

        update(sql.toString(), params);
    }

    @Override
    public List<ClientModel> findById(int id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user ");
        sql.append("WHERE user_id = ?");

        Object[] params = {id};
        return query(sql.toString(), new ClientMapper(), params);
    }
}
