package com.kkp.keuangan.form;

import com.kkp.keuangan.model.ModelPembelian;
import com.kkp.keuangan.model.ModelPembelianDetail;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormPembelian extends JPanel {
    private JTextField tfKode, tfPoStatus, tfReturStatus, tfTanggalPembelian, tfTanggalDeadline;
    private JComboBox<SupplierItem> cbSupplier;
    private JComboBox<String> cbMetodePembayaran;
    private JTextArea taRemark;
    private JButton btnTambahItem, btnHapusItem, btnSimpan, btnRefreshSupplier;
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JLabel lblGrandTotal, lblInfo;

    private static final String DB_URL = "jdbc:sqlite:pos_app.db";
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public FormPembelian() {
        initComponents();
        generateHeaderDefaults();
        loadSuppliers();
        updateDetailState();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(12,12,12,12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        JLabel title = new JLabel("Buat Pembelian");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, gbc);

        // Row 1: Kode, PO Status, Retur Status
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        tfKode = makeReadonlyField();
        tfPoStatus = makeReadonlyField();
        tfReturStatus = makeReadonlyField();

        addLabeledComponent("Kode pembelian", tfKode, 0,1,2);
        addLabeledComponent("Purchase Order Status", tfPoStatus, 2,1,2);
        addLabeledComponent("Retur Status", tfReturStatus, 4,1,2);

        // Row 2: tanggal pembelian, deadline, supplier
        tfTanggalPembelian = new JTextField();
        tfTanggalDeadline = new JTextField();
        cbSupplier = new JComboBox<>();
        cbSupplier.addItem(new SupplierItem(0, "Pilih supplier terlebih dahulu"));
        cbSupplier.addActionListener(e -> {
            SupplierItem s = (SupplierItem) cbSupplier.getSelectedItem();
            if (s != null) {
                // if supplier selected (id !=0) enable detail area
                updateDetailState();
            }
        });

        addLabeledComponent("Tanggal Pembelian", tfTanggalPembelian, 0,2,2);
        addLabeledComponent("Tanggal Deadline", tfTanggalDeadline, 2,2,2);
        addLabeledComponent("Supplier", cbSupplier, 4,2,2);

        // Row 3: Metode Pembayaran, Remark (remark takes multiple columns)
        cbMetodePembayaran = new JComboBox<>(new String[] {"Kas Kecil", "Transfer Bank", "Cash"});
        addLabeledComponent("Metode Pembayaran", cbMetodePembayaran, 0,3,2);

        taRemark = new JTextArea(3, 40);
        JScrollPane spRemark = new JScrollPane(taRemark);
        gbc.gridx = 2; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        add(spRemark, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Row 4: detail table label
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 6;
        JLabel lblDetail = new JLabel("Detail Item");
        lblDetail.setFont(lblDetail.getFont().deriveFont(Font.BOLD, 13f));
        add(lblDetail, gbc);
        gbc.gridwidth = 1;

        // Row 5: detail table
        detailModel = new DefaultTableModel(new Object[] {"No", "Nama Barang", "Qty", "Satuan", "Harga/Unit", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                // allow qty, satuan, harga editable (2,3,4)
                return column == 2 || column == 3 || column == 4;
            }
        };
        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(24);

        // recalc total when qty or harga edited
        detailModel.addTableModelListener(e -> recalcTotals());

        JScrollPane spTable = new JScrollPane(detailTable);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 6; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 1;
        add(spTable, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0; gbc.weightx = 0;
        gbc.gridwidth = 1;

        // Row 6: info & buttons
        lblInfo = new JLabel("Pilih supplier terlebih dahulu");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        add(lblInfo, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8,0));
        btnTambahItem = new JButton("Tambah Item");
        btnHapusItem = new JButton("Hapus Item");
        btnRefreshSupplier = new JButton("Refresh Supplier");
        btnSimpan = new JButton("Simpan");

        btnTambahItem.addActionListener(e -> onTambahItem());
        btnHapusItem.addActionListener(e -> onHapusItem());
        btnRefreshSupplier.addActionListener(e -> loadSuppliers());
        btnSimpan.addActionListener(e -> onSimpan());

        btnPanel.add(btnRefreshSupplier);
        btnPanel.add(btnTambahItem);
        btnPanel.add(btnHapusItem);
        btnPanel.add(btnSimpan);

        gbc.gridx = 3; gbc.gridy = 6; gbc.gridwidth = 3;
        add(btnPanel, gbc);
        gbc.gridwidth = 1;

        // Row 7: Grand total
        lblGrandTotal = new JLabel("0.00");
        lblGrandTotal.setFont(lblGrandTotal.getFont().deriveFont(Font.BOLD, 14f));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("Total: "));
        totalPanel.add(lblGrandTotal);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 6;
        add(totalPanel, gbc);
    }

    private JTextField makeReadonlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(Color.WHITE);
        return t;
    }

    private void addLabeledComponent(String label, Component comp, int gridx, int gridy, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gridx; gbc.gridy = gridy; gbc.gridwidth = 1;
        add(new JLabel(label), gbc);
        gbc.gridx = gridx; gbc.gridy = gridy+1; gbc.gridwidth = width;
        add(comp, gbc);
    }

    private void generateHeaderDefaults() {
        tfKode.setText(ModelPembelian.generateKode());
        tfPoStatus.setText("OPEN");
        tfReturStatus.setText("NONE");
        tfTanggalPembelian.setText(df.format(new Date()));
        tfTanggalDeadline.setText(df.format(new Date()));
        taRemark.setText("");
    }

    private void updateDetailState() {
        SupplierItem s = (SupplierItem) cbSupplier.getSelectedItem();
        boolean enabled = s != null && s.getId() != 0;
        detailTable.setEnabled(enabled);
        btnTambahItem.setEnabled(enabled);
        btnHapusItem.setEnabled(enabled && detailModel.getRowCount() > 0);
        lblInfo.setText(enabled ? "Tambah item" : "Pilih supplier terlebih dahulu");
        if (!enabled) {
            // clear detail table text (show placeholder row)
            detailModel.setRowCount(0);
        }
    }

    private void loadSuppliers() {
        // load suppliers from DB
        cbSupplier.removeAllItems();
        cbSupplier.addItem(new SupplierItem(0, "Pilih supplier terlebih dahulu"));
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT id, nama FROM supplier ORDER BY nama");
             ResultSet rs = ps.executeQuery()) {
            boolean any = false;
            while (rs.next()) {
                any = true;
                cbSupplier.addItem(new SupplierItem(rs.getInt("id"), rs.getString("nama")));
            }
            if (!any) {
                lblInfo.setText("Tidak ada supplier di database. Silakan tambahkan supplier terlebih dahulu.");
            } else {
                lblInfo.setText("Supplier ter-load.");
            }
        } catch (Exception ex) {
            lblInfo.setText("Gagal memuat supplier: " + ex.getMessage());
        }
        // ensure detail state reflects selection
        updateDetailState();
    }

    private void onTambahItem() {
        AddItemDialog d = new AddItemDialog(SwingUtilities.getWindowAncestor(this));
        d.setVisible(true);
        if (!d.isCancelled()) {
            int no = detailModel.getRowCount() + 1;
            Object[] row = new Object[] {
                    no,
                    d.getNamaBarang(),
                    d.getQty(),
                    d.getSatuan(),
                    d.getHargaUnit(),
                    String.format("%.2f", d.getQty() * d.getHargaUnit())
            };
            detailModel.addRow(row);
            recalcTotals();
            updateDetailState();
        }
    }

    private void onHapusItem() {
        int sel = detailTable.getSelectedRow();
        if (sel >= 0) {
            detailModel.removeRow(sel);
            // re-number
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                detailModel.setValueAt(i+1, i, 0);
            }
            recalcTotals();
            updateDetailState();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris item untuk dihapus.");
        }
    }

    private void recalcTotals() {
        double grand = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            try {
                Object q = detailModel.getValueAt(i, 2);
                Object h = detailModel.getValueAt(i, 4);
                double qty = q == null ? 0 : Double.parseDouble(q.toString());
                double harga = h == null ? 0 : Double.parseDouble(h.toString());
                double total = qty * harga;
                detailModel.setValueAt(String.format("%.2f", total), i, 5);
                grand += total;
            } catch (Exception ex) {
                // ignore parse
            }
        }
        lblGrandTotal.setText(String.format("%.2f", grand));
    }

    private void onSimpan() {
        SupplierItem s = (SupplierItem) cbSupplier.getSelectedItem();
        if (s == null || s.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih supplier terlebih dahulu.");
            return;
        }
        if (detailModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada item pembelian.");
            return;
        }

        try {
            ModelPembelian pembelian = new ModelPembelian();
            pembelian.setKode(tfKode.getText());
            pembelian.setTanggalPembelian(tfTanggalPembelian.getText());
            pembelian.setTanggalDeadline(tfTanggalDeadline.getText());
            pembelian.setSupplierId(s.getId());
            pembelian.setMetodePembayaran(cbMetodePembayaran.getSelectedItem().toString());
            pembelian.setTotal(Double.parseDouble(lblGrandTotal.getText()));
            pembelian.setPoStatus(tfPoStatus.getText());
            pembelian.setReturStatus(tfReturStatus.getText());
            pembelian.setRemark(taRemark.getText());

            int pembelianId = pembelian.insert(); // insert header

            // insert details
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                String nama = String.valueOf(detailModel.getValueAt(i, 1));
                double qty = Double.parseDouble(String.valueOf(detailModel.getValueAt(i, 2)));
                String satuan = String.valueOf(detailModel.getValueAt(i, 3));
                double harga = Double.parseDouble(String.valueOf(detailModel.getValueAt(i, 4)));
                double total = qty * harga;
                ModelPembelianDetail det = new ModelPembelianDetail(0, pembelianId, nama, qty, satuan, harga, total);
                det.insert();
            }

            // You can call CoaPembelian to generate accounting entries if needed
            JOptionPane.showMessageDialog(this, "Pembelian tersimpan (id=" + pembelianId + ").");
            // reset form
            generateHeaderDefaults();
            detailModel.setRowCount(0);
            loadSuppliers();
            recalcTotals();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan ke DB: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Simple supplier holder
    private static class SupplierItem {
        private final int id;
        private final String name;
        public SupplierItem(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public String toString() { return name; }
    }

    // Dialog to add item
    private static class AddItemDialog extends JDialog {
        private boolean cancelled = true;
        private JTextField tfNama, tfQty, tfSatuan, tfHarga;

        public AddItemDialog(Window owner) {
            super(owner, "Tambah Item", ModalityType.APPLICATION_MODAL);
            init();
        }

        private void init() {
            setSize(380,220);
            setLocationRelativeTo(getOwner());
            setLayout(new BorderLayout(8,8));
            JPanel p = new JPanel(new GridLayout(4,2,6,6));
            p.add(new JLabel("Nama Barang:")); tfNama = new JTextField(); p.add(tfNama);
            p.add(new JLabel("Quantity:")); tfQty = new JTextField("1"); p.add(tfQty);
            p.add(new JLabel("Satuan:")); tfSatuan = new JTextField("pcs"); p.add(tfSatuan);
            p.add(new JLabel("Harga/Unit:")); tfHarga = new JTextField("0"); p.add(tfHarga);
            add(p, BorderLayout.CENTER);
            JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Batal");
            btn.add(ok); btn.add(cancel);
            add(btn, BorderLayout.SOUTH);

            ok.addActionListener(e -> {
                if (tfNama.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama barang harus diisi.");
                    return;
                }
                try {
                    Double.parseDouble(tfQty.getText());
                    Double.parseDouble(tfHarga.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Format qty/harga tidak valid.");
                    return;
                }
                cancelled = false;
                setVisible(false);
            });

            cancel.addActionListener(e -> {
                cancelled = true;
                setVisible(false);
            });
        }

        public boolean isCancelled() { return cancelled; }
        public String getNamaBarang() { return tfNama.getText().trim(); }
        public double getQty() { try { return Double.parseDouble(tfQty.getText().trim()); } catch(Exception e) { return 0; } }
        public String getSatuan() { return tfSatuan.getText().trim(); }
        public double getHargaUnit() { try { return Double.parseDouble(tfHarga.getText().trim()); } catch(Exception e) { return 0; } }
    }
}