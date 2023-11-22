import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class GradeDeleter {
    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void deleteGradesFromUI() {
        List<String> userNames = UserSubjectViewer.getUserNames();
        if (userNames == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch users from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> userDropdown = new JComboBox<>(userNames.toArray(new String[0]));

        try (Connection conn = DriverManager.getConnection(UserSubjectViewer.DB_URL, UserSubjectViewer.USERNAME, UserSubjectViewer.PASSWORD)) {
            String selectedUser = (String) JOptionPane.showInputDialog(
                    null,
                    "Select a user:",
                    "User Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    userNames.toArray(),
                    null);

            if (selectedUser != null) {
                List<String> subjectNames = UserSubjectViewer.getUserSubjects(conn, UserSubjectViewer.getUserId(conn, selectedUser));
                if (subjectNames == null) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to fetch subjects from the database",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JComboBox<String> subjectDropdown = new JComboBox<>(subjectNames.toArray(new String[0]));

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Select a subject:"));
                panel.add(subjectDropdown);

                int result = JOptionPane.showConfirmDialog(null, panel,
                        "Delete Grades", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String selectedSubject = subjectDropdown.getSelectedItem().toString();
                    List<String> grades = UserSubjectViewer.getGrades(conn, UserSubjectViewer.getUserId(conn, selectedUser), UserSubjectViewer.getSubjectId(conn, selectedSubject));

                    if (!grades.isEmpty()) {
                        JComboBox<String> gradeDropdown = new JComboBox<>(grades.toArray(new String[0]));

                        JPanel gradePanel = new JPanel(new GridLayout(0, 1));
                        gradePanel.add(new JLabel("Select a grade to delete:"));
                        gradePanel.add(gradeDropdown);

                        int gradeResult = JOptionPane.showConfirmDialog(null, gradePanel,
                                "Delete Grade", JOptionPane.OK_CANCEL_OPTION);

                        if (gradeResult == JOptionPane.OK_OPTION) {
                            String selectedGrade = gradeDropdown.getSelectedItem().toString();
                            deleteGrade(selectedUser, selectedSubject, selectedGrade);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No grades found for the selected user and subject",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
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

    private static void deleteGrade(String userName, String subjectName, String grade) {
        try (Connection conn = DriverManager.getConnection(UserSubjectViewer.DB_URL, UserSubjectViewer.USERNAME, UserSubjectViewer.PASSWORD)) {
            int userId = UserSubjectViewer.getUserId(conn, userName);
            int subjectId = UserSubjectViewer.getSubjectId(conn, subjectName);

            if (userId != -1 && subjectId != -1) {
                String deleteGradeQuery = "DELETE FROM grades WHERE user_id = ? AND subject_id = ? AND grade = ?";
                try (PreparedStatement deleteGradeStatement = conn.prepareStatement(deleteGradeQuery)) {
                    deleteGradeStatement.setInt(1, userId);
                    deleteGradeStatement.setInt(2, subjectId);
                    deleteGradeStatement.setString(3, grade);
                    int rowsAffected = deleteGradeStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null,
                                "Grade deleted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No matching grade found for the selected user, subject, and grade",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "User or subject not found",
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradeDeleter::deleteGradesFromUI);
    }
}
