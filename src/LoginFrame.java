import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hospital Management System - Login");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("  Hospital Management System");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(new Color(41, 128, 185));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel(""));
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(41, 128, 185));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role     = rs.getString("role");
                String fullName = rs.getString("full_name");
                JOptionPane.showMessageDialog(this, "Welcome, " + fullName + "!");
                new DashboardFrame(role, fullName);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Wrong username or password!",                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}