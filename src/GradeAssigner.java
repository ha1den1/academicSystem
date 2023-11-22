import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class GradeAssigner {
    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void assignGradesFromUI() {
        List<String> userNames = UserSubjectViewer.getUserNames();
        if (userNames == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch users from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> userDropdown = new JComboBox<>(userNames.toArray(new String[0]));
        JTextField gradeField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Select a user:"));
        panel.add(userDropdown);
        panel.add(new JLabel("Enter grade:"));
        panel.add(gradeField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Assign Grades", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String selectedUser = userDropdown.getSelectedItem().toString();
            String grade = gradeField.getText();

            try (Connection conn = DriverManager.getConnection(UserSubjectViewer.DB_URL, UserSubjectViewer.USERNAME, UserSubjectViewer.PASSWORD)) {
                List<String> subjectNames = UserSubjectViewer.getUserSubjects(conn, UserSubjectViewer.getUserId(conn, selectedUser));
                if (subjectNames == null) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to fetch subjects from the database",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JComboBox<String> subjectDropdown = new JComboBox<>(subjectNames.toArray(new String[0]));

                JPanel subjectPanel = new JPanel(new GridLayout(0, 2));
                subjectPanel.add(new JLabel("Select a subject:"));
                subjectPanel.add(subjectDropdown);

                int subjectResult = JOptionPane.showConfirmDialog(null, subjectPanel,
                        "Assign Grades", JOptionPane.OK_CANCEL_OPTION);

                if (subjectResult == JOptionPane.OK_OPTION) {
                    String selectedSubject = subjectDropdown.getSelectedItem().toString();
                    int subjectId = UserSubjectViewer.getSubjectId(conn, selectedSubject);

                    if (subjectId != -1) {
                        if (isValidGrade(grade)) {
                            assignGrades(selectedUser, subjectId, grade);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Incorrect input. Grade must be between 1 and 10.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Subject not found",
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

    private static boolean isValidGrade(String grade) {
        try {
            int gradeValue = Integer.parseInt(grade);
            return gradeValue >= 1 && gradeValue <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void assignGrades(String userName, int subjectId, String grade) {
        try (Connection conn = DriverManager.getConnection(UserSubjectViewer.DB_URL, UserSubjectViewer.USERNAME, UserSubjectViewer.PASSWORD)) {
            int userId = UserSubjectViewer.getUserId(conn, userName);

            if (userId != -1) {
                String assignGradeQuery = "INSERT INTO grades (user_id, subject_id, grade) VALUES (?, ?, ?)";
                try (PreparedStatement assignGradeStatement = conn.prepareStatement(assignGradeQuery)) {
                    assignGradeStatement.setInt(1, userId);
                    assignGradeStatement.setInt(2, subjectId);
                    assignGradeStatement.setString(3, grade);
                    assignGradeStatement.executeUpdate();

                    JOptionPane.showMessageDialog(null,
                            "Grade assigned successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "User not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
