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

import com.kkp.keuangan.backend.dao.SupplierDAO;
import com.kkp.keuangan.backend.dao.PembelianDAO;
import com.kkp.keuangan.backend.model.ModelSupplier;
import com.kkp.keuangan.backend.model.ModelPembelian;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.monitor.component.ButtonEditorPembelian;
import com.kkp.keuangan.monitor.component.ButtonRenderer;
import com.kkp.keuangan.swing.ScrollBar;

public class MonitorPembelian extends JPanel {

    private JTextField txtSearch;
    private JButton btnPrev, btnNext, btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane spTable;

    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalRows = 0;

    private List<Object[]> allData = new ArrayList<>();

    public MonitorPembelian() {
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

        JLabel lblTitle = new JLabel("Daftar Pembelian");
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
        // TABLE PENJUALAN
        // -------------------------
        model = new DefaultTableModel(
                new Object[]{"ID", "Tanggal", "Supplier", "Total", "Aksi"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }        
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionForeground(new Color(0,0,0));
        table.setGridColor(new Color(240, 240, 240));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditorPembelian(new JCheckBox(), table));
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        table.getColumnModel().getColumn(4).setMinWidth(60);

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

        PembelianDAO dao = new PembelianDAO();
        SupplierDAO supplierDAO = new SupplierDAO();

        List<ModelPembelian> list = dao.findAll();

        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        for (ModelPembelian p : list) {

            String tglUI = "";
            try {
                LocalDate ld = LocalDate.parse(p.getTanggalPembelian(), dbFmt);
                tglUI = ld.format(uiFmt);
            } catch (Exception e) {
                tglUI = p.getTanggalPembelian();
            }

            ModelSupplier c = supplierDAO.findById(p.getSupplierId());
            String supplierName = (c != null) ? c.getNama() : "Unknown (" + p.getSupplierId() + ")";

            allData.add(new Object[]{
                    p.getId(),
                    tglUI,
                    supplierName,
                    p.getTotal(),
            });
        }

        totalRows = allData.size();
    }

    private void refreshTable() {
        model.setRowCount(0);
    
        PembelianDAO dao = new PembelianDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
    
        int offset = (currentPage - 1) * rowsPerPage;
        List<ModelPembelian> pageData = dao.findPage(rowsPerPage, offset);
    
        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    
        for (ModelPembelian p : pageData) {
    
            // format tanggal
            String tglUI;
            try {
                LocalDate ld = LocalDate.parse(p.getTanggalPembelian(), dbFmt);
                tglUI = ld.format(uiFmt);
            } catch (Exception e) {
                tglUI = p.getTanggalPembelian();
            }
    
            // ambil customer name
            ModelSupplier c = supplierDAO.findById(p.getSupplierId());
            String supplierName = (c != null) ? c.getNama() : ("ID " + p.getSupplierId());
    
            model.addRow(new Object[]{
                    p.getId(),
                    tglUI,
                    supplierName,
                    p.getTotal(),
            });
        }
    
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(offset + rowsPerPage < totalRows);
    }    

    private void searchData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        PembelianDAO dao = new PembelianDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
    
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
    
        List<ModelPembelian> list = dao.search(keyword);
        model.setRowCount(0);
    
        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    
        for (ModelPembelian p : list) {
    
            String tglUI;
            try {
                LocalDate ld = LocalDate.parse(p.getTanggalPembelian(), dbFmt);
                tglUI = ld.format(uiFmt);
            } catch (Exception e) {
                tglUI = p.getTanggalPembelian();
            }
    
            ModelSupplier c = supplierDAO.findById(p.getSupplierId());
            String supplierName = (c != null) ? c.getNama() : ("ID " + p.getSupplierId());
    
            model.addRow(new Object[]{
                    p.getId(),
                    tglUI,
                    supplierName,
                    p.getTotal(),
            });
        }
    
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
    }    

    private void refreshData() {
        PembelianDAO dao = new PembelianDAO();
        totalRows = dao.countAll();   // ambil total data dari DB
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
