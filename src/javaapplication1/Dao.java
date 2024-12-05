/**
 * Course: ITMD 411
 * Date: Dec/ 04/ 2024
 * Done by: Md. Mahmudul Hasan (A20502196)
 * PROJECT: Database Recording
 */

package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
    private String url = "jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false";
    private String username = "fp411";
    private String password = "411";
    static Connection connect = null;
    Statement statement = null;

    public Dao() {
    }

    // Establishes a connection to the database
    public Connection getConnection() {
        try {
            connect = DriverManager.getConnection(url, username, password);
            statement = connect.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connect;
    }

    // Creates the necessary tables for tickets and users in the database
    public void createTables() {
        final String createTicketsTable = "CREATE TABLE IF NOT EXISTS mhasan_tickets("
                + "ticket_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "ticket_issuer VARCHAR(30), "
                + "ticket_description VARCHAR(200), "
                + "status VARCHAR(20) DEFAULT 'Open', "
                + "priority VARCHAR(10), "
                + "assigned_to VARCHAR(30))";

        final String createUsersTable = "CREATE TABLE IF NOT EXISTS mhasan_users("
                + "uid INT AUTO_INCREMENT PRIMARY KEY, "
                + "uname VARCHAR(30), "
                + "upass VARCHAR(30), "
                + "admin INT)";

        try {
            statement = getConnection().createStatement();
            statement.executeUpdate(createTicketsTable);
            statement.executeUpdate(createUsersTable);
            System.out.println("Created tables in the given database...");

            statement.close();
            connect.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Adds default user data to the database from a CSV file
        addUsers();
    }

    // Reads user data from a CSV file and inserts it into the database
    public void addUsers() {
        BufferedReader br;
        List<List<String>> array = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(new File("./userlist.csv")));
            String line;
            while ((line = br.readLine()) != null) {
                array.add(Arrays.asList(line.split(",")));
            }
        } catch (Exception e) {
            System.out.println("There was a problem loading the file");
        }

        try {
            Connection connection = getConnection();
            String checkUserSQL = "SELECT COUNT(*) FROM mhasan_users WHERE uname = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSQL);
            String insertUserSQL = "INSERT INTO mhasan_users(uname, upass, admin) VALUES(?, ?, ?)";
            PreparedStatement insertUserStmt = connection.prepareStatement(insertUserSQL);

            for (List<String> rowData : array) {
                String uname = rowData.get(0);
                String upass = rowData.get(1);
                int admin = Integer.parseInt(rowData.get(2));

                // Checks if the user already exists before inserting
                checkUserStmt.setString(1, uname);
                ResultSet rs = checkUserStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    insertUserStmt.setString(1, uname);
                    insertUserStmt.setString(2, upass);
                    insertUserStmt.setInt(3, admin);
                    insertUserStmt.executeUpdate();
                }
            }

            System.out.println("Unique users added successfully.");
            checkUserStmt.close();
            insertUserStmt.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Inserts a new ticket into the tickets table and returns its generated ID
    public int insertTicket(String ticketName, String ticketDesc) {
        int id = 0;
        try {
            String sql = "INSERT INTO mhasan_tickets(ticket_issuer, ticket_description) VALUES(?, ?)";
            PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, ticketName);
            pstmt.setString(2, ticketDesc);
            pstmt.executeUpdate();

            ResultSet resultSet = pstmt.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // Retrieves a ticket record by its ID
    public ResultSet viewTicketById(int ticketId) {
        ResultSet result = null;
        try {
            String sql = "SELECT * FROM mhasan_tickets WHERE ticket_id = ?";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setInt(1, ticketId);
            result = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Assigns a ticket to a specific user by ticket ID
    public boolean assignTicket(int ticketId, String assignee) {
        boolean assigned = false;
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("UPDATE mhasan_tickets SET assigned_to = ? WHERE ticket_id = ?")) {
            pstmt.setString(1, assignee);
            pstmt.setInt(2, ticketId);
            assigned = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assigned;
    }

    // Updates the description of a ticket identified by its ID
    public boolean updateTicketById(int ticketId, String newDescription) {
        boolean updated = false;
        try {
            String sql = "UPDATE mhasan_tickets SET ticket_description = ? WHERE ticket_id = ?";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setString(1, newDescription);
            pstmt.setInt(2, ticketId);
            updated = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    // Deletes a user by their user ID
    public boolean deleteUserById(int userId) {
        boolean deleted = false;
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("DELETE FROM mhasan_users WHERE uid = ?")) {
            pstmt.setInt(1, userId);
            deleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deleted;
    }

    // Deletes a ticket by its ID with confirmation
    public boolean deleteTicketById(int ticketId) {
        boolean deleted = false;
        try {
            String sql = "DELETE FROM mhasan_tickets WHERE ticket_id = ?";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setInt(1, ticketId);

            int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete ticket number: " + ticketId + "?",
                    "Confirm Deletion", javax.swing.JOptionPane.YES_NO_OPTION);

            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                deleted = pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deleted;
    }

    // Closes a ticket by updating its status to 'Closed'
    public boolean closeTicketById(int ticketId) {
        boolean closed = false;
        try {
            String sql = "UPDATE mhasan_tickets SET status = 'Closed' WHERE ticket_id = ?";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setInt(1, ticketId);
            closed = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return closed;
    }

    // Reads all ticket records from the database
    public ResultSet readRecords() {
        ResultSet results = null;
        try {
            statement = getConnection().createStatement();
            results = statement.executeQuery("SELECT * FROM mhasan_tickets");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Filters tickets by their status
    public ResultSet filterTicketsByStatus(String status) {
        ResultSet results = null;
        try {
            String sql = "SELECT * FROM mhasan_tickets WHERE status = ?";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setString(1, status);
            results = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Filters tickets by their priority level
    public ResultSet filterTicketsByPriority(String priority) {
        ResultSet results = null;
        try {
            String sql = "SELECT * FROM mhasan_tickets WHERE priority = ?";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setString(1, priority);
            results = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Retrieves all user records from the database
    public ResultSet getAllUsers() {
        ResultSet results = null;
        try {
            String sql = "SELECT uid, uname, admin FROM mhasan_users";
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            results = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
