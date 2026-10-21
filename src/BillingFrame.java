import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BillingFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField patientIdF, amountF, descF;

    public BillingFrame() {
        setTitle("Billing Management");
        setSize(820, 540);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Generate Bill"));

        form.add(new JLabel("Patient ID:"));   patientIdF = new JTextField(); form.add(patientIdF);
        form.add(new JLabel("Amount (Rs):"));  amountF    = new JTextField(); form.add(amountF);
        form.add(new JLabel("Description:")); descF      = new JTextField(); form.add(descF);

        JButton addBtn = new JButton("Generate Bill");
        addBtn.addActionListener(e -> addBill());
        form.add(addBtn);
        form.add(new JLabel("")); form.add(new JLabel("")); form.add(new JLabel(""));

        model = new DefaultTableModel(
                new String[]{"Bill ID","Patient ID","Amount","Description","Paid","Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        loadBills();

        add(form,                   BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadBills() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery("SELECT * FROM billing ORDER BY id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getInt("patient_id"),
                        "Rs " + rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getBoolean("paid") ? "Yes" : "No",
                        rs.getString("bill_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addBill() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO billing (patient_id, amount, description) VALUES (?,?,?)");
            stmt.setInt(1, Integer.parseInt(patientIdF.getText().trim()));
            stmt.setDouble(2, Double.parseDouble(amountF.getText().trim()));
            stmt.setString(3, descF.getText().trim());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bill generated!");
            patientIdF.setText(""); amountF.setText(""); descF.setText("");
            loadBills();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Patient ID and Amount must be numbers!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}