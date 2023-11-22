import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectHandler {

    public static void addSubject(String subjectName) {
        try (Connection connection = DatabaseHandler.connect()) {
            String query = "INSERT INTO subjects (subject_name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, subjectName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DatabaseHandler.closeConnection(null);
        }
    }
    public static void addSubjectFromUI(String subjectName) {
        if (subjectName != null && !subjectName.isEmpty()) {
            addSubject(subjectName);
            JOptionPane.showMessageDialog(null, "Subject added successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Subject name cannot be empty!");
        }
    }

    public static List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect()) {
            String query = "SELECT subject_name FROM subjects";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    subjects.add(resultSet.getString("subject_name"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DatabaseHandler.closeConnection(null);
        }

        return subjects;
    }
}
