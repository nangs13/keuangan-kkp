package com.kkp.keuangan.form;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.kkp.keuangan.backend.dao.CustomerDAO;
import com.kkp.keuangan.backend.model.ModelCustomer;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.swing.ScrollBar;

public class FormCustomer extends javax.swing.JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private DefaultTableModel tableModel;

    public FormCustomer() {
        initComponents();
        applyCustomUI();
        setupTable();
        loadData();
    }

    private void setupTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Pelanggan", "Total Hutang"}, 0);
        tblCustomer.setModel(tableModel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<ModelCustomer> list = customerDAO.findAll();
        for (ModelCustomer c : list) {
            tableModel.addRow(new Object[]{c.getId(), c.getNama(), c.getHutang()});
        }
    }

    private void applyCustomUI() {
        txtId.setUI(new RTextFieldUI());
        txtNama.setUI(new RTextFieldUI());
        txtHutang.setUI(new RTextFieldUI());

        btnSimpan.setUI(new RButtonUI());
        btnUbah.setUI(new RButtonUI());
        btnHapus.setUI(new RButtonUI());
        btnBersihkan.setUI(new RButtonUI());
        btnTambahHutang.setUI(new RButtonUI());

        tblCustomer.setRowHeight(28);
        tblCustomer.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        tblCustomer.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        tblCustomer.getTableHeader().setBackground(new java.awt.Color(230, 230, 230));
        tblCustomer.setSelectionBackground(new java.awt.Color(200, 220, 255));
        tblCustomer.setGridColor(new java.awt.Color(240, 240, 240));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));
        jScrollPane1.getViewport().setBackground(java.awt.Color.WHITE);
        jScrollPane1.setVerticalScrollBar(new ScrollBar());
    }

    public void cariNama(String nama) {
        tableModel.setRowCount(0);
        List<ModelCustomer> list = customerDAO.findByName(nama);
        for (ModelCustomer c : list) {
            tableModel.addRow(new Object[]{c.getId(), c.getNama(), c.getHutang()});
        }
    }

    private void kosongkanForm() {
        txtId.setText("");
        txtNama.setText("");
        txtHutang.setText("");
    }

    private boolean validasiInput() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pelanggan tidak boleh kosong!");
            return false;
        }
        try {
            double hutang = Double.parseDouble(txtHutang.getText());
            if (hutang < 0) {
                JOptionPane.showMessageDialog(this, "Nominal hutang harus bernilai positif!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kolom hutang harus berupa angka!");
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        txtHutang = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        btnTambahHutang = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();

        jLabel1.setText("ID Pelanggan:");
        jLabel2.setText("Nama Pelanggan:");
        jLabel3.setText("Total Hutang:");

        txtId.setEditable(false);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(evt -> btnSimpanActionPerformed(evt));

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(evt -> btnUbahActionPerformed(evt));

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(evt -> btnHapusActionPerformed(evt));

        btnBersihkan.setText("Bersihkan");
        btnBersihkan.addActionListener(evt -> btnBersihkanActionPerformed(evt));

        btnTambahHutang.setText("Tambah / Kurangi Hutang");
        btnTambahHutang.addActionListener(evt -> btnTambahHutangActionPerformed(evt));

        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nama Pelanggan", "Total Hutang"}
        ));
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtId)
                            .addComponent(txtNama)
                            .addComponent(txtHutang)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBersihkan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTambahHutang)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtHutang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus)
                    .addComponent(btnBersihkan)
                    .addComponent(btnTambahHutang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );
    }

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validasiInput()) return;
        ModelCustomer c = new ModelCustomer(0, txtNama.getText(), Double.parseDouble(txtHutang.getText()));
        customerDAO.insert(c);
        loadData();
        kosongkanForm();
        JOptionPane.showMessageDialog(this, "Data pelanggan berhasil disimpan!");
    }

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pelanggan dari tabel terlebih dahulu!");
            return;
        }
        if (!validasiInput()) return;
        ModelCustomer c = new ModelCustomer(Integer.parseInt(txtId.getText()), txtNama.getText(), Double.parseDouble(txtHutang.getText()));
        customerDAO.update(c);
        loadData();
        kosongkanForm();
        JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diperbarui!");
    }

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pelanggan dahulu!");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin hapus data pelanggan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            customerDAO.delete(Integer.parseInt(txtId.getText()));
            loadData();
            kosongkanForm();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil dihapus!");
        }
    }

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {
        kosongkanForm();
    }

    private void btnTambahHutangActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan dulu!");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Masukkan nominal hutang (+ tambah / - kurangi):");
        if (input != null) {
            try {
                double nominal = Double.parseDouble(input);
                customerDAO.addHutang(Integer.parseInt(txtId.getText()), nominal);
                loadData();
                JOptionPane.showMessageDialog(this, "Hutang berhasil diperbarui!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Nominal harus angka!");
            }
        }
    }

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tblCustomer.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNama.setText(tableModel.getValueAt(row, 1).toString());
            txtHutang.setText(tableModel.getValueAt(row, 2).toString());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new javax.swing.JFrame() {{
                setContentPane(new FormCustomer());
                setTitle("Form Data Pelanggan");
                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                pack();
                setVisible(true);
            }};
        });
    }

    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambahHutang;
    private javax.swing.JButton btnUbah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtHutang;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNama;
}
