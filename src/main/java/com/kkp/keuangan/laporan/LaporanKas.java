package com.kkp.keuangan.laporan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RPanelUI;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

// ----- MODEL DATA (Simulasi database) -----
public class LaporanKas extends JPanel {
    private JComboBox<String> cbBulan;
    private JComboBox<String> cbTahun;
    private JComboBox<String> cbKas;
    private JButton btnTampilkan, btnPrint;
    private JPanel panelCard;

    public LaporanKas() {
        setLayout(null);
        setBackground(new Color(245, 245, 245));
        setSize(900, 600);
        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel lblTitle = new JLabel("Laporan Kas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 900, 30);
        add(lblTitle);

        // Kas
        JLabel lblKas = new JLabel("Pilih Kas:");
        lblKas.setBounds(50, 80, 80, 30);
        add(lblKas);

        cbKas = new JComboBox<>();
        loadComboCoa();
        cbKas.setBounds(140, 80, 150, 30);
        cbKas.setUI(new RComboBoxUI());
        add(cbKas);

        // Bulan
        JLabel lblBulan = new JLabel("Pilih Bulan:");
        lblBulan.setBounds(310, 80, 80, 30);
        add(lblBulan);

        String[] bulan = {
            "01 - Januari", "02 - Februari", "03 - Maret", "04 - April",
            "05 - Mei", "06 - Juni", "07 - Juli", "08 - Agustus",
            "09 - September", "10 - Oktober", "11 - November", "12 - Desember"
        };
        cbBulan = new JComboBox<>(bulan);
        cbBulan.setBounds(400, 80, 150, 30);
        cbBulan.setUI(new RComboBoxUI());
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        cbBulan.setSelectedIndex(currentMonth - 1);
        add(cbBulan);

        // Tahun
        JLabel lblTahun = new JLabel("Pilih Tahun:");
        lblTahun.setBounds(570, 80, 80, 30);
        add(lblTahun);

        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);
        String[] tahun = {
            String.valueOf(tahunSekarang - 1),
            String.valueOf(tahunSekarang),
            String.valueOf(tahunSekarang + 1)
        };
        cbTahun = new JComboBox<>(tahun);
        cbTahun.setBounds(660, 80, 100, 30);
        cbTahun.setUI(new RComboBoxUI());
        cbTahun.setSelectedItem(String.valueOf(tahunSekarang));
        add(cbTahun);

        // Button Tampilkan
        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.setUI(new RButtonUI());
        btnTampilkan.setBounds(780, 80, 100, 30);
        add(btnTampilkan);
        btnTampilkan.addActionListener(this::tampilkanLaporan);

        // Panel Card Laporan
        panelCard = new JPanel(null);
        panelCard.setBounds(50, 130, 800, 350);
        panelCard.setBackground(Color.WHITE);
        panelCard.setUI(new RPanelUI());
        add(panelCard);

        // Isi laporan


        // Button Print
        btnPrint = new JButton("Print");
        btnPrint.setUI(new RButtonUI());
        btnPrint.setBounds(400, 500, 100, 35);
        add(btnPrint);
        btnPrint.addActionListener(this::printLaporan);
    }

