package com.ldnhat.mapper.impl;

import com.ldnhat.mapper.RowMapper;
import com.ldnhat.model.ClientModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientMapper implements RowMapper<ClientModel> {
    @Override
    public ClientModel mapRow(ResultSet rs) throws SQLException {

        ClientModel clientModel = new ClientModel();

        clientModel.setId(rs.getInt("user_id"));
        clientModel.setName(rs.getString("name"));
        clientModel.setUsername(rs.getString("username"));
        clientModel.setPassword(rs.getString("password"));
        clientModel.setImage(rs.getString("image"));


        return clientModel;
    }
}
