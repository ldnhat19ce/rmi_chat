package com.ldnhat.dao.impl;

import com.ldnhat.dao.GenericDAO;
import com.ldnhat.mapper.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AbstractDAO<T> implements GenericDAO<T> {

    ResourceBundle bundle = ResourceBundle.getBundle("db");

    public Connection getConnection() {

        String url = bundle.getString("url");
        String username = bundle.getString("username");
        String password = bundle.getString("password");

        try {
            Class.forName(bundle.getString("driveName"));
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<T> query(String sql, RowMapper<T> rowMapper, Object... parameters) {
        List<T> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ptmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ptmt = conn.prepareStatement(sql);

            //set parameter
            setStatement(ptmt, parameters);
            rs = ptmt.executeQuery();
            while (rs.next()) {

                list.add(rowMapper.mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ptmt != null) {
                    ptmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Override
    public void update(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            ptmt = conn.prepareStatement(sql);
            setStatement(ptmt, parameters);
            ptmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ptmt != null) {
                    ptmt.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public Long insert(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ptmt = null;
        ResultSet rs = null;
        Long id = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            ptmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setStatement(ptmt, parameters);
            ptmt.executeUpdate();
            rs = ptmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong(1);
            }
            conn.commit();
            return id;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ptmt != null) {
                    ptmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int count(String sql, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            int count = 0;
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            setStatement(statement, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            return 0;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                return 0;
            }
        }


    }

    private void setStatement(PreparedStatement ptmt, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            int index = i + 1;
            if (parameter instanceof Long) {
                ptmt.setLong(index, (Long) parameter);
            } else if (parameter instanceof Integer) {
                ptmt.setInt(index, (Integer) parameter);
            } else if (parameter instanceof String) {
                ptmt.setString(index, (String) parameter);
            } else if (parameter instanceof Float) {
                ptmt.setFloat(index, (Float) parameter);
            } else if (parameter instanceof Timestamp) {
                ptmt.setTimestamp(index, (Timestamp) parameter);
            }else if(parameter instanceof Date){
                ptmt.setDate(index, (Date) parameter);
            }
        }
    }

}
