package com.example.cloudst.authentication;

import lombok.SneakyThrows;

import java.sql.*;

public class DBBaseAuthentication implements Authentication {

    private static final String SQLITE_SRC = "jdbc:sqlite:src/main/resources/db/users_auth_reg";

    private static Connection connection;
    private static Statement stmt;

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        String passwordDB = null;
        String username = null;

        startAuthentication();

        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users_auth WHERE login = ?");
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.isClosed()) {
                return null;
            }

            username = rs.getString("username");
            passwordDB = rs.getString("password");


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ((passwordDB.equals(password))) ? username : null;
    }

    @SneakyThrows
    public byte[] getSalt(String login){
        byte[] salt = null;
        startAuthentication();

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users_auth WHERE login = ?");

        ps.setString(1, login);
        ResultSet resultSet = ps.executeQuery();

        if (resultSet.isClosed()) {
            return null;
        }

        salt = resultSet.getBytes(5);

        return salt;
    }

    @Override
    public void createUser(String login, String password, String username, byte[] salt) {
        startAuthentication();
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO users_auth (username, login, password, salt) VALUES (?,?,?,?)");

            pstmt.setString(1, username);
            pstmt.setString(2, login);
            pstmt.setString(3, password);
            pstmt.setBytes(4, salt);

            pstmt.addBatch();

            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean checkLoginByFree(String login) {
        return null;
    }

    @Override
    public void startAuthentication() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_SRC);
            Statement stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void endAuthentication() {
        connection.close();
    }

   /* public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_SRC);
            Statement stmt1 = connection.createStatement();
         //   stmt1.executeUpdate("DELETE FROM users_auth WHERE id = 1");
            ResultSet res = stmt1.executeQuery("SELECT * FROM users_auth");
            while (res.next()) {
                System.out.println("id    " + "|  " +
                        "username         " + "|  " +
                        "login             " + "|  " +
                        "password           " +
                        "|" + "salt");
                System.out.println(res.getString(1) + "       " +
                        res.getString(2) +"                  " +
                        res.getString(3) +"               " +
                        res.getString(4) +"        " +
                        "     " + Arrays.toString(res.getBytes(5)));
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }*/
}
