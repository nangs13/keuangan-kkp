package com.kkp.keuangan.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.model.ModelPembelian;
import com.kkp.keuangan.model.ModelPembelianDetail;
import com.kkp.keuangan.swing.ScrollBar;

public class FormPembelian extends JPanel {

    // ==== HEADER ====
    private JTextField txtKode, txtPoStatus, txtReturStatus, txtTanggalPembelian, txtTanggalDeadline;
    private JComboBox<SupplierItem> cbSupplier;
    private JComboBox<String> cbMetodePembayaran;
    private JTextArea taRemark;

    // ==== TABLE ====
    private JTable table;
    private DefaultTableModel model;

    // ==== BUTTON ====
    private JButton btnTambahItem, btnHapusItem, btnSimpan, btnRefreshSupplier;
    private JLabel lblGrandTotal;

    private static final String DB_URL = "jdbc:sqlite:pos_app.db";
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public FormPembelian() {
        initComponents();
        loadSuppliers();
        generateHeaderDefaults();
        calcTotal();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // =========================================================
        // 🧩 TITLE
        // =========================================================
        JLabel lblTitle = new JLabel("Form Pembelian");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle, BorderLayout.WEST);

        // =========================================================
        // 🧩 HEADER FORM
        // =========================================================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtKode = new JTextField();
        txtKode.setUI(new RTextFieldUI());
        txtPoStatus = new JTextField();
        txtPoStatus.setUI(new RTextFieldUI());
        txtReturStatus = new JTextField();
        txtReturStatus.setUI(new RTextFieldUI());
        txtTanggalPembelian = new JTextField();
        txtTanggalPembelian.setUI(new RTextFieldUI());
        txtTanggalDeadline = new JTextField();
        txtTanggalDeadline.setUI(new RTextFieldUI());

        cbSupplier = new JComboBox<>();
        cbSupplier.setUI(new RComboBoxUI());
        cbMetodePembayaran = new JComboBox<>(new String[] { "Kas Kecil", "Transfer Bank", "Cash" });
        cbMetodePembayaran.setUI(new RComboBoxUI());

        taRemark = new JTextArea(3, 20);
        taRemark.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Kode Pembelian"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtKode, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("PO Status"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtPoStatus, gbc);

        gbc.gridx = 4;
        formPanel.add(new JLabel("Retur Status"), gbc);
        gbc.gridx = 5;
        formPanel.add(txtReturStatus, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tanggal Pembelian"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTanggalPembelian, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Tanggal Deadline"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtTanggalDeadline, gbc);

        gbc.gridx = 4;
        formPanel.add(new JLabel("Supplier"), gbc);
        gbc.gridx = 5;
        formPanel.add(cbSupplier, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Metode Pembayaran"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbMetodePembayaran, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Remark"), gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 3;
        JScrollPane spRemark = new JScrollPane(taRemark);
        formPanel.add(spRemark, gbc);
        gbc.gridwidth = 1;

