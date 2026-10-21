import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PatientFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameF, ageF, genderF, phoneF;

    public PatientFrame() {
        setTitle("Patient Management");
        setSize(820, 540);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Add New Patient"));

        form.add(new JLabel("Name:"));   nameF   = new JTextField(); form.add(nameF);
        form.add(new JLabel("Age:"));    ageF    = new JTextField(); form.add(ageF);
        form.add(new JLabel("Gender:")); genderF = new JTextField(); form.add(genderF);
        form.add(new JLabel("Phone:"));  phoneF  = new JTextField(); form.add(phoneF);

        JButton addBtn    = new JButton("Add Patient");
        JButton deleteBtn = new JButton("Delete Selected");
        form.add(addBtn);
        form.add(deleteBtn);
        form.add(new JLabel("")); form.add(new JLabel(""));

        addBtn   .addActionListener(e -> addPatient());
        deleteBtn.addActionListener(e -> deletePatient());

        model = new DefaultTableModel(
                new String[]{"ID","Name","Age","Gender","Phone","Registered"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        loadPatients();

        add(form,                   BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadPatients() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery("SELECT * FROM patients ORDER BY id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addPatient() {
        if (nameF.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!"); return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO patients (name, age, gender, phone) VALUES (?,?,?,?)");
            stmt.setString(1, nameF.getText().trim());
            stmt.setInt(2, Integer.parseInt(ageF.getText().trim()));
            stmt.setString(3, genderF.getText().trim());
            stmt.setString(4, phoneF.getText().trim());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Patient added!");
            nameF.setText(""); ageF.setText(""); genderF.setText(""); phoneF.setText("");
            loadPatients();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deletePatient() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient first!"); return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this patient?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM patients WHERE id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            loadPatients();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}