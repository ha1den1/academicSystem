import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupEditor extends JDialog {
    private JComboBox<String> groupComboBox;
    private JTextField newGroupField;
    private JButton btnEdit;
    private JButton btnDelete;

    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public GroupEditor(JFrame parent) {
        super(parent, "Edit group", true);
        JPanel panel = new JPanel(new GridLayout(4, 1));

        List<String> groups = getGroups();
        groupComboBox = new JComboBox<>(groups.toArray(new String[0]));
        newGroupField = new JTextField(10);
        btnEdit = new JButton("Edit group");
        btnDelete = new JButton("Delete group");

        panel.add(new JLabel("Select Group:"));
        panel.add(groupComboBox);
        panel.add(new JLabel("New Group Name:"));
        panel.add(newGroupField);
        panel.add(btnEdit);
        panel.add(btnDelete);

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGroup = (String) groupComboBox.getSelectedItem();
                editGroup(selectedGroup);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGroup = (String) groupComboBox.getSelectedItem();
                deleteGroup(selectedGroup);
            }
        });
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    private List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT group_name FROM groups";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        groups.add(resultSet.getString("group_name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return groups;
    }

    private void editGroup(String groupName) {
        String newGroupName = newGroupField.getText();

        if (newGroupName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in the new group name field",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE groups SET group_name=? WHERE group_name=?";
            try (PreparedStatement updateStatement = conn.prepareStatement(sql)) {
                updateStatement.setString(1, newGroupName);
                updateStatement.setString(2, groupName);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Group name updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update group name",
                            "Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setVisible(false);
        }
    }

    private void deleteGroup(String groupName) {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this group?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                String sql = "DELETE FROM groups WHERE group_name=?";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1, groupName);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Group deletion successful",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Group deletion failed",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                this.setVisible(false);
            }
        }
    }
}
