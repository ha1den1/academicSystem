import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GroupHandler {

    public static void addGroup(String groupName) {
        try (Connection connection = DatabaseHandler.connect()) {
            String query = "INSERT INTO groups (group_name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, groupName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DatabaseHandler.closeConnection(null);
        }
    }
    public static void addGroupFromUI(String groupName) {
        if (groupName != null && !groupName.isEmpty()) {
            addGroup(groupName);
            JOptionPane.showMessageDialog(null, "Group added successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Group name cannot be empty!");
        }
    }
}
