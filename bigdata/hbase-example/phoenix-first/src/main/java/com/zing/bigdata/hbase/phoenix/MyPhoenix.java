package com.zing.bigdata.hbase.phoenix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyPhoenix {

    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        Connection connection = DriverManager.getConnection("jdbc:phoenix:hadooooop:2181");

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM person");

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }

        statement.close();
        connection.close();
    }

}
