package main;

import main.champion.Champion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    /**
     * Initializes the DB Manager, should only be done once. Establishes the initial connection and creates the
     * necessary tables if they don't exist
     */
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

            // favourite table, unique id & string name are only two columns
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

    /**
     * Inserts a champion object into the database by serializing the object into a byte array and
     * storing it along with the champion name
     *
     * @param champion the champion object to be stored
     */
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

    /**
     * Queries the database to find a specific champion. The byte array found in the database in then deserialized into
     * a champion object and returned.
     *
     * @param championName the name of the champion to be queried'
     *
     * @return the found champion or null if not found
     */
    public Champion queryChampion(String championName) {
        Champion champion = null;
        String sql = "SELECT id, name, data "
                + "FROM champions WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, championName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                champion = Champion.deserialize(rs.getBytes("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return champion;
    }

    /**
     * Establishes a connection to the SQLite database
     *
     * @return Connection object representing the new connection
     */
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

    /**
    * method to insert a new favourite into the favourites db, passed in a champion object and will insert solely name
    *
    * @param champion  champion you wish to be favourited
    */
    public void insertFavourite(Champion champion) {
        String sql = "INSERT INTO favourites(name) VALUES(?)";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, champion.getName());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to remove a favourite from the db, passed in a champion object and will remove name
     *
     * @param champion  champion you wish to be removed
     */
    public void removeFavourite(Champion champion) {
        String sql = "DELETE FROM favourites WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, champion.getName());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of strings of currently favourited champions
     */
    public List<String> queryFavourites() {
        List<String> favourites = new ArrayList<>();
        String sql = "SELECT name FROM favourites";

        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                // append each name to the favourites list
                favourites.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return favourites;
    }
}
