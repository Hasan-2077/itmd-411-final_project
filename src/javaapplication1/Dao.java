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

    // Constructor
    public Dao() {
    }

    public Connection getConnection() {
        try {
            connect = DriverManager.getConnection(url, username, password);
            statement = connect.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connect;
    }

    // CRUD implementation

    public void createTables() {
        // Variables for SQL Query table creations
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
            // Execute queries to create tables
            statement = getConnection().createStatement();
            statement.executeUpdate(createTicketsTable);
            statement.executeUpdate(createUsersTable);
            System.out.println("Created tables in the given database...");

            // Close connection/statement object
            statement.close();
            connect.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Add users to the users table
        addUsers();
    }

    public void addUsers() {
        // Read user data from file
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
            // Setup the connection with the DB
            Connection connection = getConnection();

            // Prepare a statement to check if the user already exists
            String checkUserSQL = "SELECT COUNT(*) FROM mhasan_users WHERE uname = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSQL);

            // Prepare a statement to insert a new user
            String insertUserSQL = "INSERT INTO mhasan_users(uname, upass, admin) VALUES(?, ?, ?)";
            PreparedStatement insertUserStmt = connection.prepareStatement(insertUserSQL);

            // Loop through the data and insert only unique users
            for (List<String> rowData : array) {
                String uname = rowData.get(0);
                String upass = rowData.get(1);
                int admin = Integer.parseInt(rowData.get(2));

                // Check if the user already exists
                checkUserStmt.setString(1, uname);
                ResultSet rs = checkUserStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    // User doesn't exist, insert them
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

    public int insertRecords(String ticketName, String ticketDesc) {
        int id = 0;
        try {
            statement = getConnection().createStatement();
            statement.executeUpdate("INSERT INTO mhasan_tickets(ticket_issuer, ticket_description) "
                    + "VALUES('" + ticketName + "', '" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

            // Retrieve ticket id number newly auto-generated upon record insertion
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

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

    public ResultSet filterTicketsByStatus(String status) {
        ResultSet results = null;
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                "SELECT * FROM mhasan_tickets WHERE status = ?")) {
            pstmt.setString(1, status);
            results = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public ResultSet filterTicketsByPriority(String priority) {
        ResultSet results = null;
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                "SELECT * FROM mhasan_tickets WHERE priority = ?")) {
            pstmt.setString(1, priority);
            results = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public ResultSet getAllUsers() {
        ResultSet results = null;
        try {
            statement = getConnection().createStatement();
            results = statement.executeQuery("SELECT uid, uname, admin FROM mhasan_users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean updateTicket(int ticketId, String newDescription) {
        boolean updated = false;
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("UPDATE mhasan_tickets SET ticket_description = ? WHERE ticket_id = ?")) {
            pstmt.setString(1, newDescription);
            pstmt.setInt(2, ticketId);
            updated = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

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

    public boolean deleteTicket(int ticketId) {
        boolean deleted = false;
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("DELETE FROM mhasan_tickets WHERE ticket_id = ?")) {
            pstmt.setInt(1, ticketId);
            deleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deleted;
    }

    public boolean closeTicket(int ticketId) {
        boolean closed = false;
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("UPDATE mhasan_tickets SET status = 'Closed' WHERE ticket_id = ?")) {
            pstmt.setInt(1, ticketId);
            closed = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return closed;
    }
}
