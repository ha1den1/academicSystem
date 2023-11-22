import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectEditor extends JDialog {
    private JComboBox<String> subjectComboBox;
    private JTextField newSubjectField;
    private JButton btnEdit;
    private JButton btnDelete;

    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public SubjectEditor(JFrame parent) {
        super(parent, "Edit subject", true);
        JPanel panel = new JPanel(new GridLayout(4, 1));

        List<String> subjects = getSubjects();
        subjectComboBox = new JComboBox<>(subjects.toArray(new String[0]));
        newSubjectField = new JTextField(10);
        btnEdit = new JButton("Edit subject");
        btnDelete = new JButton("Delete subject");

        panel.add(new JLabel("Select Subject:"));
        panel.add(subjectComboBox);
        panel.add(new JLabel("New Subject Name:"));
        panel.add(newSubjectField);
        panel.add(btnEdit);
        panel.add(btnDelete);

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSubject = (String) subjectComboBox.getSelectedItem();
                editSubject(selectedSubject);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSubject = (String) subjectComboBox.getSelectedItem();
                deleteSubject(selectedSubject);
            }
        });
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    private List<String> getSubjects() {
        List<String> subjects = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT subject_name FROM subjects";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        subjects.add(resultSet.getString("subject_name"));
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
        return subjects;
    }

    private void editSubject(String subjectName) {
        String newSubjectName = newSubjectField.getText();

        if (newSubjectName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in the new subject name field",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE subjects SET subject_name=? WHERE subject_name=?";
            try (PreparedStatement updateStatement = conn.prepareStatement(sql)) {
                updateStatement.setString(1, newSubjectName);
                updateStatement.setString(2, subjectName);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Subject name updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update subject name",
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

    private void deleteSubject(String subjectName) {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this subject?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                String sql = "DELETE FROM subjects WHERE subject_name=?";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1, subjectName);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Subject deletion successful",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Subject deletion failed",
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
