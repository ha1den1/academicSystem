import javax.swing.*;
import java.awt.*;

public class Student extends JFrame {
    private JPanel panel1;
    private JButton btnSelectGrades;
    private User loggedInUser;

    public Student(User user) {
        this.loggedInUser = user;
        setTitle("Student UI");
        setContentPane(panel1);
        setMinimumSize(new Dimension(450, 474));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        btnSelectGrades.addActionListener(e -> showStudentGrades());
    }

    private void showStudentGrades() {
        if (loggedInUser != null) {
            UserSubjectViewer.viewUserSubjectsAndGroups(loggedInUser.name);
        } else {
            JOptionPane.showMessageDialog(this,
                    "User information not available",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.name = "Test Student";
            testUser.email = "test@student.com";
            testUser.teacher = 0;
            testUser.admin = 0;
            testUser.password = "testpassword";

            Student studentPage = new Student(testUser);
            studentPage.setVisible(true);
            studentPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
