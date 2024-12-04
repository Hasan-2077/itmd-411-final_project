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

	Dao conn;
	int loginAttempts = 0; // Track failed login attempts
	final int MAX_ATTEMPTS = 3; // Maximum allowed attempts

	public Login() {
		super("IIT HELP DESK LOGIN");
		conn = new Dao();
		conn.createTables(); // Ensure tables are created

		setSize(400, 210);
		setLayout(new GridLayout(4, 2));
		setLocationRelativeTo(null); // Centers window

		// UI Components
		JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
		JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);

		JTextField txtUname = new JTextField(10);
		JPasswordField txtPassword = new JPasswordField();
		JButton btnSubmit = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// Configure Labels
		lblStatus.setToolTipText("Contact help desk to unlock password");
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);

		// Add Components to Frame
		add(lblUsername); // 1st row
		add(txtUname);
		add(lblPassword); // 2nd row
		add(txtPassword);
		add(btnSubmit); // 3rd row
		add(btnExit);
		add(lblStatus); // 4th row

		// Action Listeners
		btnSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = txtUname.getText().trim();
				String password = new String(txtPassword.getPassword());

				if (username.isEmpty() || password.isEmpty()) {
					lblStatus.setText("Username or password cannot be empty.");
					return;
				}

				if (loginAttempts >= MAX_ATTEMPTS) {
					lblStatus.setText("Account locked. Contact admin.");
					return;
				}

				Boolean isAdmin = authenticateUser(username, password); // Use Boolean to handle null case

				if (isAdmin != null) {
					// Role-based navigation
					if (isAdmin) {
						JOptionPane.showMessageDialog(null, "Welcome, Admin!");
						new AdminPanel(); // Open Admin version of Tickets GUI
					} else {
						JOptionPane.showMessageDialog(null, "Welcome, User!");
						new Tickets(false); // Open User version of Tickets GUI
					}
					setVisible(false);
					dispose();
				} else {
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

		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // Show the frame
	}

	/**
	 * Authenticates the user against the database.
	 * 
	 * @param username The username entered by the user.
	 * @param password The password entered by the user.
	 * @return `true` if the user is an admin, `false` if not, and `null` if
	 *         authentication fails.
	 */
	private Boolean authenticateUser(String username, String password) {
		String query = "SELECT admin FROM mhasan_users WHERE uname = ? AND upass = ?";
		try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
			// Log the query parameters
			System.out.println("Executing query with username: " + username + " and password: " + password);

			// Set parameters for the query
			stmt.setString(1, username);
			stmt.setString(2, password);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				// Log successful authentication
				System.out.println("User authenticated, admin: " + rs.getBoolean("admin"));
				return rs.getBoolean("admin");
			} else {
				// Log failed authentication
				System.out.println("No user found with the given credentials.");
			}
		} catch (SQLException ex) {
			// Log SQL exceptions
			System.out.println("SQL Exception occurred!");
			ex.printStackTrace();
		}
		return null; // Authentication failed
	}

	public static void main(String[] args) {
		new Login(); // Start the login window
	}
}
