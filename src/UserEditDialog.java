import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserEditDialog extends JDialog {
    private JComboBox<String> userComboBox;
    private JTextField newNameField;
    private JTextField newEmailField;
    private JTextField newPasswordField;
    private JButton btnEdit;
    private JButton btnDelete;

    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public UserEditDialog(JFrame parent) {
        super(parent, "Edit User", true);

        JPanel panel = new JPanel(new GridLayout(6, 2));

        List<String> users = getUsers();
        userComboBox = new JComboBox<>(users.toArray(new String[0]));

        newNameField = new JTextField(10);
        newEmailField = new JTextField(10);
        newPasswordField = new JTextField(10);

        btnEdit = new JButton("Edit User");
        btnDelete = new JButton("Delete User");

        panel.add(new JLabel("Select User:"));
        panel.add(userComboBox);
        panel.add(new JLabel("New Name:"));
        panel.add(newNameField);
        panel.add(new JLabel("New Email:"));
        panel.add(newEmailField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(btnEdit);
        panel.add(btnDelete);

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = (String) userComboBox.getSelectedItem();
                editUser(selectedUser);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = (String) userComboBox.getSelectedItem();
                deleteUser(selectedUser);
            }
        });

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    private List<String> getUsers() {
        List<String> users = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT name FROM users";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        users.add(resultSet.getString("name"));
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

        return users;
    }

    private void editUser(String userName) {
        String newName = newNameField.getText();
        String newEmail = newEmailField.getText();
        String newPassword = newPasswordField.getText();

        if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE users SET name=?, email=?, password=? WHERE name=?";
            try (PreparedStatement updateStatement = conn.prepareStatement(sql)) {
                updateStatement.setString(1, newName);
                updateStatement.setString(2, newEmail);
                updateStatement.setString(3, newPassword);
                updateStatement.setString(4, userName);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "User information updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update user information",
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

    private void deleteUser(String userName) {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the user " + userName + "?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                String sql = "DELETE FROM users WHERE name=?";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1, userName);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this,
                                "User deleted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete user",
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
