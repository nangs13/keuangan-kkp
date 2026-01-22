package com.kkp.keuangan.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.dao.MutasiKasDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.backend.model.ModelMutasiKas;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.monitor.component.ButtonEditor;
import com.kkp.keuangan.monitor.component.ButtonEditorMutasiKas;
import com.kkp.keuangan.monitor.component.ButtonRenderer;
import com.kkp.keuangan.swing.ScrollBar;

public class MonitorMutasiKas extends JPanel {

    private JTextField txtSearch;
    private JButton btnPrev, btnNext, btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane spTable;

    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalRows = 0;

    private List<Object[]> allData = new ArrayList<>();

    public MonitorMutasiKas() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));

        // -------------------------
        // PANEL ATAS (Search bar)
        // -------------------------
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Daftar Mutasi Kas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setUI(new RTextFieldUI());

        btnSearch = new JButton("Cari");
        btnSearch.setUI(new RButtonUI());
        btnSearch.addActionListener(e -> searchData());

        btnRefresh = new JButton("Refresh");
        btnRefresh.setUI(new RButtonUI());
        btnRefresh.addActionListener(e -> refreshData());

        searchPanel.add(new JLabel("Cari:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // -------------------------
        // TABLE MUTASI KAS
        // -------------------------
        model = new DefaultTableModel(
                new Object[]{"Code", "Tanggal", "Sumber", "Tujuan", "Total Jumlah", "Keterangan", "Aksi"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }        
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionForeground(new Color(0, 0, 0));
        table.setGridColor(new Color(240, 240, 240));
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditorMutasiKas(new JCheckBox(), table));
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setMinWidth(60);

        spTable = new JScrollPane(table);
        spTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getViewport().setBackground(Color.WHITE);

        // -------------------------
        // PANEL PAGINATION
        // -------------------------
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setOpaque(false);

        btnPrev = new JButton("<< Prev");
        btnPrev.setUI(new RButtonUI());
        btnPrev.addActionListener(e -> previousPage());

        btnNext = new JButton("Next >>");
        btnNext.setUI(new RButtonUI());
        btnNext.addActionListener(e -> nextPage());

        navPanel.add(btnPrev);
        navPanel.add(btnNext);

        // -------------------------
        // ADD KE PANEL UTAMA
        // -------------------------
        add(topPanel, BorderLayout.NORTH);
        add(spTable, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);

        // -------------------------
        // LOAD DATA AWAL
        // -------------------------
        loadAllData();
        refreshTable();
    }

    // -------------------------
    // METHOD DATA
    // -------------------------
    private void loadAllData() {
        allData.clear();

        MutasiKasDAO dao = new MutasiKasDAO();
        CoaDAO coaDAO = new CoaDAO();

        List<ModelMutasiKas> list = dao.getSummaryAll(); // gunakan summary grouped by code

        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        for (ModelMutasiKas m : list) {

            String tglUI = "";
            try {
                LocalDate ld = LocalDate.parse(m.getTanggal().toString().split(" ")[0], dbFmt); // ambil date saja
                tglUI = ld.format(uiFmt);
            } catch (Exception e) {
                tglUI = m.getTanggal().toString();
            }

            ModelCoa sumberCoa = coaDAO.findByCode(m.getSumberCode());
            String sumberName = (sumberCoa != null) ? sumberCoa.getNama() : "Unknown (" + m.getSumberCode() + ")";

            allData.add(new Object[]{
                    m.getCode(),
                    tglUI,
                    sumberName,
                    m.getJumlah(), // total sum
                    m.getKeterangan().split(" | Tujuan: ")[0], // ambil keterangan asli
            });
        }

        totalRows = allData.size();
    }

    private void refreshTable() {
        model.setRowCount(0);
    
        MutasiKasDAO dao = new MutasiKasDAO();
        CoaDAO coaDAO = new CoaDAO();
    
        int offset = (currentPage - 1) * rowsPerPage;
    
        // Asumsi ada method findPageSummary(int limit, int offset) di DAO
        // Jika belum, implementasikan mirip findPage di PenjualanDAO, tapi dengan GROUP BY
        List<ModelMutasiKas> pageData = dao.findPageSummary(rowsPerPage, offset); 
    
        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    
        for (ModelMutasiKas m : pageData) {
    
            // format tanggal
            String tglUI;
            try {
                LocalDate ld = LocalDate.parse(m.getTanggal().toString().split(" ")[0], dbFmt);
                tglUI = ld.format(uiFmt);
            } catch (Exception e) {
                tglUI = m.getTanggal().toString();
            }
    
            // ambil nama sumber
            ModelCoa sumberCoa = coaDAO.findByCode(m.getSumberCode());
            String sumberName = (sumberCoa != null) ? sumberCoa.getNama() : m.getSumberCode();
    
            model.addRow(new Object[]{
                    m.getCode(),
                    tglUI,
                    sumberName,
                    m.getJumlah(),
                    m.getKeterangan().split(" | Tujuan: ")[0],
            });
        }
    
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(offset + rowsPerPage < totalRows);
    }    

    private void searchData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        MutasiKasDAO dao = new MutasiKasDAO();
        CoaDAO coaDAO = new CoaDAO();
    
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
    
        // Asumsi ada method searchSummary(String keyword) di DAO
        // Yang mencari di code, tanggal, sumber_code, tujuan_code, keterangan dengan GROUP BY
        List<ModelMutasiKas> list = dao.searchSummary(rowsPerPage, 0, keyword);
        model.setRowCount(0);
    
        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    
        for (ModelMutasiKas m : list) {
    
            String tglUI;
            try {
                LocalDate ld = LocalDate.parse(m.getTanggal().toString().split(" ")[0], dbFmt);
                tglUI = ld.format(uiFmt);
            } catch (Exception e) {
                tglUI = m.getTanggal().toString();
            }
    
            ModelCoa sumberCoa = coaDAO.findByCode(m.getSumberCode());
            String sumberName = (sumberCoa != null) ? sumberCoa.getNama() : m.getSumberCode();
    
            model.addRow(new Object[]{
                    m.getCode(),
                    tglUI,
                    sumberName,
                    m.getJumlah(),
                    m.getKeterangan().split(" | Tujuan: ")[0],
            });
        }
    
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
    }    

    private void refreshData() {
        MutasiKasDAO dao = new MutasiKasDAO();
        totalRows = dao.countAllSummary();   // method baru di DAO: hitung jumlah unique code
        currentPage = 1;
        refreshTable();
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshTable();
        }
    }

    private void nextPage() {
        int totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            refreshTable();
        }
    }
}