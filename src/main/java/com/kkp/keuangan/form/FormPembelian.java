package com.kkp.keuangan.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.model.ModelPembelian;
import com.kkp.keuangan.model.ModelPembelianDetail;

public class FormPembelian extends JFrame {
    private JTable headerTable;
    private JTable detailTable;
    private DefaultTableModel headerModel;
    private DefaultTableModel detailModel;
    private JComboBox<SupplierItem> cbSupplier;
    private JButton btnTambahItem, btnSimpan, btnHapusItem, btnRefreshSupplier;
    private JLabel lblInfo;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    // sesuaikan path DB jika diperlukan
    private static final String DB_URL = "jdbc:sqlite:pos_app.db";

    public FormPembelian() {
        super("Form Pembelian");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);
        initComponents();
        loadSuppliers();
        generateHeaderRow();
    }

    private void initComponents() {
        // Header table (single-row) columns:
        String[] headerCols = new String[] {
                "Kode Pembelian", "PO Status", "Retur Status",
                "Tanggal Pembelian", "Tanggal Deadline", "Supplier", "Metode Pembayaran"
        };
        headerModel = new DefaultTableModel(null, headerCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing for kode/po/retur (0,1,2)
                if (column <= 2) return false;
                // allow editing tanggal pembelian, deadline, supplier (but we will use a combobox for supplier)
                return true;
            }
        };
        headerTable = new JTable(headerModel);
        headerTable.setRowHeight(24);

        // Detail table columns:
        String[] detailCols = new String[] { "Nama Barang", "Quantity", "Satuan", "Harga/Unit", "Total", "Aksi" };
        detailModel = new DefaultTableModel(null, detailCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing quantity (1), satuan (2) and harga/unit (3)
                return column == 1 || column == 2 || column == 3;
            }
        };
        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(24);

        // Supplier combobox will be displayed below but we'll store selection to headerTable's supplier column
        cbSupplier = new JComboBox<>();
        cbSupplier.addItem(new SupplierItem(0, "Pilih supplier terlebih dahulu"));
        cbSupplier.addActionListener(e -> {
            SupplierItem s = (SupplierItem) cbSupplier.getSelectedItem();
            if (s != null) {
                // set supplier name into header table (col 5)
                headerModel.setValueAt(s.getName(), 0, 5);
            }
        });

        // Buttons
        btnTambahItem = new JButton("Tambah Item");
        btnHapusItem = new JButton("Hapus Item Terpilih");
        btnSimpan = new JButton("Simpan Pembelian");
        btnRefreshSupplier = new JButton("Refresh Supplier");
        lblInfo = new JLabel(" ");

        btnTambahItem.addActionListener(e -> onTambahItem());
        btnHapusItem.addActionListener(e -> onHapusItem());
        btnSimpan.addActionListener(e -> onSimpan());
        btnRefreshSupplier.addActionListener(e -> loadSuppliers());

        // When user edits quantity or harga, recalc total
        detailModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && (col == 1 || col == 3)) {
                try {
                    Object qObj = detailModel.getValueAt(row, 1);
                    Object hObj = detailModel.getValueAt(row, 3);
                    double qty = qObj == null ? 0 : Double.parseDouble(qObj.toString());
                    double harga = hObj == null ? 0 : Double.parseDouble(hObj.toString());
                    double total = qty * harga;
                    detailModel.setValueAt(String.format("%.2f", total), row, 4);
                } catch (Exception ex) {
                    // ignore parse errors
                }
            }
        });

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(8,8));
        topPanel.setBorder(BorderFactory.createTitledBorder("Header Pembelian (tabel 1)"));
        JScrollPane headerScroll = new JScrollPane(headerTable);
        topPanel.add(headerScroll, BorderLayout.CENTER);

        JPanel supplierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        supplierPanel.add(new JLabel("Supplier:"));
        supplierPanel.add(cbSupplier);
        supplierPanel.add(btnRefreshSupplier);
        topPanel.add(supplierPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(8,8));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Detail Item (tabel 2)"));
        JScrollPane detailScroll = new JScrollPane(detailTable);
        bottomPanel.add(detailScroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(btnTambahItem);
        controls.add(btnHapusItem);
        controls.add(btnSimpan);
        bottomPanel.add(controls, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout(8,8));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(bottomPanel, BorderLayout.CENTER);
        getContentPane().add(lblInfo, BorderLayout.SOUTH);
    }

    private void generateHeaderRow() {
        // Create a single row with defaults
        String kode = ModelPembelian.generateKode();
        String poStatus = "OPEN";
        String returStatus = "NONE";
        String tglPembelian = df.format(new Date());
        String tglDeadline = df.format(new Date());
        String supplier = "Pilih supplier terlebih dahulu";
        String metode = "Tunai";

        headerModel.setRowCount(0);
        headerModel.addRow(new Object[] {
                kode, poStatus, returStatus, tglPembelian, tglDeadline, supplier, metode
        });
    }

    private void loadSuppliers() {
        // fetch suppliers directly from DB (table supplier expected)
        cbSupplier.removeAllItems();
        cbSupplier.addItem(new SupplierItem(0, "Pilih supplier terlebih dahulu"));
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            ps = conn.prepareStatement("SELECT id, nama FROM supplier ORDER BY nama");
            rs = ps.executeQuery();
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
            lblInfo.setText("Gagal load supplier: " + ex.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch(Exception e){}
            try { if (ps != null) ps.close(); } catch(Exception e){}
            try { if (conn != null) conn.close(); } catch(Exception e){}
        }
    }

    private void onTambahItem() {
        AddItemDialog d = new AddItemDialog(this);
        d.setVisible(true);
        if (!d.isCancelled()) {
            Object[] row = new Object[] {
                    d.getNamaBarang(),
                    d.getQty(),
                    d.getSatuan(),
                    d.getHargaUnit(),
                    String.format("%.2f", d.getQty() * d.getHargaUnit()),
                    "Hapus"
            };
            detailModel.addRow(row);
        }
    }

    private void onHapusItem() {
        int sel = detailTable.getSelectedRow();
        if (sel >= 0) {
            detailModel.removeRow(sel);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris item untuk dihapus.");
        }
    }

    private void onSimpan() {
        // Validate supplier
        SupplierItem s = (SupplierItem) cbSupplier.getSelectedItem();
        if (s == null || s.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih supplier terlebih dahulu.");
            return;
        }
        // Build ModelPembelian from header row
        try {
            Object kodeObj = headerModel.getValueAt(0, 0);
            Object tglBObj = headerModel.getValueAt(0, 3);
            Object tglDObj = headerModel.getValueAt(0, 4);
            Object metodeObj = headerModel.getValueAt(0, 6);

            String kode = kodeObj.toString();
            String tglPembelian = tglBObj.toString();
            String tglDeadline = tglDObj.toString();
            String metode = metodeObj.toString();

            // accumulate totals
            double grandTotal = 0;
            List<ModelPembelianDetail> items = new ArrayList<>();
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                String nama = String.valueOf(detailModel.getValueAt(i, 0));
                double qty = Double.parseDouble(String.valueOf(detailModel.getValueAt(i, 1)));
                String satuan = String.valueOf(detailModel.getValueAt(i, 2));
                double harga = Double.parseDouble(String.valueOf(detailModel.getValueAt(i, 3)));
                double total = qty * harga;
                grandTotal += total;
                ModelPembelianDetail detail = new ModelPembelianDetail(0, 0, nama, qty, satuan, harga, total);
                items.add(detail);
            }

            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada item. Tambahkan minimal 1 item.");
                return;
            }

            ModelPembelian pembelian = new ModelPembelian();
            pembelian.setKode(kode);
            pembelian.setTanggalPembelian(tglPembelian);
            pembelian.setTanggalDeadline(tglDeadline);
            pembelian.setSupplierId(s.getId());
            pembelian.setMetodePembayaran(metode);
            pembelian.setTotal(grandTotal);
            pembelian.setPoStatus("OPEN");
            pembelian.setReturStatus("NONE");

            // Save using model (transaction)
            int pembelianId = pembelian.insert();
            for (ModelPembelianDetail det : items) {
                det.setPembelianId(pembelianId);
                det.insert();
            }

            JOptionPane.showMessageDialog(this, "Transaksi pembelian berhasil disimpan (id=" + pembelianId + ").");
            // reset form
            generateHeaderRow();
            detailModel.setRowCount(0);
            loadSuppliers();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Format angka salah pada detail item: " + nfe.getMessage());
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Gagal simpan ke DB: " + sqle.getMessage());
            sqle.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Simple supplier holder for combobox
    private static class SupplierItem {
        private final int id;
        private final String name;
        public SupplierItem(int id, String name) {
            this.id = id; this.name = name;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override
        public String toString() { return name; }
    }

    // Dialog to add item
    private static class AddItemDialog extends JDialog {
        private boolean cancelled = true;
        private JTextField tfNama, tfQty, tfSatuan, tfHarga;
        public AddItemDialog(Frame owner) {
            super(owner, "Tambah Item", true);
            setSize(380,220);
            setLocationRelativeTo(owner);
            init();
        }
        private void init() {
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
                // validate
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

    // For quick run/testing
    public static void main(String[] args) {
        // NOTE: kita tidak memanggil DBHelper di sini karena kamu tidak menggunakan DBHelper.
        // Pastikan pos_app.db sudah ada dan memiliki tabel supplier, pembelian, pembelian_detail.
        SwingUtilities.invokeLater(() -> {
            new FormPembelian().setVisible(true);
        });
    }
}
