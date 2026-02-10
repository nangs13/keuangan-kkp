package com.kkp.keuangan.laporan;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.backend.Database;
import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.dao.CoaLogDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.backend.model.ModelCoaLog;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RPanelUI;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.Map;

import com.kkp.keuangan.swing.ModernScrollBarUI;
import com.kkp.keuangan.swing.Table;
import com.kkp.keuangan.swing.TableHeader;

// ----- MODEL DATA (Simulasi database) -----
public class LaporanArusKas extends JPanel {
    private JComboBox<String> cbBulan;
    private JComboBox<String> cbTahun;
    private JComboBox<String> cbKas;
    private JButton btnTampilkan, btnPrint;
    private JPanel panelCard;
    private Table tableLaporan;
    private DefaultTableModel tableModel;
    private List<LaporanArusKasHarianRecord> dailyRecords;

    public LaporanArusKas() {
        setLayout(null);
        setBackground(new Color(245, 245, 245));
        setSize(900, 600);
        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel lblTitle = new JLabel("Laporan Arus Kas");
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
        panelCard = new JPanel(new BorderLayout()); // Use BorderLayout for JTable
        panelCard.setBounds(50, 130, 800, 350);
        panelCard.setBackground(Color.WHITE);
        panelCard.setUI(new RPanelUI());
        add(panelCard);

        // Setup Table
        tableModel = new DefaultTableModel(new Object[]{"Tanggal", "Posisi Awal", "Pemasukan", "Pengeluaran", "Posisi Akhir"}, 0);
        tableLaporan = new Table();
        tableLaporan.setModel(tableModel);
        tableLaporan.getTableHeader().setReorderingAllowed(false);
        tableLaporan.getTableHeader().setResizingAllowed(false);
        tableLaporan.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                TableHeader header = new TableHeader(o + "");
                if (i1 == 4) {
                    header.setHorizontalAlignment(JLabel.RIGHT);
                } else if (i1 == 1 || i1 == 2 || i1 == 3) {
                    header.setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    header.setHorizontalAlignment(JLabel.CENTER);
                }
                return header;
            }
        });
        tableLaporan.setRowHeight(30);
        tableLaporan.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 0) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else if (column >= 1 && column <= 4) {
                    setHorizontalAlignment(JLabel.RIGHT);
                }
                
                // Styling
                if (isSelected) {
                    com.setForeground(new Color(15, 89, 140));
                    com.setBackground(new Color(220, 240, 255));
                } else {
                    com.setForeground(new Color(102, 102, 102));
                    com.setBackground(Color.WHITE);
                }
                
                setBorder(noFocusBorder);
                return com;
            }
        });

        JScrollPane scroll = new JScrollPane(tableLaporan);
        scroll.setBackground(Color.WHITE);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        panelCard.add(scroll, BorderLayout.CENTER);


        // Button Print
        btnPrint = new JButton("Print");
        btnPrint.setUI(new RButtonUI());
        btnPrint.setBounds(400, 500, 100, 35);
        add(btnPrint);
        btnPrint.addActionListener(this::printLaporan);
    }

    private void printLaporan(ActionEvent e) {
        try {
            // Load the JRXML file
            String reportPath = "src/main/resources/com/kkp/keuangan/laporan/LaporanArusKasHarian.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

            // Prepare report parameters
            Map<String, Object> parameters = new java.util.HashMap<>();
            String bulanStr = cbBulan.getSelectedItem().toString().substring(0, 2);
            String tahunStr = cbTahun.getSelectedItem().toString();
            parameters.put("periode", bulanStr + "-" + tahunStr);

            // Create a JRBeanCollectionDataSource from the dailyRecords list
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dailyRecords);

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // View the report
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage());
            ex.printStackTrace();
        }
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
        String bulanStr = cbBulan.getSelectedItem().toString().substring(0, 2);
        int bulan = Integer.parseInt(bulanStr);
        int tahun = Integer.parseInt(cbTahun.getSelectedItem().toString());
        String kasDipilih = cbKas.getSelectedItem().toString();
        String coaCode = kasDipilih.split(" - ")[0];
        String periode = String.valueOf(tahun) + bulanStr;

        dailyRecords = new ArrayList<>();
        tableModel.setRowCount(0); // Clear existing table data

        try {
            CoaDAO coaDAO = new CoaDAO();
            CoaLogDAO coaLogDAO = new CoaLogDAO();

            // Get the COA for the selected period to find its beginning balance
            ModelCoa coaKas = coaDAO.findByCode(coaCode, periode);
            if (coaKas == null) {
                JOptionPane.showMessageDialog(this, "COA tidak ditemukan untuk periode ini.");
                return;
            }

            // Calculate start and end dates for the selected month
            LocalDate startDate = LocalDate.of(tahun, bulan, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // Get all COA logs for the selected COA and date range
            List<ModelCoaLog> coaLogs = coaLogDAO.findByCoaIdAndDateRange(coaKas.getId(), startDate, endDate);

            // Group logs by date
            Map<LocalDate, List<ModelCoaLog>> logsByDate = coaLogs.stream()
                    .collect(Collectors.groupingBy(ModelCoaLog::getTanggal));

            double currentPosisiAwal = coaKas.getBeginning();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                double pemasukanHarian = 0;
                double pengeluaranHarian = 0;

                List<ModelCoaLog> dailyLogs = logsByDate.get(date);
                if (dailyLogs != null) {
                    for (ModelCoaLog log : dailyLogs) {
                        if ("debit".equalsIgnoreCase(log.getTipe())) {
                            pemasukanHarian += log.getNominal();
                        } else if ("credit".equalsIgnoreCase(log.getTipe())) {
                            pengeluaranHarian += log.getNominal();
                        }
                    }
                }

                double posisiAkhirHarian = currentPosisiAwal + pemasukanHarian - pengeluaranHarian;

                LaporanArusKasHarianRecord record = new LaporanArusKasHarianRecord(
                        date,
                        currentPosisiAwal,
                        pemasukanHarian,
                        pengeluaranHarian,
                        posisiAkhirHarian
                );
                dailyRecords.add(record);
                tableModel.addRow(new Object[]{
                    record.getTanggal().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    String.format("Rp. %,.0f", record.getPosisiAwal()),
                    String.format("Rp. %,.0f", record.getPemasukan()),
                    String.format("Rp. %,.0f", record.getPengeluaran()),
                    String.format("Rp. %,.0f", record.getPosisiAkhir())
                });

                currentPosisiAwal = posisiAkhirHarian; // Update for the next day
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ambil data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Laporan Arus Kas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new LaporanArusKas());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}