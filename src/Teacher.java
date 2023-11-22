import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Teacher extends JFrame {
    private JPanel panel1;
    private JButton btnCheckGroup;
    private JButton btnAddGrades;
    private JButton btnDeleteGrade;

    private User user;

    public Teacher(User user) {
        this.user = user;
        setTitle("Teacher UI");
        setContentPane(panel1);
        setMinimumSize(new Dimension(450, 474));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        btnCheckGroup.addActionListener(e -> {
            String userName = user.name;
            UserInfo userInfo = LoggedInUserInfo.getLoggedInUserInfo(userName);

            if (userInfo != null) {
                String teacherName = userName;
                String teacherGroup = LoggedInUserInfo.getTeacherGroup(userName);

                String message = "Teacher: " + teacherName + "\n";
                if (teacherGroup != null) {
                    message += "Group: " + teacherGroup + "\n";
                }
                message += "\nSubjects:\n" + String.join("\n", userInfo.getSubjects()) + "\n";
                message += "\nGroups:\n" + String.join("\n", userInfo.getGroups()) + "\n";

                JOptionPane.showMessageDialog(null, message, "Teacher Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Teacher not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnAddGrades.addActionListener(e -> GradeAssigner.assignGradesFromUI());
        btnDeleteGrade.addActionListener(e -> GradeDeleter.deleteGradesFromUI());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.name = "teacherUsername";
            Teacher teacherPage = new Teacher(testUser);
            teacherPage.setVisible(true);
            teacherPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
