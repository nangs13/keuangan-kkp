/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kkp.keuangan.form;
import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.component.uis.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
/**
 *
 * @author User
 */
public class FormLaporanLabarugi extends JPanel {
    private JComboBox<String> cbBulan;
    private JComboBox<String> cbTahun;
    private JButton btnTampilkan, btnPrint;
    private JLabel lblLabaKotor, lblBiayaMarketing, lblTotalMarketing, lblLabaBersih;
    private JPanel panelCard;

    public FormLaporanLabarugi() {
        setLayout(null);
        setBackground(new Color(245, 245, 245));
        setSize(900, 600);
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Laporan Laba Rugi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 900, 30);
        add(lblTitle);

        JLabel lblBulan = new JLabel("Pilih Bulan:");
        lblBulan.setBounds(200, 80, 100, 25);
        add(lblBulan);

        String[] bulan = {
            "01 - Januari", "02 - Februari", "03 - Maret", "04 - April",
            "05 - Mei", "06 - Juni", "07 - Juli", "08 - Agustus",
            "09 - September", "10 - Oktober", "11 - November", "12 - Desember"
        };
        cbBulan = new JComboBox<>(bulan);
        cbBulan.setBounds(280, 80, 140, 25);
        cbBulan.setUI(new RComboBoxUI());
        add(cbBulan);

        JLabel lblTahun = new JLabel("Pilih Tahun:");
        lblTahun.setBounds(450, 80, 100, 25);
        add(lblTahun);

        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);
        String[] tahun = {
            String.valueOf(tahunSekarang - 1),
            String.valueOf(tahunSekarang),
            String.valueOf(tahunSekarang + 1)
        };
        cbTahun = new JComboBox<>(tahun);
        cbTahun.setBounds(540, 80, 100, 25);
        cbTahun.setUI(new RComboBoxUI());
        cbTahun.setSelectedItem(String.valueOf(tahunSekarang));
        add(cbTahun);

        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.setUI(new RButtonUI());
        btnTampilkan.setBounds(660, 80, 120, 25);
        add(btnTampilkan);
        btnTampilkan.addActionListener(this::tampilkanLaporan);

        // ===== PANEL CARD LAPORAN =====
        panelCard = new JPanel(null);
        panelCard.setBounds(150, 140, 600, 320);
        panelCard.setBackground(Color.WHITE);
        panelCard.setUI(new RPanelUI());
        add(panelCard);

        // ====== Isi laporan ======
        JLabel lblLabaKotorTitle = new JLabel("LABA KOTOR (Pendapatan)");
        lblLabaKotorTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabaKotorTitle.setBounds(40, 40, 250, 25);
        panelCard.add(lblLabaKotorTitle);

        lblLabaKotor = new JLabel("Rp. 0");
        lblLabaKotor.setBounds(420, 40, 150, 25);
        lblLabaKotor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelCard.add(lblLabaKotor);

        JLabel lblBiayaMarketingTitle = new JLabel("BIAYA MARKETING (Beban)");
        lblBiayaMarketingTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBiayaMarketingTitle.setBounds(40, 100, 250, 25);
        panelCard.add(lblBiayaMarketingTitle);

        lblBiayaMarketing = new JLabel("Rp. 0");
        lblBiayaMarketing.setBounds(420, 100, 150, 25);
        lblBiayaMarketing.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelCard.add(lblBiayaMarketing);

        JLabel lblTotalMarketingTitle = new JLabel("TOTAL BIAYA MARKETING");
        lblTotalMarketingTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalMarketingTitle.setBounds(40, 160, 250, 25);
        panelCard.add(lblTotalMarketingTitle);

        lblTotalMarketing = new JLabel("Rp. 0");
        lblTotalMarketing.setBounds(420, 160, 150, 25);
        lblTotalMarketing.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelCard.add(lblTotalMarketing);

        JLabel lblLabaBersihTitle = new JLabel("LABA BERSIH SEBELUM PAJAK");
        lblLabaBersihTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabaBersihTitle.setBounds(40, 220, 300, 25);
        panelCard.add(lblLabaBersihTitle);

        lblLabaBersih = new JLabel("Rp. 0");
        lblLabaBersih.setBounds(420, 220, 150, 25);
        lblLabaBersih.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabaBersih.setForeground(new Color(0, 128, 0));
        panelCard.add(lblLabaBersih);

        // ===== TOMBOL PRINT =====
        btnPrint = new JButton("Print");
        btnPrint.setUI(new RButtonUI());
        btnPrint.setBounds(400, 500, 100, 35);
        add(btnPrint);
        btnPrint.addActionListener(this::printLaporan);
    }

    private void tampilkanLaporan(ActionEvent e) {
        String bulanDipilih = cbBulan.getSelectedItem().toString().substring(0, 2);
        String tahunDipilih = cbTahun.getSelectedItem().toString();
        String periode = tahunDipilih + bulanDipilih;

        try (Connection conn = Database.getConnection()) {
            double labaKotor = getSumPendapatan(conn, "401", periode);
            double totalMarketing = getSumBeban(conn, "501", periode);
            double labaBersih = labaKotor - totalMarketing;

            lblLabaKotor.setText(String.format("Rp. %, .0f", labaKotor));
            lblBiayaMarketing.setText(String.format("Rp. %, .0f", totalMarketing));
            lblTotalMarketing.setText(String.format("Rp. %, .0f", totalMarketing));
            lblLabaBersih.setText(String.format("Rp. %, .0f", labaBersih));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ambil data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ===== Ambil pendapatan (kode 401) dari tabel COA =====
    private double getSumPendapatan(Connection conn, String kode, String periode) throws SQLException {
        String sql = "SELECT SUM(beginning + (credit - debit)) AS total " +
                     "FROM coa WHERE code LIKE ? AND periode = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, kode + "%");
        ps.setString(2, periode);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble("total");
        return 0;
    }

    // ===== Ambil beban (kode 501) dari tabel COA =====
    private double getSumBeban(Connection conn, String kode, String periode) throws SQLException {
        String sql = "SELECT SUM(beginning + (debit - credit)) AS total " +
                     "FROM coa WHERE code LIKE ? AND periode = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, kode + "%");
        ps.setString(2, periode);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble("total");
        return 0;
    }

    private void printLaporan(ActionEvent e) {
        try {
            String bulanDipilih = cbBulan.getSelectedItem().toString().substring(0, 2);
            String tahunDipilih = cbTahun.getSelectedItem().toString();
            String periode = tahunDipilih + bulanDipilih;

            Map<String, Object> param = new HashMap<>();
            param.put("periode", periode);

            String jasperPath = "C:\\Users\\User\\Documents\\Semester 7\\kkpUpdate\\keuangan-kkp\\src\\main\\java\\com\\kkp\\keuangan\\laporan\\LaporanLaba-rugi.jasper";

            JasperPrint jp = JasperFillManager.fillReport(jasperPath, param, Database.getConnection());
            JasperViewer.viewReport(jp, false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Laporan Laba Rugi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new FormLaporanLabarugi());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}