    private void loadComboCoa() {
        String sql = "SELECT * FROM coa WHERE code LIKE '101-0%'";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("nama");
                String label = code + " - " + name;
                cbKas.addItem(label);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load COA: " + e.getMessage());
        }
    }

    private void tampilkanLaporan(ActionEvent e) {
        String bulanDipilih = cbBulan.getSelectedItem().toString().substring(0, 2);
        String tahunDipilih = cbTahun.getSelectedItem().toString();
        String kasDipilih = cbKas.getSelectedItem().toString();
        String periode = tahunDipilih + bulanDipilih;
        String coaCode = kasDipilih.split(" - ")[0];

        try {
            CoaDAO coaDAO = new CoaDAO();
            ModelCoa coaKas = coaDAO.findByCode(coaCode, periode);

            renderKas(coaKas);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ambil data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void renderKas(ModelCoa coaKas) {
        panelCard.removeAll(); // Clear existing components
        int yOffset = 20;
        int xOffset = 40;
        int labelHeight = 25;
        int valueXOffset = 420;
        int valueWidth = 150;

        JLabel lblAktivaTitle = new JLabel("   " + coaKas.getCode() + " - " + coaKas.getNama());
        lblAktivaTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAktivaTitle.setBounds(xOffset, yOffset, 250, labelHeight);
        panelCard.add(lblAktivaTitle);
        yOffset += labelHeight + 5;

        JLabel lblBegin = new JLabel("Posisi Awal Kas");
        lblBegin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBegin.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblBegin);

        JLabel lblBeginValue = new JLabel(String.format("Rp. %,.0f", coaKas.getBeginning()));
        lblBeginValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBeginValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblBeginValue);
        yOffset += labelHeight;

        JLabel lblDebit = new JLabel("Pemasukan");
        lblDebit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDebit.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblDebit);

        JLabel lblDebitValue = new JLabel(String.format("Rp. %,.0f", coaKas.getDebit()));
        lblDebitValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDebitValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblDebitValue);
        yOffset += labelHeight;

        JLabel lblCredit = new JLabel("Pengeluaran");
        lblCredit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCredit.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblCredit);

        JLabel lblCreditValue = new JLabel(String.format("Rp. %,.0f", coaKas.getCredit()));
        lblCreditValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCreditValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblCreditValue);
        yOffset += labelHeight;

        JLabel lblEnding = new JLabel("Posisi Akhir Kas");
        lblEnding.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEnding.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblEnding);

        JLabel lblEndingValue = new JLabel(String.format("Rp. %,.0f", coaKas.getEnding()));
        lblEndingValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEndingValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblEndingValue);
        yOffset += labelHeight + 40; // Add spacing before footer

        // Footer - Signature section
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        String currentDate = LocalDate.now().format(formatter);
        
        JLabel lblLocation = new JLabel("Jakarta, " + currentDate);
        lblLocation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLocation.setBounds(valueXOffset, yOffset, valueWidth + 50, labelHeight);
        panelCard.add(lblLocation);
        yOffset += labelHeight + 60; // Space for signature

        JLabel lblSignature = new JLabel("Supin");
        lblSignature.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSignature.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblSignature);

        // Update panel
        panelCard.revalidate();
        panelCard.repaint();
    }

    private void printLaporan(ActionEvent e) {
        try {
            String bulanDipilih = cbBulan.getSelectedItem().toString().substring(0, 2);
            String tahunDipilih = cbTahun.getSelectedItem().toString();
            String kasDipilih = cbKas.getSelectedItem().toString();
            String periode = tahunDipilih + bulanDipilih;
            String coaCode = kasDipilih.split(" - ")[0];
    
            CoaDAO coaDAO = new CoaDAO();
            List<ModelCoa> allCoa = new ArrayList<>();
            allCoa.addAll(coaDAO.findAllByCode(coaCode, periode));
    
            Map<String, Object> param = new HashMap<>();
            param.put("periode", periode);
    
            String mainReportName = "LaporanKasDetail";
            String mainJrxmlPath = "com/kkp/keuangan/laporan/" + mainReportName + ".jrxml";
            String mainJasperPath = "com/kkp/keuangan/laporan/" + mainReportName + ".jasper";
    
            InputStream mainJasperStream = getClass().getClassLoader().getResourceAsStream(mainJasperPath);
            JasperReport mainJasperReport;
    
            if (mainJasperStream != null) {
                mainJasperReport = (JasperReport) JRLoader.loadObject(mainJasperStream);
                mainJasperStream.close();
                System.out.println("Menggunakan main .jasper yang sudah ada: " + mainJasperPath);
            } else {
                InputStream mainJrxmlStream = getClass().getClassLoader().getResourceAsStream(mainJrxmlPath);
                if (mainJrxmlStream == null) {
                    throw new RuntimeException("File main .jrxml tidak ditemukan: " + mainJrxmlPath);
                }
                mainJasperReport = JasperCompileManager.compileReport(mainJrxmlStream);
                mainJrxmlStream.close();
                System.out.println("Berhasil compile main .jrxml ke memory: " + mainJrxmlPath);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCoa);

            JasperPrint jp = JasperFillManager.fillReport(
                    mainJasperReport,
                    param,
                    dataSource
            );
    
            JasperViewer.viewReport(jp, false);
    
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Laporan Kas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new LaporanKas());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}