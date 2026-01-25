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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.dao.PembelianDAO;
import com.kkp.keuangan.backend.dao.SupplierDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.backend.model.ModelPembelian;
import com.kkp.keuangan.backend.model.ModelPembelianDetail;
import com.kkp.keuangan.backend.model.ModelPenjualanDetail;
import com.kkp.keuangan.backend.model.ModelProduk;
import com.kkp.keuangan.backend.model.ModelSupplier;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.form.dialog.DialogPilihProduk;
import com.kkp.keuangan.swing.ScrollBar;
import com.toedter.calendar.JDateChooser;

public class FormPembelian extends JPanel {

    // ==== HEADER ====
    private JDateChooser datePembelian;
    private JComboBox<ModelSupplier> cbSupplier;
    private JComboBox<ModelCoa> cbCo;
    private JTextArea taRemark;

    // ==== TABLE ====
    private JTable table;
    private DefaultTableModel model;

    // ==== BUTTON ====
    private JButton btnTambahItem, btnHapusItem, btnSimpan, btnRefreshSupplier;
    private JLabel lblGrandTotal;

    // ==== STATE ====
    private boolean isUpdating = false;
    private List<ModelProduk> produkList = new ArrayList<>();

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public FormPembelian() {
        initComponents();
        loadSuppliers();
        generateHeaderDefaults();

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

        datePembelian = new JDateChooser();
        datePembelian.setDateFormatString("yyyy-MM-dd");
        datePembelian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        datePembelian.setPreferredSize(new java.awt.Dimension(180, 28));

        cbSupplier = new JComboBox<>();
        cbSupplier.setUI(new RComboBoxUI());
        cbCo = new JComboBox<>();
        cbCo.setUI(new RComboBoxUI());

        taRemark = new JTextArea(3, 20);
        taRemark.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // Row 1 (Tanggal Pembelian, Supplier)
        gbc.gridx = 0;
        gbc.gridy = 0; // mulai dari 0
        formPanel.add(new JLabel("Tanggal Pembelian"), gbc);
        gbc.gridx = 1;
        formPanel.add(datePembelian, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Supplier"), gbc);
        gbc.gridx = 3;
        formPanel.add(cbSupplier, gbc);

        // Row 2 (Metode Pembayaran, Remark)
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Metode Pembayaran"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbCo, gbc);

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
                new Object[] { "No", "Nama Barang", "Kategori", "Qty", "Harga/Unit", "Total", "Aksi" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                // hanya kolom Qty(3), Harga(4), Aksi(6) yang editable
                return c == 3 || c == 4 ||  c == 6;
            }
        };
        model.addTableModelListener(e -> hitungTotal());

        // buat table dengan model
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionForeground(new Color(0,0,0));
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (table.getColumnCount() > 0) {
            TableColumn col0 = table.getColumnModel().getColumn(0);
            col0.setMinWidth(0);
            col0.setMaxWidth(0);
            col0.setPreferredWidth(0);
        }

        // Button Renderer/Editor (kolom "Aksi" index 6)
        if (table.getColumnCount() > 6) {
            TableColumn aksiColumn = table.getColumnModel().getColumn(6);
            aksiColumn.setCellRenderer(new ButtonRenderer());
            aksiColumn.setCellEditor(new ButtonEditor(new JCheckBox(), this));
        }

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
        btnTambahItem.addActionListener(e -> pilihProduk());
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
        loadParentCombo();
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
        datePembelian.setDate(null);
        taRemark.setText("");
    }

    private void loadSuppliers() {
        cbSupplier.removeAllItems();
        List<ModelSupplier> list = SupplierDAO.findAll();
        cbSupplier.addItem(null);
        for (ModelSupplier c : list) {
            cbSupplier.addItem(c);
        }
    }

    private void loadParentCombo() {
        cbCo.removeAllItems();
        List<ModelCoa> list = CoaDAO.findAllByCode("101-01");
        cbCo.addItem(null);
        for (ModelCoa c : list) {
            cbCo.addItem(c);
        }
    }

    private void pilihProduk() {
        DialogPilihProduk dialog =
            new DialogPilihProduk(SwingUtilities.getWindowAncestor(this), true);
        dialog.setVisible(true);

        var produk = dialog.getSelectedProduk();
        produkList.add(produk);
        if (produk != null) {
            model.addRow(new Object[]{
                model.getRowCount() + 1,
                produk.getNama(),
                produk.getKategori(),
                1,
                produk.getHarga(),
                1 * produk.getHarga(),
                "Hapus"
            });
            hitungTotal();
        }
    }

    private void hitungTotal() {
        if (isUpdating) return;
        isUpdating = true;

        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
                double harga = Double.parseDouble(model.getValueAt(i, 4).toString());
                produkList.get(i).setStok(qty);
                produkList.get(i).setHarga(harga);
                double subtotal = qty * harga;
                model.setValueAt(subtotal, i, 5);
                total += subtotal;
            } catch (Exception e) {
                // abaikan baris yang belum lengkap
            }
        }
        lblGrandTotal.setText(String.format("%.2f", total));

        isUpdating = false;
    }

    private void removeItem() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih item yang ingin dihapus!");
            return;
        }

        removeItem(selectedRow);
    }

    private void removeItem(int selectedRow) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus item ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            produkList.remove(selectedRow);
            model.removeRow(selectedRow);
            hitungTotal();
            JOptionPane.showMessageDialog(this, "Item berhasil dihapus!");
        }
    }

    private void save() {
        ModelSupplier s = (ModelSupplier) cbSupplier.getSelectedItem();
        if (s == null || s.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih supplier terlebih dahulu!");
            return;
        }
        if (produkList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada item!");
            return;
        }
        try {
            Date selectedDate = datePembelian.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih tanggal pembelian!");
                return;
            }
            List<ModelPembelianDetail> details = new ArrayList<>();

            for (int i = 0; i < produkList.size(); i++) {
                ModelProduk produk = produkList.get(i);
                int produkId = produk.getId();
                double qty = produk.getStok();
                double harga = produk.getHarga();
                double total = qty * harga;

                details.add(new ModelPembelianDetail(
                    0,
                    0,
                    produkId,
                    qty,
                    "",
                    harga,
                    total));
            }

            ModelPembelian p = new ModelPembelian();
            
            p.setTanggalPembelian(df.format(selectedDate));
            p.setSupplierId(s.getId());
            p.setCoaId(((ModelCoa) cbCo.getSelectedItem()).getId());
            p.setTotal(Double.parseDouble(lblGrandTotal.getText()));
            p.setRemark(taRemark.getText());
            p.setDetailList(details);
            PembelianDAO dao = new PembelianDAO();
            
            int id = dao.insert(p);
            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
                generateHeaderDefaults();
                model.setRowCount(0);
                loadSuppliers();
                hitungTotal();
            } else {
                throw new Exception("Failed to save data");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
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
}
