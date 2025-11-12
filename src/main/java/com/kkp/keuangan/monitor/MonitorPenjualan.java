package com.kkp.keuangan.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.swing.ScrollBar;

public class MonitorPenjualan extends JPanel {

    private JTextField txtSearch;
    private JButton btnPrev, btnNext, btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane spTable;

    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalRows = 0;

    private List<Object[]> allData = new ArrayList<>();

    public MonitorPenjualan() {
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

        JLabel lblTitle = new JLabel("Daftar Penjualan");
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
                new Object[]{"ID", "Tanggal", "Customer", "Total", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setGridColor(new Color(240, 240, 240));

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
        // contoh dummy data
        allData.clear();
        for (int i = 1; i <= 105; i++) {
            allData.add(new Object[]{i, "2025-11-" + ((i % 30) + 1), "Customer " + i, (i * 50000), "Selesai"});
        }
        totalRows = allData.size();
    }

    private void refreshTable() {
        model.setRowCount(0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, totalRows);

        for (int i = start; i < end; i++) {
            model.addRow(allData.get(i));
        }

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(end < totalRows);
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }

        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : allData) {
            String customer = row[2].toString().toLowerCase();
            String status = row[4].toString().toLowerCase();
            if (customer.contains(keyword) || status.contains(keyword)) {
                filtered.add(row);
            }
        }

        model.setRowCount(0);
        for (Object[] row : filtered) {
            model.addRow(row);
        }

        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
    }

    private void refreshData() {
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
