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

        btnAddTeacher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TeacherDatabaseHandler.addTeacherFromUI();
            }
        });

        btnAddStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDatabaseHandler.addStudentFromUI();
            }
        });

        btnEditUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    UserEditDialog editDialog = new UserEditDialog(Admin.this);
                    editDialog.setVisible(true);
                });
            }
        });
        btnAssignUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserGroupAssigner.assignUserToGroupFromUI();
            }
        });
        btnViewUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserSubjectViewer.viewUserSubjectsAndGroups(getSelectedUser());
            }
        });
        btnAssignUserToSubjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserSubjectAssigner.assignUserToSubjectFromUI();
            }
        });
        btnEditSubject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(()->{
                    SubjectEditor subjectEditor = new SubjectEditor(Admin.this);
                    subjectEditor.setVisible(true);
                });
            }
        });
        btnEditGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(()->{
                    GroupEditor groupEditor = new GroupEditor(Admin.this);
                    groupEditor.setVisible(true);
                });
            }
        });
        btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Admin.this,
                        "Are you sure you want to log out?",
                        "Log Out Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    dispose();

                    openLoginForm();
                }
            }
        });
        btnDeleteGrade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GradeDeleter.deleteGradesFromUI();
            }
        });
    btnAddGrade.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            GradeAssigner.assignGradesFromUI();
        }
    });

        btnAddSubject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String subjectName = JOptionPane.showInputDialog("Enter the subject name:");
                if (subjectName != null) {
                    SubjectHandler.addSubjectFromUI(subjectName);
                }
            }
        });

        btnAddGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = JOptionPane.showInputDialog("Enter the group name:");
                if (groupName != null) {
                    GroupHandler.addGroupFromUI(groupName);
                }
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
