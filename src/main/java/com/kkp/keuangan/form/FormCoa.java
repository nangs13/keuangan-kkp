package com.kkp.keuangan.form;

import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.model.ModelCoa;
import com.kkp.keuangan.component.uis.RButtonUI;
import com.kkp.keuangan.component.uis.RComboBoxUI;
import com.kkp.keuangan.component.uis.RTextFieldUI;
import com.kkp.keuangan.swing.ScrollBar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FormCoa extends JPanel {

    private JTextField txtId;
    private JTextField txtCode;
    private JTextField txtNama;
    private JComboBox<String> cbType;
    private JComboBox<ModelCoa> cbParent;
    private JTextField txtBeginning;

    private JButton btnSimpan;
    private JButton btnEdit;
    private JButton btnHapus;
    private JButton btnBatal;
    private JButton btnRefresh;

    private JTable table;
    private DefaultTableModel model;
    private JScrollPane spTable;

    final CoaDAO coaDao = new CoaDAO();
    private Integer selectedId = null;

    public FormCoa() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));

        // -------------------------
        // FORM INPUT
        // -------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblTitle = new JLabel("Form COA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblCode = new JLabel("Code");
        JLabel lblNama = new JLabel("Nama COA");
        JLabel lblType = new JLabel("Type");
        JLabel lblParent = new JLabel("Parent");
        JLabel lblBeginning = new JLabel("Beginning");

        txtId = new JTextField();
        txtId.setVisible(false);

        txtCode = new JTextField();
        txtCode.setUI(new RTextFieldUI());
        txtCode.setEditable(false);

        txtNama = new JTextField();
        txtNama.setUI(new RTextFieldUI());

        cbType = new JComboBox<>(new String[]{"debit", "credit"});
        cbType.setUI(new RComboBoxUI());

        cbParent = new JComboBox<>();
        cbParent.setUI(new RComboBoxUI());

        txtBeginning = new JTextField();
        txtBeginning.setUI(new RTextFieldUI());

        btnSimpan = new JButton("Simpan");
        btnSimpan.setUI(new RButtonUI());
        btnSimpan.addActionListener(e -> tambahCoa());

        btnEdit = new JButton("Edit");
        btnEdit.setUI(new RButtonUI());
        btnEdit.addActionListener(e -> editCoa());

        btnHapus = new JButton("Hapus");
        btnHapus.setUI(new RButtonUI());
        btnHapus.addActionListener(e -> hapusCoa());

        btnBatal = new JButton("Batal");
        btnBatal.setUI(new RButtonUI());
        btnBatal.addActionListener(e -> resetForm());

        btnRefresh = new JButton("Refresh");
        btnRefresh.setUI(new RButtonUI());
        btnRefresh.addActionListener(e -> loadData());

        // -------------------------
        // Layout form
        // -------------------------
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(lblCode, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCode, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(lblNama, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNama, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(lblType, gbc);
        gbc.gridx = 1;
        formPanel.add(cbType, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(lblParent, gbc);
        gbc.gridx = 1;
        formPanel.add(cbParent, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(lblBeginning, gbc);
        gbc.gridx = 1;
        formPanel.add(txtBeginning, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButton.setOpaque(false);
        panelButton.add(btnSimpan);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(btnBatal);
        panelButton.add(btnRefresh);
        formPanel.add(panelButton, gbc);

        // -------------------------
        // Table COA
        // -------------------------
        model = new DefaultTableModel(
                new Object[]{"ID", "Code", "Nama", "Type", "Parent", "Beginning", "Debit", "Credit", "Ending"}, 0
        ) {
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

        // hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        loadParentCombo();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    selectedId = (Integer) model.getValueAt(r, 0);
                    txtCode.setText(model.getValueAt(r, 1).toString());
                    txtNama.setText(model.getValueAt(r, 2).toString());
                    cbType.setSelectedItem(model.getValueAt(r, 3).toString());
                    // parent set by code (optional)
                    String parent = model.getValueAt(r, 4).toString();
                    if (parent != null) {
                        int parentId = (Integer) Integer.parseInt(parent);
                        boolean found = false;
                        ModelCoa currentParent = getParent(parentId);
                        for (int i = 1; i <= cbParent.getItemCount(); i++) {
                            ModelCoa item = cbParent.getItemAt(i);
                            if (item.getId() == currentParent.getId()) {
                                cbParent.setSelectedIndex(i);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            cbParent.setSelectedIndex(0);
                        }
                    } else {
                        cbParent.setSelectedIndex(0);
                    }
                    txtBeginning.setText(model.getValueAt(r, 5).toString());
                }
            }
        });

        spTable = new JScrollPane(table);
        spTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getViewport().setBackground(Color.WHITE);

        // -------------------------
        // Add to main panel
        // -------------------------
        add(formPanel, BorderLayout.NORTH);
        add(spTable, BorderLayout.CENTER);

        loadData();
        // loadParentCombo();
    }

    private void loadParentCombo() {
        cbParent.removeAllItems();
        List<ModelCoa> list = coaDao.findAll();
        cbParent.addItem(null);
        for (ModelCoa c : list) {
            cbParent.addItem(c);
        }
    }

    private ModelCoa getParent(int id) {
        ModelCoa parent = coaDao.findById(id);
        return parent;
    }

    private void loadData() {
        model.setRowCount(0);
        List<ModelCoa> list = coaDao.findAll();
        for (ModelCoa c : list) {
            model.addRow(new Object[]{
                c.getId(), c.getCode(), c.getNama(), c.getType(),
                c.getParentId() != null ? c.getParentId() : "",
                c.getBeginning(), c.getDebit(), c.getCredit(), c.getEnding()
            });
        }
        selectedId = null;
    }

    private void resetForm() {
        txtId.setText("");
        txtCode.setText("");
        txtNama.setText("");
        cbType.setSelectedIndex(0);
        cbParent.setSelectedIndex(0);
        txtBeginning.setText("0");
        selectedId = null;
        table.clearSelection();
    }

    private void tambahCoa() {
        String nama = txtNama.getText().trim();
        String type = cbType.getSelectedItem().toString();
        ModelCoa parent = (ModelCoa) cbParent.getSelectedItem();
        Integer parentId = parent != null ? parent.getId() : null;
        String parentCode = parent != null ? parent.getCode() : "";
        double beginning;
        try {
            beginning = Double.parseDouble(txtBeginning.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Beginning harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama COA harus diisi!");
            return;
        }

        coaDao.addChildCoa(nama, type, parentCode, parentId, beginning);
        loadData();
        resetForm();
        loadParentCombo();
    }

    private void editCoa() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            return;
        }

        try {
            ModelCoa coa = new ModelCoa();
            ModelCoa parent = (ModelCoa) cbParent.getSelectedItem();
            String type = cbType.getSelectedItem().toString();
            coa.setId(selectedId); // ID data yang dipilih
            coa.setCode(txtCode.getText());
            coa.setNama(txtNama.getText());
            coa.setType(type);
            coa.setParentId(parent != null ? parent.getId() : null);
            coa.setBeginning(Double.parseDouble(txtBeginning.getText()));

            coaDao.update(coa);

            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            loadData();
            resetForm();
            loadParentCombo();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Pastikan semua angka valid!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusCoa() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus COA ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        coaDao.delete(selectedId);
        loadData();
        resetForm();
        loadParentCombo();
    }
}
