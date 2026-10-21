import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame(String role, String userName) {
        setTitle("Dashboard — " + userName);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(700, 55));

        JLabel appName = new JLabel("   Hospital Management System");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel userInfo = new JLabel(userName + "  |  " + role + "   ");
        userInfo.setForeground(Color.WHITE);

        header.add(appName,  BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        JPanel menu = new JPanel(new GridLayout(2, 2, 20, 20));
        menu.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton btnPatients     = makeBtn("Patients",     new Color(39,  174, 96));
        JButton btnAppointments = makeBtn("Appointments", new Color(142, 68, 173));
        JButton btnBilling      = makeBtn("Billing",      new Color(230, 126, 34));
        JButton btnLogout       = makeBtn("Logout",       new Color(192, 57,  43));

        btnPatients    .addActionListener(e -> new PatientFrame());
        btnAppointments.addActionListener(e -> new AppointmentFrame());
        btnBilling     .addActionListener(e -> new BillingFrame());
        btnLogout      .addActionListener(e -> { new LoginFrame(); dispose(); });

        if (role.equals("STAFF")) {
            btnBilling.setEnabled(false);
            btnBilling.setText("Billing (No Access)");
        }

        menu.add(btnPatients);
        menu.add(btnAppointments);
        menu.add(btnBilling);
        menu.add(btnLogout);

        add(header, BorderLayout.NORTH);
        add(menu,   BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton makeBtn(String label, Color color) {
        JButton btn = new JButton(label);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }
}