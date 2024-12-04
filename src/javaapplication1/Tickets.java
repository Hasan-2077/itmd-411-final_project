package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

    Dao dao = new Dao();
    Boolean chkIfAdmin = null;

    private JMenu mnuFile = new JMenu("File");
    private JMenu mnuAdmin = new JMenu("Admin");
    private JMenu mnuTickets = new JMenu("Tickets");
    private JMenu mnuFilters = new JMenu("Filters");

    JMenuItem mnuItemExit;
    JMenuItem mnuItemUpdate;
    JMenuItem mnuItemDelete;
    JMenuItem mnuItemCloseTicket;
    JMenuItem mnuItemOpenTicket;
    JMenuItem mnuItemViewTicket;
    JMenuItem mnuItemAssignTicket;
    JMenuItem mnuItemFilterByStatus;
    JMenuItem mnuItemFilterByPriority;
    JMenuItem mnuItemDashboard;

    public Tickets(Boolean isAdmin) {
        chkIfAdmin = isAdmin;
        createMenu();
        prepareGUI();
    }

    private void createMenu() {
        // File Menu
        mnuItemExit = new JMenuItem("Exit");
        mnuFile.add(mnuItemExit);

        // Admin Menu (only accessible to admins)
        mnuItemUpdate = new JMenuItem("Update Ticket");
        mnuItemDelete = new JMenuItem("Delete Ticket");
        mnuItemCloseTicket = new JMenuItem("Close Ticket");
        mnuItemAssignTicket = new JMenuItem("Assign Ticket");
        mnuItemDashboard = new JMenuItem("Dashboard");
        mnuAdmin.add(mnuItemUpdate);
        mnuAdmin.add(mnuItemDelete);
        mnuAdmin.add(mnuItemCloseTicket);
        mnuAdmin.add(mnuItemAssignTicket);
        mnuAdmin.add(mnuItemDashboard);

        // Tickets Menu
        mnuItemOpenTicket = new JMenuItem("Open Ticket");
        mnuItemViewTicket = new JMenuItem("View Tickets");
        mnuTickets.add(mnuItemOpenTicket);
        mnuTickets.add(mnuItemViewTicket);

        // Filters Menu
        mnuItemFilterByStatus = new JMenuItem("Filter by Status");
        mnuItemFilterByPriority = new JMenuItem("Filter by Priority");
        mnuFilters.add(mnuItemFilterByStatus);
        mnuFilters.add(mnuItemFilterByPriority);

        // Add Action Listeners
        mnuItemExit.addActionListener(this);
        mnuItemUpdate.addActionListener(this);
        mnuItemDelete.addActionListener(this);
        mnuItemCloseTicket.addActionListener(this);
        mnuItemAssignTicket.addActionListener(this);
        mnuItemDashboard.addActionListener(this);
        mnuItemOpenTicket.addActionListener(this);
        mnuItemViewTicket.addActionListener(this);
        mnuItemFilterByStatus.addActionListener(this);
        mnuItemFilterByPriority.addActionListener(this);

        if (!chkIfAdmin) {
            mnuAdmin.setEnabled(false); // Disable Admin menu for non-admin users
        }
    }

    private void prepareGUI() {
        JMenuBar bar = new JMenuBar();
        bar.add(mnuFile);
        bar.add(mnuAdmin);
        bar.add(mnuTickets);
        bar.add(mnuFilters);
        setJMenuBar(bar);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent wE) {
                System.exit(0);
            }
        });

        setSize(400, 600);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mnuItemExit) {
            System.exit(0);
        } else if (e.getSource() == mnuItemOpenTicket) {
            String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
            String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

            int id = dao.insertRecords(ticketName, ticketDesc);
            if (id != 0) {
                JOptionPane.showMessageDialog(null, "Ticket ID: " + id + " created successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create the ticket.");
            }

        } else if (e.getSource() == mnuItemViewTicket) {
            try {
                JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
                jt.setBounds(60, 80, 300, 400);
                JScrollPane sp = new JScrollPane(jt);
                add(sp);
                setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == mnuItemUpdate) {
            int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to update:"));
            String newDescription = JOptionPane.showInputDialog(null, "Enter new description:");
            boolean isUpdated = dao.updateTicket(ticketId, newDescription);
            if (isUpdated) {
                JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update Ticket ID: " + ticketId);
            }

        } else if (e.getSource() == mnuItemDelete) {
            int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to delete:"));
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete Ticket ID: " + ticketId + "?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean isDeleted = dao.deleteTicket(ticketId);
                if (isDeleted) {
                    JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete Ticket ID: " + ticketId);
                }
            }

        } else if (e.getSource() == mnuItemCloseTicket) {
            int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to close:"));
            boolean isClosed = dao.closeTicket(ticketId);
            if (isClosed) {
                JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " closed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to close Ticket ID: " + ticketId);
            }

        } else if (e.getSource() == mnuItemAssignTicket) {
            int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to assign:"));
            String assignee = JOptionPane.showInputDialog(null, "Enter the assignee's name:");
            boolean isAssigned = dao.assignTicket(ticketId, assignee);
            if (isAssigned) {
                JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " assigned to " + assignee + " successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to assign Ticket ID: " + ticketId);
            }

        } else if (e.getSource() == mnuItemDashboard) {
            // Admin Dashboard Placeholder
            JOptionPane.showMessageDialog(null, "Admin Dashboard feature is under development.");

        } else if (e.getSource() == mnuItemFilterByStatus) {
            String status = JOptionPane.showInputDialog(null, "Enter status to filter by (Open/Closed):");
            try {
                JTable jt = new JTable(ticketsJTable.buildTableModel(dao.filterTicketsByStatus(status)));
                JScrollPane sp = new JScrollPane(jt);
                add(sp);
                setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == mnuItemFilterByPriority) {
            String priority = JOptionPane.showInputDialog(null, "Enter priority to filter by (High/Medium/Low):");
            try {
                JTable jt = new JTable(ticketsJTable.buildTableModel(dao.filterTicketsByPriority(priority)));
                JScrollPane sp = new JScrollPane(jt);
                add(sp);
                setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
