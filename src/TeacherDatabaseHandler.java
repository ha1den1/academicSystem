import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TeacherDatabaseHandler {

    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void addTeacherFromUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField nameField = new JTextField(10);
        JTextField emailField = new JTextField(10);
        JTextField passwordField = new JTextField(10);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Teacher Information",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String teacherName = nameField.getText();
            String teacherEmail = emailField.getText();
            String teacherPassword = passwordField.getText();

            if (!teacherName.isEmpty() && !teacherEmail.isEmpty() && !teacherPassword.isEmpty()) {
                addTeacher(teacherName, teacherEmail, teacherPassword);
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void addTeacher(String teacherName, String teacherEmail, String teacherPassword) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO users (name, email, teacher, password) VALUES (?, ?, 1, ?)";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, teacherName);
                preparedStatement.setString(2, teacherEmail);
                preparedStatement.setString(3, teacherPassword);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Teacher added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Failed to add teacher",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
