package com.kkp.keuangan.form;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.component.uis.*;

public class FormBiaya extends JPanel {

    private JTextField txtTanggal, txtKeterangan;
    private JLabel lblTotalSumber, lblTotalTujuan;
    private JPanel panelSumber, panelTujuan;
    private JTable tblHistori;
    private DefaultTableModel modelHistori;

    private java.util.List<SumberRow> sumberList = new ArrayList<>();
    private java.util.List<TujuanRow> tujuanList = new ArrayList<>();
    private java.util.Map<String, String> coaMap = new java.util.HashMap<>();

    public FormBiaya() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        // ===== Panel Atas =====
        JPanel panelAtas = new JPanel(new GridLayout(1, 2, 10, 10));
        panelAtas.setOpaque(false);

        JPanel panelTanggal = new JPanel(new BorderLayout(4, 4));
        panelTanggal.setOpaque(false);
        JLabel lblTanggal = new JLabel("Tanggal:");
        txtTanggal = new JTextField(java.time.LocalDate.now().toString());
        txtTanggal.setUI(new RTextFieldUI());
        panelTanggal.add(lblTanggal, BorderLayout.NORTH);
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);

        JPanel panelKet = new JPanel(new BorderLayout(4, 4));
        panelKet.setOpaque(false);
        JLabel lblKet = new JLabel("Keterangan:");
        txtKeterangan = new JTextField();
        txtKeterangan.setUI(new RTextFieldUI());
        panelKet.add(lblKet, BorderLayout.NORTH);
        panelKet.add(txtKeterangan, BorderLayout.CENTER);

        panelAtas.add(panelTanggal);
        panelAtas.add(panelKet);

        // ===== Panel Sumber Dana =====
        JPanel panelSumberWrap = new JPanel(new BorderLayout());
        panelSumberWrap.setBorder(BorderFactory.createTitledBorder("Debit"));
        panelSumberWrap.setOpaque(false);

        panelSumber = new JPanel();
        panelSumber.setLayout(new BoxLayout(panelSumber, BoxLayout.Y_AXIS));
        panelSumber.setOpaque(false);

        JScrollPane scrollSumber = new JScrollPane(panelSumber);
        scrollSumber.setPreferredSize(new Dimension(420, 80));
        scrollSumber.setBorder(BorderFactory.createEmptyBorder());
        scrollSumber.getVerticalScrollBar().setUI(new RScrollBarUI());

        JButton btnTambahSumber = new JButton("Tambah Debit");
        btnTambahSumber.setUI(new RButtonUI());
        btnTambahSumber.setPreferredSize(new Dimension(160, 30));
        lblTotalSumber = new JLabel("Total Debit: 0");

        JPanel panelSumberBottom = new JPanel(new BorderLayout(6, 6));
        panelSumberBottom.setOpaque(false);
        panelSumberBottom.add(btnTambahSumber, BorderLayout.WEST);
        panelSumberBottom.add(lblTotalSumber, BorderLayout.EAST);

        panelSumberWrap.add(scrollSumber, BorderLayout.CENTER);
        panelSumberWrap.add(panelSumberBottom, BorderLayout.SOUTH);

        // ===== Panel Tujuan Dana =====
        JPanel panelTujuanWrap = new JPanel(new BorderLayout());
        panelTujuanWrap.setBorder(BorderFactory.createTitledBorder("Kredit"));
        panelTujuanWrap.setOpaque(false);

        panelTujuan = new JPanel();
        panelTujuan.setLayout(new BoxLayout(panelTujuan, BoxLayout.Y_AXIS));
        panelTujuan.setOpaque(false);

        JScrollPane scrollTujuan = new JScrollPane(panelTujuan);
        scrollTujuan.setPreferredSize(new Dimension(420, 80));
        scrollTujuan.setBorder(BorderFactory.createEmptyBorder());
        scrollTujuan.getVerticalScrollBar().setUI(new RScrollBarUI());

        JButton btnTambahTujuan = new JButton("Tambah Kredit");
        btnTambahTujuan.setUI(new RButtonUI());
        btnTambahTujuan.setPreferredSize(new Dimension(160, 30));
        lblTotalTujuan = new JLabel("Total Kredit: 0");

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

        // ===== Gabungkan =====
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.add(panelAtas);
        panelForm.add(Box.createVerticalStrut(10));
        panelForm.add(panelSumberWrap);
        panelForm.add(Box.createVerticalStrut(8));
        panelForm.add(panelTujuanWrap);
        panelForm.add(Box.createVerticalStrut(12));
        panelForm.add(panelButton);

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        scrollForm.setPreferredSize(new Dimension(820, 360));
        scrollForm.getVerticalScrollBar().setUI(new RScrollBarUI());

        // ===== Histori =====
        modelHistori = new DefaultTableModel(
            new String[]{"Tanggal", "Debit", "Kredit", "Total", "Keterangan"}, 0);
        tblHistori = new JTable(modelHistori);
        tblHistori.setRowHeight(22);

        JScrollPane scrollHistori = new JScrollPane(tblHistori);
        scrollHistori.setBorder(BorderFactory.createTitledBorder("Histori Biaya"));
        scrollHistori.setPreferredSize(new Dimension(820, 180));
        scrollHistori.getVerticalScrollBar().setUI(new RScrollBarUI());

        add(scrollForm, BorderLayout.CENTER);
        add(scrollHistori, BorderLayout.SOUTH);

        // ===== Aksi =====
        btnTambahSumber.addActionListener(e -> tambahSumber());
        btnTambahTujuan.addActionListener(e -> tambahTujuan());
        btnSimpan.addActionListener(e -> simpanBiaya());
    }

    private void loadCoaData(JComboBox<String> combo) {
        String sql = "SELECT code, nama FROM coa";
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

    private void tambahSumber() {
        SumberRow row = new SumberRow(this);
        sumberList.add(row);
        panelSumber.add(row);
        panelSumber.revalidate();
        panelSumber.repaint();
    }

    private void tambahTujuan() {
        TujuanRow row = new TujuanRow(this);
        tujuanList.add(row);
        panelTujuan.add(row);
        panelTujuan.revalidate();
        panelTujuan.repaint();
    }

    protected void updateTotalSumber() {
        int total = sumberList.stream().mapToInt(SumberRow::getNominal).sum();
        lblTotalSumber.setText("Total Sumber: " + total);
    }

    protected void updateTotalTujuan() {
        int total = tujuanList.stream().mapToInt(TujuanRow::getNominal).sum();
        lblTotalTujuan.setText("Total Tujuan: " + total);
    }

    private void simpanBiaya() {
        String tanggal = txtTanggal.getText();
        String ket = txtKeterangan.getText();

        int totalSumber = sumberList.stream().mapToInt(SumberRow::getNominal).sum();
        int totalTujuan = tujuanList.stream().mapToInt(TujuanRow::getNominal).sum();

        if (totalSumber != totalTujuan) {
            JOptionPane.showMessageDialog(this, "Total sumber dan tujuan harus sama!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Kurangi saldo sumber
            for (SumberRow row : sumberList) {
                String code = coaMap.get(row.getSumber());
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE coa SET ending = ending - ? WHERE code = ?")) {
                    ps.setDouble(1, row.getNominal());
                    ps.setString(2, code);
                    ps.executeUpdate();
                }
            }

            // Tambah saldo tujuan dan catat transaksi
            for (TujuanRow row : tujuanList) {
                String code = coaMap.get(row.getTujuan());
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE coa SET ending = ending + ? WHERE code = ?")) {
                    ps.setDouble(1, row.getNominal());
                    ps.setString(2, code);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO biaya (tanggal, sumber_code, tujuan_code, jumlah, keterangan) VALUES (?, ?, ?, ?, ?)")) {
                    for (SumberRow s : sumberList) {
                        ps.setString(1, tanggal);
                        ps.setString(2, coaMap.get(s.getSumber()));
                        ps.setString(3, code);
                        ps.setDouble(4, row.getNominal());
                        ps.setString(5, ket);
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Data biaya berhasil disimpan!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan biaya: " + e.getMessage());
        }

        modelHistori.addRow(new Object[]{
            tanggal, sumberList.size() + " sumber", tujuanList.size() + " tujuan", totalTujuan, ket
        });

        sumberList.clear();
        tujuanList.clear();
        panelSumber.removeAll();
        panelTujuan.removeAll();
        panelSumber.revalidate();
        panelTujuan.revalidate();
        updateTotalSumber();
        updateTotalTujuan();
    }

    // === Inner Class untuk Row Sumber & Tujuan ===
    private static class SumberRow extends JPanel {
        private JComboBox<String> cmbSumber;
        private JTextField txtNominal;
        private JButton btnHapus;

        public SumberRow(FormBiaya parent) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
            setOpaque(false);

            cmbSumber = new JComboBox<>();
            parent.loadCoaData(cmbSumber);
            cmbSumber.setPreferredSize(new Dimension(220, 28));
            cmbSumber.setUI(new RComboBoxUI());

            txtNominal = new JTextField(8);
            txtNominal.setPreferredSize(new Dimension(100, 28));
            txtNominal.setUI(new RTextFieldUI());
            txtNominal.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) { parent.updateTotalSumber(); }
            });

            btnHapus = new JButton("Hapus");
            btnHapus.setUI(new RButtonUI());
            btnHapus.setPreferredSize(new Dimension(84, 28));
            btnHapus.addActionListener(e -> {
                parent.sumberList.remove(this);
                Container c = getParent();
                if (c != null) { c.remove(this); c.revalidate(); c.repaint(); }
                parent.updateTotalSumber();
            });

            add(new JLabel("Sumber:"));
            add(cmbSumber);
            add(new JLabel("Nominal:"));
            add(txtNominal);
            add(btnHapus);
        }

        public String getSumber() { return cmbSumber.getSelectedItem().toString(); }
        public int getNominal() {
            try { return Integer.parseInt(txtNominal.getText()); } catch (NumberFormatException e) { return 0; }
        }
    }

    private static class TujuanRow extends JPanel {
        private JComboBox<String> cmbTujuan;
        private JTextField txtNominal;
        private JButton btnHapus;

        public TujuanRow(FormBiaya parent) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
            setOpaque(false);

            cmbTujuan = new JComboBox<>();
            parent.loadCoaData(cmbTujuan);
            cmbTujuan.setPreferredSize(new Dimension(220, 28));
            cmbTujuan.setUI(new RComboBoxUI());

            txtNominal = new JTextField(8);
            txtNominal.setPreferredSize(new Dimension(100, 28));
            txtNominal.setUI(new RTextFieldUI());
            txtNominal.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) { parent.updateTotalTujuan(); }
            });

            btnHapus = new JButton("Hapus");
            btnHapus.setUI(new RButtonUI());
            btnHapus.setPreferredSize(new Dimension(84, 28));
            btnHapus.addActionListener(e -> {
                parent.tujuanList.remove(this);
                Container c = getParent();
                if (c != null) { c.remove(this); c.revalidate(); c.repaint(); }
                parent.updateTotalTujuan();
            });

            add(new JLabel("Tujuan:"));
            add(cmbTujuan);
            add(new JLabel("Nominal:"));
            add(txtNominal);
            add(btnHapus);
        }

        public String getTujuan() { return cmbTujuan.getSelectedItem().toString(); }
        public int getNominal() {
            try { return Integer.parseInt(txtNominal.getText()); } catch (NumberFormatException e) { return 0; }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Form Biaya");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FormBiaya());
        frame.setSize(900, 660);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}