package com.kkp.keuangan.form;

import com.kkp.keuangan.backend.dao.SupplierDAO;
import com.kkp.keuangan.backend.model.ModelSupplier;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.swing.ScrollBar;





public class FormSupplier extends javax.swing.JPanel {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private DefaultTableModel tableModel;

    public FormSupplier() {
        initComponents();
        applyCustomUI();
        setupTable();
        loadData();
        
    }
    
    
    private void setupTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Supplier", "Total Hutang"}, 0);
        tblSupplier.setModel(tableModel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<ModelSupplier> list = supplierDAO.findAll();
        for (ModelSupplier s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getNama(), s.getHutang()});
        }
    }
    
    private void applyCustomUI() {
    // Atur style untuk textfield
    txtId.setUI(new RTextFieldUI());
    txtNama.setUI(new RTextFieldUI());
    txtHutang.setUI(new RTextFieldUI());

    // Atur style untuk tombol
    btnSimpan.setUI(new RButtonUI());
    btnUbah.setUI(new RButtonUI());
    btnHapus.setUI(new RButtonUI());
    btnBersihkan.setUI(new RButtonUI());
    btnTambahHutang.setUI(new RButtonUI());

    // Atur style untuk tabel dan scrollbar
    tblSupplier.setRowHeight(28);
    tblSupplier.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
    tblSupplier.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
    tblSupplier.getTableHeader().setBackground(new java.awt.Color(230, 230, 230));
    tblSupplier.setSelectionBackground(new java.awt.Color(200, 220, 255));
    tblSupplier.setGridColor(new java.awt.Color(240, 240, 240));

    jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));
    jScrollPane1.getViewport().setBackground(java.awt.Color.WHITE);
    jScrollPane1.setVerticalScrollBar(new ScrollBar());

}


    public void cariNama(String nama) {
        tableModel.setRowCount(0);
        List<ModelSupplier> list = supplierDAO.findByName(nama);
        for (ModelSupplier s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getNama(), s.getHutang()});
        }
    }

    private void kosongkanForm() {
        txtId.setText("");
        txtNama.setText("");
        txtHutang.setText("");
    }

    private boolean validasiInput() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama supplier tidak boleh kosong!");
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        tblSupplier = new javax.swing.JTable();

        jLabel1.setText("ID Supplier:");
        jLabel2.setText("Nama Supplier:");
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

        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "ID", "Nama Supplier", "Total Hutang" }
        ));
        tblSupplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSupplierMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSupplier);

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
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validasiInput()) return;
        ModelSupplier supplier = new ModelSupplier(0, txtNama.getText(), Double.parseDouble(txtHutang.getText()));
        supplierDAO.insert(supplier);
        loadData();
        kosongkanForm();
        JOptionPane.showMessageDialog(this, "Data supplier berhasil disimpan!");
    }
    

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data supplier dari tabel terlebih dahulu!");
            return;
        }
        if (!validasiInput()) return;
        ModelSupplier supplier = new ModelSupplier(Integer.parseInt(txtId.getText()), txtNama.getText(), Double.parseDouble(txtHutang.getText()));
        supplierDAO.update(supplier);
        loadData();
        kosongkanForm();
        JOptionPane.showMessageDialog(this, "Data supplier berhasil diperbarui!");
    }

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data supplier dari tabel terlebih dahulu!");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data supplier ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            supplierDAO.delete(Integer.parseInt(txtId.getText()));
            loadData();
            kosongkanForm();
            JOptionPane.showMessageDialog(this, "Data supplier berhasil dihapus!");
        }
    }

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {
        kosongkanForm();
    }

    private void btnTambahHutangActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih supplier dari tabel terlebih dahulu!");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Masukkan nominal hutang (positif untuk menambah, negatif untuk mengurangi):");
        if (input != null) {
            try {
                double nominal = Double.parseDouble(input);
                supplierDAO.addHutang(Integer.parseInt(txtId.getText()), nominal);
                loadData();
                JOptionPane.showMessageDialog(this, "Hutang berhasil diperbarui!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Nominal harus berupa angka!");
            }
        }
    }

    private void tblSupplierMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tblSupplier.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNama.setText(tableModel.getValueAt(row, 1).toString());
            txtHutang.setText(tableModel.getValueAt(row, 2).toString());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new javax.swing.JFrame() {{
                setContentPane(new FormSupplier());
                setTitle("Form Data Supplier");
                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                pack();
                setVisible(true);
            }};
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambahHutang;
    private javax.swing.JButton btnUbah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtHutang;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
}
