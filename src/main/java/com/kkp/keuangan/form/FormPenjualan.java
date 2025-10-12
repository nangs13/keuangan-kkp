package com.kkp.keuangan.form;

import com.kkp.keuangan.backend.dao.PenjualanDAO;
import com.kkp.keuangan.backend.model.ModelPenjualan;
import com.kkp.keuangan.backend.model.ModelPenjualanDetail;
import com.kkp.keuangan.form.dialog.DialogPilihProduk;
import com.kkp.keuangan.component.uis.*;
import com.kkp.keuangan.swing.ScrollBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormPenjualan extends JPanel {

    private JTable tableDetail;
    private DefaultTableModel model;
    private JTextField txtTotal;
    private JButton btnTambahProduk, btnSimpan, btnBatal;
    private final PenjualanDAO dao = new PenjualanDAO();
    private boolean isUpdating = false;

    public FormPenjualan() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        // -------------------------
        // 🧩 TITLE
        // -------------------------
        JLabel lblTitle = new JLabel("Form Penjualan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(lblTitle, BorderLayout.WEST);

        // -------------------------
        // 🧩 TABLE DETAIL PENJUALAN
        // -------------------------
        model = new DefaultTableModel(new Object[]{"Produk ID", "Qty", "Harga Satuan", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // hanya kolom Qty (1) dan Harga Satuan (2) yang bisa diubah
                return column == 1 || column == 2;
            }
        };
        tableDetail = new JTable(model);
        model.addTableModelListener(e -> hitungTotal());
        tableDetail.setRowHeight(28);
        tableDetail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableDetail.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableDetail.getTableHeader().setBackground(new Color(230, 230, 230));
        tableDetail.setSelectionBackground(new Color(200, 220, 255));
        tableDetail.setGridColor(new Color(240, 240, 240));

        JScrollPane spTable = new JScrollPane(tableDetail);
        spTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getViewport().setBackground(Color.WHITE);

        // -------------------------
        // 🧩 BAGIAN BAWAH (TOTAL DAN BUTTON)
        // -------------------------
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTotal = new JLabel("Total Harga");
        txtTotal = new JTextField("0", 15);
        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setUI(new RTextFieldUI());

        btnTambahProduk = new JButton("Tambah Produk");
        btnTambahProduk.setUI(new RButtonUI());
        btnTambahProduk.addActionListener(e -> pilihProduk());

        btnSimpan = new JButton("Simpan Penjualan");
        btnSimpan.setUI(new RButtonUI());
        btnSimpan.addActionListener(e -> simpanPenjualan());

        btnBatal = new JButton("Batal");
        btnBatal.setUI(new RButtonUI());
        btnBatal.addActionListener(e -> resetForm());

        // layout bottom
        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(lblTotal, gbc);

        gbc.gridx = 1;
        bottomPanel.add(txtTotal, gbc);

        gbc.gridx = 2;
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButton.setOpaque(false);
        panelButton.add(btnTambahProduk);
        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);
        bottomPanel.add(panelButton, gbc);

        // -------------------------
        // 🧩 TAMBAH KE PANEL UTAMA
        // -------------------------
        add(topPanel, BorderLayout.NORTH);
        add(spTable, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // -------------------------
    // 📦 METHOD LOGIKA PENJUALAN
    // -------------------------
    private void pilihProduk() {
        DialogPilihProduk dialog =
            new DialogPilihProduk(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        var produk = dialog.getSelectedProduk();
        if (produk != null) {
            model.addRow(new Object[]{
                produk.getId(),
                1,
                produk.getHarga(),
                produk.getHarga()
            });
            hitungTotal();
        }
    }

    private void hitungTotal() {
        if (isUpdating) return; // cegah loop rekursif
        isUpdating = true;

        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int qty = Integer.parseInt(model.getValueAt(i, 1).toString());
                double harga = Double.parseDouble(model.getValueAt(i, 2).toString());
                double subtotal = qty * harga;
                model.setValueAt(subtotal, i, 3);
                total += subtotal;
            } catch (Exception e) {
                // abaikan baris yang belum lengkap
            }
        }
        txtTotal.setText(String.valueOf(total));

        isUpdating = false;
    }

    private void resetForm() {
        model.setRowCount(0);
        txtTotal.setText("0");
    }

    private void simpanPenjualan() {
        double total = 0;
        List<ModelPenjualanDetail> details = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int produkId = Integer.parseInt(model.getValueAt(i, 0).toString());
                int qty = Integer.parseInt(model.getValueAt(i, 1).toString());
                double harga = Double.parseDouble(model.getValueAt(i, 2).toString());
                double subtotal = qty * harga;
                model.setValueAt(subtotal, i, 3);
                total += subtotal;

                details.add(new ModelPenjualanDetail(produkId, qty, harga));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Pastikan semua data terisi dengan benar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        txtTotal.setText(String.valueOf(total));

        ModelPenjualan p = new ModelPenjualan();
        p.setTanggal(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        p.setTotalHarga(total);
        p.setDetailList(details);

        int id = dao.insert(p);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Penjualan berhasil disimpan!");
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan penjualan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
