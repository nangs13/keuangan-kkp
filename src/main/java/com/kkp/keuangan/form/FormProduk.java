package com.kkp.keuangan.form;

import com.kkp.keuangan.backend.dao.ProdukDAO;
import com.kkp.keuangan.backend.model.ModelProduk;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.swing.ScrollBar;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormProduk extends JPanel {

    private JTextField txtNama;
    private JTextField txtHarga;
    private JTextField txtStok;
    private JComboBox<String> cbKategori;
    private JButton btnSimpan;
    private JButton btnEdit;
    private JButton btnHapus;
    private JButton btnBatal;
    private JButton btnRefresh;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane spTable;

    private ProdukDAO produkDao = new ProdukDAO();
    private Integer selectedId = null;

    public FormProduk() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        // -------------------------
        // FORM INPUT
        // -------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblTitle = new JLabel("Form Produk");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblNama = new JLabel("Nama Produk");
        JLabel lblHarga = new JLabel("Harga");
        JLabel lblStok = new JLabel("Stok");
        JLabel lblKategori = new JLabel("Kategori");

        txtNama = new JTextField();
        txtNama.setUI(new RTextFieldUI());

        txtHarga = new JTextField();
        txtHarga.setUI(new RTextFieldUI());

        txtStok = new JTextField();
        txtStok.setUI(new RTextFieldUI());

        cbKategori = new JComboBox<>(new String[]{
            "Makanan", "Minuman"
        });
        cbKategori.setUI(new RComboBoxUI());

        btnSimpan = new JButton("Simpan");
        btnSimpan.setUI(new RButtonUI());
        btnSimpan.addActionListener(e -> tambahProduk());

        btnEdit = new JButton("Edit");
        btnEdit.setUI(new RButtonUI());
        btnEdit.addActionListener(e -> editProduk());

        btnHapus = new JButton("Hapus");
        btnHapus.setUI(new RButtonUI());
        btnHapus.addActionListener(e -> hapusProduk());

        btnBatal = new JButton("Batal");
        btnBatal.setUI(new RButtonUI());
        btnBatal.addActionListener(e -> resetForm());

        btnRefresh = new JButton("Refresh");
        btnRefresh.setUI(new RButtonUI());
        btnRefresh.addActionListener(e -> loadData());

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(lblNama, gbc);
        gbc.gridx = 1; formPanel.add(txtNama, gbc);

        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(lblHarga, gbc);
        gbc.gridx = 1; formPanel.add(txtHarga, gbc);

        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(lblStok, gbc);
        gbc.gridx = 1; formPanel.add(txtStok, gbc);

        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(lblKategori, gbc);
        gbc.gridx = 1; formPanel.add(cbKategori, gbc);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButton.setOpaque(false);
        panelButton.add(btnSimpan);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(btnBatal);
        panelButton.add(btnRefresh);
        formPanel.add(panelButton, gbc);

        // -------------------------
        // TABLE
        // -------------------------
        model = new DefaultTableModel(
            new Object[]{"ID", "Nama Produk", "Harga", "Stok", "Kategori"}, 0
        ) {
            // disable editing in table cells
            @Override
            public boolean isCellEditable(int row, int column) {
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

        // hide ID column (make it present but not visible)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // on row click -> load to form for edit/delete
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    Integer id = (Integer) model.getValueAt(r, 0);
                    String nama = model.getValueAt(r, 1).toString();
                    String harga = model.getValueAt(r, 2).toString();
                    String stok = model.getValueAt(r, 3).toString();
                    String kategori = model.getValueAt(r, 4).toString();

                    selectedId = id;
                    txtNama.setText(nama);
                    txtHarga.setText(harga);
                    txtStok.setText(stok);
                    cbKategori.setSelectedItem(kategori);
                }
            }
        });

        spTable = new JScrollPane(table);
        spTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getViewport().setBackground(Color.WHITE);

        // -------------------------
        // ADD TO MAIN PANEL
        // -------------------------
        add(formPanel, BorderLayout.NORTH);
        add(spTable, BorderLayout.CENTER);

        // load initial data
        loadData();
    }

    private void tambahProduk() {
        String nama = txtNama.getText().trim();
        String harga = txtHarga.getText().trim();
        String stok = txtStok.getText().trim();
        String kategori = cbKategori.getSelectedItem().toString();

        if (nama.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua field terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double h = Double.parseDouble(harga);
            int s = Integer.parseInt(stok);
            ModelProduk p = new ModelProduk(nama, h, s, kategori);
            produkDao.insert(p);
            loadData();
            resetForm();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduk() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah dari tabel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nama = txtNama.getText().trim();
        String harga = txtHarga.getText().trim();
        String stok = txtStok.getText().trim();
        String kategori = cbKategori.getSelectedItem().toString();

        if (nama.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua field terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double h = Double.parseDouble(harga);
            int s = Integer.parseInt(stok);
            ModelProduk p = new ModelProduk(selectedId, nama, h, s, kategori);
            produkDao.update(p);
            loadData();
            resetForm();
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan stok harus angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal update: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusProduk() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus dari tabel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            produkDao.delete(selectedId);
            loadData();
            resetForm();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<ModelProduk> list = produkDao.findAll();
            for (ModelProduk p : list) {
                model.addRow(new Object[]{p.getId(), p.getNama(), p.getHarga(), p.getStok(), p.getKategori()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        selectedId = null;
    }

    private void resetForm() {
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        cbKategori.setSelectedIndex(0);
        selectedId = null;
        table.clearSelection();
    }
}
