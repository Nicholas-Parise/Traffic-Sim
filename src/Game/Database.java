package Game;

import java.sql.*;

// nicholas parise
// this class manages the database
public class Database {

    final static String fileName = "userDatabase";

    /**
     * simply get the connection to the database
     * @return connection
     */
    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + fileName + ".db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * creates a new database
     */
    public static void createNewDatabase() {
        Connection conn = connect();
        if (conn != null) {
            System.out.println("database created");
        }else{
            System.out.println("Failed to create database");
        }
    }


    /**
     * adds a table to the database
     */
    public static void createNewTable() {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id integer PRIMARY KEY,\n"
                + " username text NOT NULL,\n"
                + " password text NOT NULL\n"
                + ");";

        try{
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * insert user into the database
     * @param username String username
     * @param password String password
     */
    public static void insert(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES('"+username+"','"+password+"')";

        try{
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * determines if an account exists in the database
     * @param username String username
     * @param password String password
     * @return true if account exists
     */
    public static boolean getAccount(String username, String password){
        // query database for count of rows where user and password is same as one provided
        String sql = "SELECT COUNT(*) AS total FROM users WHERE username = '"+username+"' AND password = '"+password+"'";

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            if(rs.getInt("total") <=0){
                return false; // if it doesn't exist in database return false
            }else{
                return true; // else account exists return true
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }





}
