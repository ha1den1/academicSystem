import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentDatabaseHandler {

    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void addStudentFromUI() {
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

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Student Information",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String studentName = nameField.getText();
            String studentEmail = emailField.getText();
            String studentPassword = passwordField.getText();

            if (!studentPassword.isEmpty() && !studentEmail.isEmpty() && !studentName.isEmpty()) {
                addTeacher(studentName, studentEmail, studentPassword);
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void addTeacher(String studentName, String studentEmail, String studentPassword) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, studentEmail);
                preparedStatement.setString(2, studentEmail);
                preparedStatement.setString(3, studentEmail);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Student added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Failed to add student",
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
