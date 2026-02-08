package com.kkp.keuangan.laporan;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RPanelUI;
import com.kkp.keuangan.swing.ModernScrollBarUI;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
/**
 *
 * @author User
 */
public class LaporanNeraca extends JPanel {
    private JComboBox<String> cbBulan;
    private JComboBox<String> cbTahun;
    private JButton btnTampilkan, btnPrint;
    private JPanel panelCard;

    public LaporanNeraca() {
        setLayout(null);
        setBackground(new Color(245, 245, 245));
        setSize(900, 600);
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Laporan Neraca");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 900, 50);
        add(lblTitle);

        JLabel lblBulan = new JLabel("Pilih Bulan:");
        lblBulan.setBounds(200, 80, 100, 50);
        add(lblBulan);

        String[] bulan = {
            "01 - Januari", "02 - Februari", "03 - Maret", "04 - April",
            "05 - Mei", "06 - Juni", "07 - Juli", "08 - Agustus",
            "09 - September", "10 - Oktober", "11 - November", "12 - Desember"
        };
        cbBulan = new JComboBox<>(bulan);
        cbBulan.setBounds(280, 80, 140, 50);
        cbBulan.setUI(new RComboBoxUI());
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        cbBulan.setSelectedIndex(currentMonth - 1);
        add(cbBulan);

        JLabel lblTahun = new JLabel("Pilih Tahun:");
        lblTahun.setBounds(450, 80, 100, 50);
        add(lblTahun);

        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);
        String[] tahun = {
            String.valueOf(tahunSekarang - 1),
            String.valueOf(tahunSekarang),
            String.valueOf(tahunSekarang + 1)
        };
        cbTahun = new JComboBox<>(tahun);
        cbTahun.setBounds(540, 80, 100, 50);
        cbTahun.setUI(new RComboBoxUI());
        cbTahun.setSelectedItem(String.valueOf(tahunSekarang));
        add(cbTahun);

        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.setUI(new RButtonUI());
        btnTampilkan.setBounds(660, 80, 120, 50);
        add(btnTampilkan);
        btnTampilkan.addActionListener(this::tampilkanLaporan);

        // ===== PANEL CARD LAPORAN =====
        panelCard = new JPanel(null);
        panelCard.setBackground(Color.WHITE);
        panelCard.setUI(new RPanelUI());
        panelCard.setPreferredSize(new Dimension(580, 600)); // Width, Height - adjust as needed

