/**
 * Course: ITMD 411
 * Date: Dec/ 04/ 2024
 * Done by: Md. Mahmudul Hasan (A20502196)
 * PROJECT: Database Recording
 */

package javaapplication1;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Login extends JFrame {

	// Data Access Object for database interaction
	Dao conn;

	// Variables to track login attempts
	int loginAttempts = 0;
	final int MAX_ATTEMPTS = 3;

	// Constructor to initialize the login GUI and handle logic
	public Login() {
		super("IIT HELP DESK LOGIN");
		conn = new Dao();
		conn.createTables(); // Ensure required tables exist in the database

		// Configure frame properties
		setSize(400, 210);
		setLayout(new GridLayout(4, 2));
		setLocationRelativeTo(null);

		// UI Components
		JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
		JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);

		JTextField txtUname = new JTextField(10);
		JPasswordField txtPassword = new JPasswordField();
		JButton btnSubmit = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// Additional UI Configuration
		lblStatus.setToolTipText("Contact help desk to unlock password");
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);

		// Add components to the frame
		add(lblUsername);
		add(txtUname);
		add(lblPassword);
		add(txtPassword);
		add(btnSubmit);
		add(btnExit);
		add(lblStatus);

		// Action listener for the Submit button
		btnSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Extract and validate user credentials
				String username = txtUname.getText().trim();
				String password = new String(txtPassword.getPassword());

				if (username.isEmpty() || password.isEmpty()) {
					lblStatus.setText("Username or password cannot be empty.");
					return;
				}

				// Check if maximum login attempts have been reached
				if (loginAttempts >= MAX_ATTEMPTS) {
					lblStatus.setText("Account locked. Contact admin.");
					return;
				}

				// Authenticate user credentials against the database
				Boolean isAdmin = authenticateUser(username, password);

				if (isAdmin != null) {
					// Navigate to AdminPanel or Tickets UI based on user role
					if (isAdmin) {
						JOptionPane.showMessageDialog(null, "Welcome, Admin!");
						new AdminPanel();
					} else {
						JOptionPane.showMessageDialog(null, "Welcome, User!");
						new Tickets(false);
					}
					setVisible(false);
					dispose(); // Close the login window
				} else {
					// Handle invalid credentials and increment login attempts
					loginAttempts++;
					int remainingAttempts = MAX_ATTEMPTS - loginAttempts;
					lblStatus.setText("Invalid credentials. " + remainingAttempts + " attempt(s) left.");
					if (remainingAttempts == 0) {
						JOptionPane.showMessageDialog(null, "Account locked. Exiting...");
						System.exit(0);
					}
				}
			}
		});

		// Action listener for the Exit button
		btnExit.addActionListener(e -> System.exit(0));

		// Make the frame visible
		setVisible(true);
	}

	// Authenticates user credentials and returns if the user is an admin or not
	private Boolean authenticateUser(String username, String password) {
		String query = "SELECT admin FROM mhasan_users WHERE uname = ? AND upass = ?";
		try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
			System.out.println("Executing query with username: " + username + " and password: " + password);

			stmt.setString(1, username);
			stmt.setString(2, password);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				System.out.println("User authenticated, admin: " + rs.getBoolean("admin"));
				return rs.getBoolean("admin");
			} else {
				System.out.println("No user found with the given credentials.");
			}
		} catch (SQLException ex) {
			System.out.println("SQL Exception occurred!");
			ex.printStackTrace();
		}
		return null;
	}

	// Entry point for the application
	public static void main(String[] args) {
		new Login();
	}
}
