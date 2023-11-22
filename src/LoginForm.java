import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField tfPassword;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel loginPanel;
    private User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(tfPassword.getPassword());

                user = getAuthenticatedUser(email, password);
                if (user != null) {
                    openAdminPage();
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email or password invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void openAdminPage() {
        SwingUtilities.invokeLater(() -> {
            if (user.admin == 1) {
                Admin adminPage = new Admin();
                adminPage.setVisible(true);
                adminPage.requestFocus();
            } else if (user.teacher == 1) {
                Teacher teacherPage = new Teacher(user);
                teacherPage.setVisible(true);
            } else {
                Student studentPage = new Student(user);
                studentPage.setVisible(true);
            }
        });

        dispose();
    }





    private User getAuthenticatedUser(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.name = resultSet.getString("name");
                        user.email = resultSet.getString("email");
                        user.teacher = resultSet.getInt("teacher");
                        user.admin = resultSet.getInt("admin");
                        user.password = resultSet.getString("password");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user != null) {

        } else {
            System.out.println("Authentication cancelled");
        }
    }
}