import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Admin extends JFrame {
    private JPanel panel1;
    private JButton btnAddTeacher;
    private JButton btnAddStudent;
    private JButton btnEditUser;
    private JButton btnAddSubject;
    private JButton btnEditSubject;
    private JButton btnEditGroup;
    private JButton btnAddGroup;
    private JButton btnAddGrade;
    private JButton btnViewUser;
    private JButton btnAssignUser;
    private JButton btnAssignUserToSubjectButton;
    private JButton btnDeleteGrade;
    private JButton btnLogOut;

    public Admin() {
        setTitle("Admin UI");
        setContentPane(panel1);
        setMinimumSize(new Dimension(450, 474));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        btnAddTeacher.addActionListener(e -> TeacherDatabaseHandler.addTeacherFromUI());

        btnAddStudent.addActionListener(e -> StudentDatabaseHandler.addStudentFromUI());

        btnEditUser.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            UserEditDialog editDialog = new UserEditDialog(Admin.this);
            editDialog.setVisible(true);
        }));
        btnAssignUser.addActionListener(e -> UserGroupAssigner.assignUserToGroupFromUI());
        btnViewUser.addActionListener(e -> UserSubjectViewer.viewUserSubjectsAndGroups(getSelectedUser()));
        btnAssignUserToSubjectButton.addActionListener(e -> UserSubjectAssigner.assignUserToSubjectFromUI());
        btnEditSubject.addActionListener(e -> SwingUtilities.invokeLater(()->{
            SubjectEditor subjectEditor = new SubjectEditor(Admin.this);
            subjectEditor.setVisible(true);
        }));
        btnEditGroup.addActionListener(e -> SwingUtilities.invokeLater(()->{
            GroupEditor groupEditor = new GroupEditor(Admin.this);
            groupEditor.setVisible(true);
        }));
        btnLogOut.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(Admin.this,
                    "Are you sure you want to log out?",
                    "Log Out Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                dispose();

                openLoginForm();
            }
        });
        btnDeleteGrade.addActionListener(e -> GradeDeleter.deleteGradesFromUI());
    btnAddGrade.addActionListener(e -> GradeAssigner.assignGradesFromUI());

        btnAddSubject.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog("Enter the subject name:");
            if (subjectName != null) {
                SubjectHandler.addSubjectFromUI(subjectName);
            }
        });

        btnAddGroup.addActionListener(e -> {
            String groupName = JOptionPane.showInputDialog("Enter the group name:");
            if (groupName != null) {
                GroupHandler.addGroupFromUI(groupName);
            }
        });


    }
    private void openLoginForm() {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm(null);
            loginForm.setVisible(true);
        });
    }
    private String getSelectedUser() {
        return (String) JOptionPane.showInputDialog(
                null,
                "Select a user:",
                "User Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                UserSubjectViewer.getUserNames().toArray(),
                null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Admin adminPage = new Admin();
            adminPage.setVisible(true);
            adminPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });

    }
}