        JScrollPane scrollPane = new JScrollPane(panelCard);
        scrollPane.setBounds(150, 140, 600, 320);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, Integer.MAX_VALUE));

        add(scrollPane);

        // ====== Isi laporan ======


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

        try {
            CoaDAO coaDAO = new CoaDAO();
            List<ModelCoa> aktivaList = coaDAO.findAllByCode("101%", periode);
            List<ModelCoa> kewajibanList = coaDAO.findAllByCode("201%", periode);
            List<ModelCoa> ekuitasList = coaDAO.findAllByCode("301%", periode);

            renderNeracaData(aktivaList, kewajibanList, ekuitasList);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ambil data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void renderNeracaData(List<ModelCoa> aktivaList, List<ModelCoa> kewajibanList, List<ModelCoa> ekuitasList) {
        panelCard.removeAll(); // Clear existing components
        int yOffset = 20;
        int xOffset = 40;
        int labelHeight = 25;
        int valueXOffset = 420;
        int valueWidth = 150;

        // Display Aktiva
        JLabel lblAktivaTitle = new JLabel("AKTIVA");
        lblAktivaTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAktivaTitle.setBounds(xOffset, yOffset, 250, labelHeight);
        panelCard.add(lblAktivaTitle);
        yOffset += labelHeight + 5;

        double totalAktiva = 0;
        for (ModelCoa coa : aktivaList) {
            JLabel lblCoaName = new JLabel("   " + coa.getCode() + " - " + coa.getNama());
            lblCoaName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCoaName.setBounds(xOffset, yOffset, 300, labelHeight);
            panelCard.add(lblCoaName);

            JLabel lblCoaValue = new JLabel(String.format("Rp. %,.0f", coa.getEnding()));
            lblCoaValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCoaValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
            panelCard.add(lblCoaValue);
            yOffset += labelHeight;
            totalAktiva += coa.getEnding();
        }
        JLabel lblTotalAktiva = new JLabel("Total Aktiva");
        lblTotalAktiva.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalAktiva.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblTotalAktiva);

        JLabel lblTotalAktivaValue = new JLabel(String.format("Rp. %,.0f", totalAktiva));
        lblTotalAktivaValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalAktivaValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblTotalAktivaValue);
        yOffset += labelHeight + 15;


        // Display Kewajiban
        JLabel lblKewajibanTitle = new JLabel("KEWAJIBAN");
        lblKewajibanTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKewajibanTitle.setBounds(xOffset, yOffset, 250, labelHeight);
        panelCard.add(lblKewajibanTitle);
        yOffset += labelHeight + 5;

        double totalKewajiban = 0;
        for (ModelCoa coa : kewajibanList) {
            JLabel lblCoaName = new JLabel("   " + coa.getCode() + " - " + coa.getNama());
            lblCoaName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCoaName.setBounds(xOffset, yOffset, 300, labelHeight);
            panelCard.add(lblCoaName);

            JLabel lblCoaValue = new JLabel(String.format("Rp. %,.0f", coa.getEnding()));
            lblCoaValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCoaValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
            panelCard.add(lblCoaValue);
            yOffset += labelHeight;
            totalKewajiban += coa.getEnding();
        }
        JLabel lblTotalKewajiban = new JLabel("Total Kewajiban");
        lblTotalKewajiban.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalKewajiban.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblTotalKewajiban);

        JLabel lblTotalKewajibanValue = new JLabel(String.format("Rp. %,.0f", totalKewajiban));
        lblTotalKewajibanValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalKewajibanValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblTotalKewajibanValue);
        yOffset += labelHeight + 15;

        // Display Ekuitas
        JLabel lblEkuitasTitle = new JLabel("EKUITAS");
        lblEkuitasTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEkuitasTitle.setBounds(xOffset, yOffset, 250, labelHeight);
        panelCard.add(lblEkuitasTitle);
        yOffset += labelHeight + 5;

        double totalEkuitas = 0;
        for (ModelCoa coa : ekuitasList) {
            JLabel lblCoaName = new JLabel("   " + coa.getCode() + " - " + coa.getNama());
            lblCoaName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCoaName.setBounds(xOffset, yOffset, 300, labelHeight);
            panelCard.add(lblCoaName);

            JLabel lblCoaValue = new JLabel(String.format("Rp. %,.0f", coa.getEnding()));
            lblCoaValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblCoaValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
            panelCard.add(lblCoaValue);
            yOffset += labelHeight;
            totalEkuitas += coa.getEnding();
        }
        JLabel lblTotalEkuitas = new JLabel("Total Ekuitas");
        lblTotalEkuitas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalEkuitas.setBounds(xOffset, yOffset, 300, labelHeight);
        panelCard.add(lblTotalEkuitas);

        JLabel lblTotalEkuitasValue = new JLabel(String.format("Rp. %,.0f", totalEkuitas));
        lblTotalEkuitasValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalEkuitasValue.setBounds(valueXOffset, yOffset, valueWidth, labelHeight);
        panelCard.add(lblTotalEkuitasValue);

        // Update panel
        int totalHeight = 0;
        for (Component comp : panelCard.getComponents()) {
            totalHeight = Math.max(totalHeight, comp.getY() + comp.getHeight());
        }
        panelCard.setPreferredSize(new Dimension(580, totalHeight + 20));
        panelCard.revalidate();
        panelCard.repaint();
    }


    private void printLaporan(ActionEvent e) {
        try {
            String bulanDipilih = cbBulan.getSelectedItem().toString().substring(0, 2);
            String tahunDipilih = cbTahun.getSelectedItem().toString();
            String periode = tahunDipilih + bulanDipilih;
    
            CoaDAO coaDAO = new CoaDAO();
            List<ModelCoa> allCoa = new ArrayList<>();
            allCoa.addAll(coaDAO.findAllByCode("101%", periode));
            allCoa.addAll(coaDAO.findAllByCode("201%", periode));
            allCoa.addAll(coaDAO.findAllByCode("301%", periode));
    
            Map<String, Object> param = new HashMap<>();
            param.put("periode", periode);
    
            String mainReportName = "LaporanNeracaDetail";
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

            String subReportName = "NeracaCoaDetailSubreport";
            String subJrxmlPath = "com/kkp/keuangan/laporan/" + subReportName + ".jrxml";
            String subJasperPath = "com/kkp/keuangan/laporan/" + subReportName + ".jasper";
    
            InputStream subJasperStream = getClass().getClassLoader().getResourceAsStream(subJasperPath);
            JasperReport subJasperReport;
    
            if (subJasperStream != null) {
                subJasperReport = (JasperReport) JRLoader.loadObject(subJasperStream);
                subJasperStream.close();
                System.out.println("Menggunakan subreport .jasper yang sudah ada: " + subJasperPath);
            } else {
                InputStream subJrxmlStream = getClass().getClassLoader().getResourceAsStream(subJrxmlPath);
                if (subJrxmlStream == null) {
                    throw new RuntimeException("File subreport .jrxml tidak ditemukan: " + subJrxmlPath);
                }
                subJasperReport = JasperCompileManager.compileReport(subJrxmlStream);
                subJrxmlStream.close();
                System.out.println("Berhasil compile subreport .jrxml ke memory: " + subJrxmlPath);
            }

            param.put("subreportNeracaCoaDetail", subJasperReport);

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
        JFrame frame = new JFrame("Laporan Neraca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new LaporanNeraca());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}