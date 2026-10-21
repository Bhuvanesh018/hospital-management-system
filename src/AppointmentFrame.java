import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AppointmentFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField patientIdF, doctorF, dateF, notesF;

    public AppointmentFrame() {
        setTitle("Appointment Management");
        setSize(820, 540);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Schedule Appointment"));

        form.add(new JLabel("Patient ID:"));        patientIdF = new JTextField(); form.add(patientIdF);
        form.add(new JLabel("Doctor Name:"));       doctorF    = new JTextField(); form.add(doctorF);
        form.add(new JLabel("Date (YYYY-MM-DD):")); dateF      = new JTextField(); form.add(dateF);
        form.add(new JLabel("Notes:"));             notesF     = new JTextField(); form.add(notesF);

        JButton addBtn = new JButton("Schedule Appointment");
        addBtn.addActionListener(e -> addAppointment());
        form.add(addBtn);
        form.add(new JLabel("")); form.add(new JLabel("")); form.add(new JLabel(""));

        model = new DefaultTableModel(
                new String[]{"ID","Patient ID","Doctor","Date","Status","Notes"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        loadAppointments();

        add(form,                   BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadAppointments() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery("SELECT * FROM appointments ORDER BY id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getInt("patient_id"),
                        rs.getString("doctor_name"), rs.getString("appointment_date"),
                        rs.getString("status"),      rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addAppointment() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO appointments (patient_id, doctor_name, appointment_date, notes) VALUES (?,?,?,?)");
            stmt.setInt(1, Integer.parseInt(patientIdF.getText().trim()));
            stmt.setString(2, doctorF.getText().trim());
            stmt.setString(3, dateF.getText().trim());
            stmt.setString(4, notesF.getText().trim());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment scheduled!");
            patientIdF.setText(""); doctorF.setText(""); dateF.setText(""); notesF.setText("");
            loadAppointments();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Patient ID must be a number!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}