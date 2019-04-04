package main;

import main.champion.Champion;

import java.sql.*;

public class DBManager {

    public DBManager() {
        Connection conn = null;
        try {
            conn = getConnection();

            // champion data table
            Statement statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS champions (\n"
                    + " id INTEGER PRIMARY KEY,\n"
                    + " name text NOT NULL,\n"
                    + " data blob NOT NULL\n"
                    + ");";
            statement.execute(sql);

            // favourite table
            statement = conn.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS favourites (\n"
                    + " id INTEGER PRIMARY KEY,\n"
                    + " name text NOT NULL\n"
                    + ");";
            statement.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void insertChampion(Champion champion) {
        String sql = "INSERT INTO champions(name,data) VALUES(?,?)";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, champion.getName());
            statement.setBytes(2, champion.serialize());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Champion queryChampion(String championName) {
        Champion champion = null;
        String sql = "SELECT id, name, data "
                + "FROM champions WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, championName);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                champion = Champion.deserialize(rs.getBytes("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return champion;
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + Main.getApplicationPath() + "lol-champion.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void insertFavourite(Champion champion){
        String sql = "INSERT INTO favourites(name) VALUES(?)";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, champion.getName());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String queryFavourites(String championName){
        String curChamp = "";
        String sql = "SELECT name FROM favourites";

        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, championName);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                curChamp = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curChamp;


    }
}
