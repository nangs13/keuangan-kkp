/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kkp.keuangan.form;
 
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RPanelUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.component.uis.RScrollBarUI;
/**
 *
 * @author User
 */
public class FormMutasikas extends JPanel {
 private JComboBox<String> cmbSumberDana;
    private JTextField txtJumlahSumber, txtTanggal, txtKeterangan;
    private JLabel lblTotalSumber;
    private JPanel panelTujuan;
    private JLabel lblTotalTujuan;
    private JTable tblHistori;
    private DefaultTableModel modelHistori;
    private java.util.List<TujuanRow> tujuanList = new ArrayList<>();
    private java.util.Map<String, String> coaMap = new java.util.HashMap<>();
  public FormMutasikas() {
  setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        // ===== Panel Atas (Tanggal & Keterangan) =====
        JPanel panelAtas = new JPanel(new GridLayout(1, 2, 10, 10));
        panelAtas.setOpaque(false);

        JPanel panelTanggal = new JPanel(new BorderLayout(4, 4));
        panelTanggal.setOpaque(false);
        JLabel lblTanggal = new JLabel("Tanggal:");
        lblTanggal.setFont(lblTanggal.getFont().deriveFont(Font.PLAIN, 12f));
        panelTanggal.add(lblTanggal, BorderLayout.NORTH);
        txtTanggal = new JTextField(java.time.LocalDate.now().toString());
        txtTanggal.setPreferredSize(new Dimension(120, 28));
        // apply custom textfield UI
        txtTanggal.setUI(new RTextFieldUI());
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);

        JPanel panelKet = new JPanel(new BorderLayout(4, 4));
        panelKet.setOpaque(false);
        JLabel lblKet = new JLabel("Keterangan:");
        lblKet.setFont(lblKet.getFont().deriveFont(Font.PLAIN, 12f));
        panelKet.add(lblKet, BorderLayout.NORTH);
        txtKeterangan = new JTextField();
        txtKeterangan.setUI(new RTextFieldUI());
        panelKet.add(txtKeterangan, BorderLayout.CENTER);

        panelAtas.add(panelTanggal);
        panelAtas.add(panelKet);

        // ===== Panel Sumber Dana =====
        JPanel panelSumber = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelSumber.setBorder(BorderFactory.createTitledBorder("Sumber Dana"));
        panelSumber.setOpaque(false);

        cmbSumberDana = new JComboBox<>();
        loadCoaData(cmbSumberDana);
        cmbSumberDana.setUI(new RComboBoxUI());
        cmbSumberDana.setPreferredSize(new Dimension(240, 30));

        txtJumlahSumber = new JTextField(10);
        txtJumlahSumber.setPreferredSize(new Dimension(120, 28));
        txtJumlahSumber.setUI(new RTextFieldUI());
        lblTotalSumber = new JLabel("Total Sumber: 0");

        txtJumlahSumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTotalSumber();
            }
        });

        panelSumber.add(new JLabel("Sumber:"));
        panelSumber.add(cmbSumberDana);
        panelSumber.add(new JLabel("Jumlah:"));
        panelSumber.add(txtJumlahSumber);
        panelSumber.add(lblTotalSumber);

        // ===== Panel Tujuan Dana =====
        JPanel panelTujuanWrap = new JPanel(new BorderLayout());
        panelTujuanWrap.setBorder(BorderFactory.createTitledBorder("Tujuan Dana"));
        panelTujuanWrap.setOpaque(false);

        panelTujuan = new JPanel();
        panelTujuan.setLayout(new BoxLayout(panelTujuan, BoxLayout.Y_AXIS));
        panelTujuan.setOpaque(false);

        // tinggi scroll lebih kecil agar tidak terlalu panjang
        JScrollPane scrollTujuan = new JScrollPane(panelTujuan);
        scrollTujuan.setPreferredSize(new Dimension(420, 56)); // compact
        scrollTujuan.setBorder(BorderFactory.createEmptyBorder());
        // apply custom scrollbar UI if available
        if (scrollTujuan.getVerticalScrollBar() != null) {
            scrollTujuan.getVerticalScrollBar().setUI(new RScrollBarUI());
        }
        if (scrollTujuan.getHorizontalScrollBar() != null) {
            scrollTujuan.getHorizontalScrollBar().setUI(new RScrollBarUI());
        }

        JButton btnTambahTujuan = new JButton("Tambah Tujuan");
        btnTambahTujuan.setUI(new RButtonUI());
        btnTambahTujuan.setPreferredSize(new Dimension(140, 30));
        lblTotalTujuan = new JLabel("Total Tujuan: 0");

        JPanel panelTujuanBottom = new JPanel(new BorderLayout(6, 6));
        panelTujuanBottom.setOpaque(false);
        panelTujuanBottom.add(btnTambahTujuan, BorderLayout.WEST);
        panelTujuanBottom.add(lblTotalTujuan, BorderLayout.EAST);

        panelTujuanWrap.add(scrollTujuan, BorderLayout.CENTER);
        panelTujuanWrap.add(panelTujuanBottom, BorderLayout.SOUTH);

        // ===== Tombol Simpan =====
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setUI(new RButtonUI());
        btnSimpan.setPreferredSize(new Dimension(120, 34));
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButton.setOpaque(false);
        panelButton.add(btnSimpan);

        // ===== Gabungkan semua form bagian atas =====
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.add(panelAtas);
        panelForm.add(Box.createVerticalStrut(10));
        panelForm.add(panelSumber);
        panelForm.add(Box.createVerticalStrut(8));
        panelForm.add(panelTujuanWrap);
        panelForm.add(Box.createVerticalStrut(12));
        panelForm.add(panelButton);

        // Tambahkan padding supaya tombol tidak ketiban scrollbar
        panelForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Scroll untuk form (agar tetap bisa discroll tapi tidak terlalu panjang)
        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        scrollForm.setPreferredSize(new Dimension(820, 320)); // dibatasi agar tabel di bawah naik
        // apply scrollbar UI to main form scroll too
        if (scrollForm.getVerticalScrollBar() != null) {
            scrollForm.getVerticalScrollBar().setUI(new RScrollBarUI());
        }

        // ===== Panel Histori Mutasi =====
        modelHistori = new DefaultTableModel(
                new String[]{"Tanggal", "Sumber Dana", "Tujuan Dana", "Total", "Keterangan"}, 0);
        tblHistori = new JTable(modelHistori);
        tblHistori.setRowHeight(22);
        tblHistori.setFillsViewportHeight(true);
        tblHistori.setShowGrid(false);
        tblHistori.setIntercellSpacing(new Dimension(8, 4));
        tblHistori.setFont(tblHistori.getFont().deriveFont(Font.PLAIN, 12f));

        JScrollPane scrollHistori = new JScrollPane(tblHistori);
        scrollHistori.setBorder(BorderFactory.createTitledBorder("Histori Mutasi"));
        scrollHistori.setPreferredSize(new Dimension(820, 180)); // agak naik / proporsional
        if (scrollHistori.getVerticalScrollBar() != null) {
            scrollHistori.getVerticalScrollBar().setUI(new RScrollBarUI());
        }

        // ===== Layout utama =====
        add(scrollForm, BorderLayout.CENTER);
        add(scrollHistori, BorderLayout.SOUTH);

        // ===== Aksi =====
        btnTambahTujuan.addActionListener(e -> tambahTujuan());
        btnSimpan.addActionListener(e -> simpanMutasi());

        // apply RPanelUI to some containers for consistent look (if class present)
        try {
            panelTujuanWrap.setUI(new RPanelUI());
            panelSumber.setUI(new RPanelUI());
            panelForm.setUI(new RPanelUI());
            panelAtas.setUI(new RPanelUI());
        } catch (Exception ex) {
            // jika RPanelUI tidak support setUI untuk JPanel, tidak masalah — styling tetap berlaku untuk komponen lain
        }
    }

    // === Load daftar COA untuk combobox ===
    private void loadCoaData(JComboBox<String> combo) {
        String sql = "SELECT code, nama FROM coa WHERE code IN ('101-03','101-02','101-01003','101-01002','101-01001')";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("nama");
                combo.addItem(name);
                coaMap.put(name, code);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load COA: " + e.getMessage());
        }
    }

    private void tambahTujuan() {
        TujuanRow row = new TujuanRow(this);
        tujuanList.add(row);
        panelTujuan.add(row);
        panelTujuan.revalidate();
        panelTujuan.repaint();
    }

    protected void updateTotalTujuan() {
        int total = 0;
        for (TujuanRow row : tujuanList) {
            total += row.getNominal();
        }
        lblTotalTujuan.setText("Total Tujuan: " + total);
    }

    private void updateTotalSumber() {
        try {
            int jumlah = Integer.parseInt(txtJumlahSumber.getText());
            lblTotalSumber.setText("Total Sumber: " + jumlah);
        } catch (NumberFormatException e) {
            lblTotalSumber.setText("Total Sumber: 0");
        }
    }

    private void simpanMutasi() {
        String tanggal = txtTanggal.getText();
        String sumber = cmbSumberDana.getSelectedItem().toString();
        String ket = txtKeterangan.getText();
        int jumlahSumber;

        try {
            jumlahSumber = Integer.parseInt(txtJumlahSumber.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah sumber tidak valid!");
            return;
        }

        int totalTujuan = 0;
        StringBuilder tujuanStr = new StringBuilder();

        for (TujuanRow row : tujuanList) {
            if (row.getNominal() > 0) {
                tujuanStr.append(row.getTujuan()).append(" (").append(row.getNominal()).append("), ");
                totalTujuan += row.getNominal();
            }
        }

        if (totalTujuan != jumlahSumber) {
            JOptionPane.showMessageDialog(this,
                    "Total tujuan (" + totalTujuan + ") tidak sama dengan jumlah sumber (" + jumlahSumber + ")!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            String sumberCode = coaMap.get(sumber);

            try (PreparedStatement ps = conn.prepareStatement("UPDATE coa SET ending = ending - ? WHERE code = ?")) {
                ps.setDouble(1, jumlahSumber);
                ps.setString(2, sumberCode);
                ps.executeUpdate();
            }

            for (TujuanRow row : tujuanList) {
                String tujuanCode = coaMap.get(row.getTujuan());

                try (PreparedStatement ps = conn.prepareStatement("UPDATE coa SET ending = ending + ? WHERE code = ?")) {
                    ps.setDouble(1, row.getNominal());
                    ps.setString(2, tujuanCode);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO mutasi_kas (tanggal, sumber_code, tujuan_code, jumlah, keterangan) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setString(1, tanggal);
                    ps.setString(2, sumberCode);
                    ps.setString(3, tujuanCode);
                    ps.setDouble(4, row.getNominal());
                    ps.setString(5, ket);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Mutasi berhasil disimpan!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan mutasi: " + e.getMessage());
        }

        modelHistori.addRow(new Object[]{tanggal, sumber, tujuanStr.toString(), jumlahSumber, ket});
        tujuanList.clear();
        panelTujuan.removeAll();
        panelTujuan.revalidate();
        panelTujuan.repaint();
        txtJumlahSumber.setText("");
        lblTotalTujuan.setText("Total Tujuan: 0");
        lblTotalSumber.setText("Total Sumber: 0");
    }

    private static class TujuanRow extends JPanel {
        private JComboBox<String> cmbTujuan;
        private JTextField txtNominal;
        private JButton btnHapus;

        public TujuanRow(FormMutasikas parent) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
            setOpaque(false);

            cmbTujuan = new JComboBox<>();
            parent.loadCoaData(cmbTujuan);
            cmbTujuan.setPreferredSize(new Dimension(220, 28));
            cmbTujuan.setUI(new RComboBoxUI());

            txtNominal = new JTextField(8);
            txtNominal.setPreferredSize(new Dimension(100, 28));
            txtNominal.setUI(new RTextFieldUI());

            btnHapus = new JButton("Hapus");
            btnHapus.setUI(new RButtonUI());
            btnHapus.setPreferredSize(new Dimension(84, 28));
            btnHapus.addActionListener(e -> {
                Container c = getParent();
                if (c != null) {
                    c.remove(this);
                    c.revalidate();
                    c.repaint();
                    parent.tujuanList.remove(this);
                    parent.updateTotalTujuan();
                }
            });

            txtNominal.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    parent.updateTotalTujuan();
                }
            });

            add(new JLabel("Tujuan:"));
            add(cmbTujuan);
            add(new JLabel("Nominal:"));
            add(txtNominal);
            add(btnHapus);
        }

        public int getNominal() {
            try {
                return Integer.parseInt(txtNominal.getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public String getTujuan() {
            return cmbTujuan.getSelectedItem().toString();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Form Mutasi Kas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FormMutasikas());
        frame.setSize(900, 640);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}