        // =========================================================
        // 🧩 TABLE DETAIL
        // =========================================================
        model = new DefaultTableModel(
                new Object[] { "No", "Nama Barang", "Qty", "Satuan", "Harga/Unit", "Total", "Aksi" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2 || c == 3 || c == 4 || c == 6;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setGridColor(new Color(240, 240, 240));
        model.addTableModelListener(e -> calcTotal());

        // Button Renderer
        TableColumn aksiColumn = table.getColumnModel().getColumn(6);
        aksiColumn.setCellRenderer(new ButtonRenderer());
        aksiColumn.setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane spTable = new JScrollPane(table);
        spTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getViewport().setBackground(Color.WHITE);

        // =========================================================
        // 🧩 FOOTER (BUTTONS + TOTAL)
        // =========================================================
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblGrandTotal = new JLabel("0.00");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnRefreshSupplier = uiBtn("Refresh Supplier");
        btnTambahItem = uiBtn("Tambah Item");
        btnHapusItem = uiBtn("Hapus Item");
        btnSimpan = uiBtn("Simpan");

        btnRefreshSupplier.addActionListener(e -> loadSuppliers());
        btnTambahItem.addActionListener(e -> addItem());
        btnHapusItem.addActionListener(e -> removeItem());
        btnSimpan.addActionListener(e -> save());

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(new JLabel("Grand Total: "), gbc);

        gbc.gridx = 1;
        bottomPanel.add(lblGrandTotal, gbc);

        gbc.gridx = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnRefreshSupplier);
        buttonPanel.add(btnTambahItem);
        buttonPanel.add(btnHapusItem);
        buttonPanel.add(btnSimpan);
        bottomPanel.add(buttonPanel, gbc);

        // =========================================================
        // 🧩 ADD TO MAIN PANEL
        // =========================================================
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.NORTH);
        add(spTable, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // =========================================================
    // 🧩 UTILITIES & EVENT HANDLERS
    // =========================================================
    private JButton uiBtn(String text) {
        JButton b = new JButton(text);
        b.setUI(new RButtonUI());
        return b;
    }

    private void generateHeaderDefaults() {
        txtKode.setText(ModelPembelian.generateKode());
        txtPoStatus.setText("OPEN");
        txtReturStatus.setText("NONE");
        txtTanggalPembelian.setText(df.format(new Date()));
        txtTanggalDeadline.setText(df.format(new Date()));
        taRemark.setText("");
    }

    private void loadSuppliers() {
        cbSupplier.removeAllItems();
        cbSupplier.addItem(new SupplierItem(0, "Pilih Supplier"));
        try (Connection c = DriverManager.getConnection(DB_URL);
                PreparedStatement ps = c.prepareStatement("SELECT id, nama FROM supplier ORDER BY nama");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbSupplier.addItem(new SupplierItem(rs.getInt("id"), rs.getString("nama")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat supplier: " + e.getMessage());
        }
    }

    private void addItem() {
        AddItemDialog d = new AddItemDialog(SwingUtilities.getWindowAncestor(this));
        d.setVisible(true);
        if (!d.isCancelled()) {
            model.addRow(new Object[] {
                    model.getRowCount() + 1,
                    d.getNamaBarang(),
                    d.getQty(),
                    d.getSatuan(),
                    d.getHargaUnit(),
                    d.getQty() * d.getHargaUnit(),
                    "Hapus"
            });
            calcTotal();
        }
    }

    private void removeItem() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            calcTotal();
        }
    }

    private void removeItem(int row) {
        if (row >= 0 && row < model.getRowCount()) {
            model.removeRow(row);
            calcTotal();
        }
    }

    private void calcTotal() {
        double sum = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                double qty = Double.parseDouble(model.getValueAt(i, 2).toString());
                double harga = Double.parseDouble(model.getValueAt(i, 4).toString());
                double total = qty * harga;
                model.setValueAt(total, i, 5);
                sum += total;
            } catch (Exception ignored) {
            }
        }
        lblGrandTotal.setText(String.format("%.2f", sum));
    }

    private void save() {
        SupplierItem s = (SupplierItem) cbSupplier.getSelectedItem();
        if (s == null || s.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih supplier terlebih dahulu!");
            return;
        }
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada item!");
            return;
        }
        try {
            ModelPembelian p = new ModelPembelian();
            p.setKode(txtKode.getText());
            p.setTanggalPembelian(txtTanggalPembelian.getText());
            p.setTanggalDeadline(txtTanggalDeadline.getText());
            p.setSupplierId(s.getId());
            p.setMetodePembayaran(cbMetodePembayaran.getSelectedItem().toString());
            p.setTotal(Double.parseDouble(lblGrandTotal.getText()));
            p.setPoStatus(txtPoStatus.getText());
            p.setReturStatus(txtReturStatus.getText());
            p.setRemark(taRemark.getText());

            int id = p.insert();
            for (int i = 0; i < model.getRowCount(); i++) {
                String nama = model.getValueAt(i, 1).toString();
                double qty = Double.parseDouble(model.getValueAt(i, 2).toString());
                String satuan = model.getValueAt(i, 3).toString();
                double harga = Double.parseDouble(model.getValueAt(i, 4).toString());
                double total = qty * harga;
                ModelPembelianDetail det = new ModelPembelianDetail(0, id, nama, qty, satuan, harga, total);
                det.insert();
            }
            JOptionPane.showMessageDialog(this, "Pembelian berhasil disimpan!");
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    private void resetForm() {
        generateHeaderDefaults();
        model.setRowCount(0);
        loadSuppliers();
        calcTotal();
    }

    // ==== INNER CLASSES ====
    private static class SupplierItem {
        private final int id;
        private final String name;

        SupplierItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("Hapus");
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return this;
        }
    }

    private static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int row;
        private final FormPembelian parent;

        public ButtonEditor(JCheckBox check, FormPembelian parent) {
            super(check);
            this.parent = parent;
            button = new JButton("Hapus");
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            row = r;
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked)
                parent.removeItem(row);
            clicked = false;
            return "Hapus";
        }
    }

    private static class AddItemDialog extends JDialog {
        private boolean cancelled = true;
        private JTextField tfNama, tfQty, tfSatuan, tfHarga;

        public AddItemDialog(Window owner) {
            super(owner, "Tambah Item", ModalityType.APPLICATION_MODAL);
            setSize(360, 220);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));

            JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));
            p.add(new JLabel("Nama Barang:"));
            tfNama = new JTextField();
            p.add(tfNama);
            p.add(new JLabel("Qty:"));
            tfQty = new JTextField("1");
            p.add(tfQty);
            p.add(new JLabel("Satuan:"));
            tfSatuan = new JTextField("pcs");
            p.add(tfSatuan);
            p.add(new JLabel("Harga:"));
            tfHarga = new JTextField("0");
            p.add(tfHarga);
            add(p, BorderLayout.CENTER);

            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Batal");
            ok.addActionListener(e -> {
                if (!tfNama.getText().trim().isEmpty()) {
                    cancelled = false;
                    setVisible(false);
                }
            });
            cancel.addActionListener(e -> setVisible(false));

            JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bp.add(ok);
            bp.add(cancel);
            add(bp, BorderLayout.SOUTH);
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public String getNamaBarang() {
            return tfNama.getText().trim();
        }

        public double getQty() {
            return Double.parseDouble(tfQty.getText().trim());
        }

        public String getSatuan() {
            return tfSatuan.getText().trim();
        }

        public double getHargaUnit() {
            return Double.parseDouble(tfHarga.getText().trim());
        }
    }
}
