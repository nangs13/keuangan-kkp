package com.kkp.keuangan.form;

import com.kkp.keuangan.backend.dao.CardInfoDAO;
import com.kkp.keuangan.backend.dao.CoaDAO;
import com.kkp.keuangan.backend.model.ModelCardInfo;
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

public class FormCardInfo extends JPanel {

    private JTextField txtId;
    private JTextField txtNama;
    private JComboBox<ModelCoa> cbCoa;

    private JButton btnSimpan;
    private JButton btnEdit;
    private JButton btnHapus;
    private JButton btnBatal;
    private JButton btnRefresh;

    private JTable table;
    private DefaultTableModel model;
    private JScrollPane spTable;

    private final CardInfoDAO cardInfoDao = new CardInfoDAO();
    private final CoaDAO coaDao = new CoaDAO();
    private Integer selectedId = null;

    public FormCardInfo() {
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

        JLabel lblTitle = new JLabel("Form Card Info");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblNama = new JLabel("Nama Card");
        JLabel lblCoa = new JLabel("COA");

        txtId = new JTextField();
        txtId.setVisible(false);

        txtNama = new JTextField();
        txtNama.setUI(new RTextFieldUI());

        cbCoa = new JComboBox<>();
        cbCoa.setUI(new RComboBoxUI());

        btnSimpan = new JButton("Simpan");
        btnSimpan.setUI(new RButtonUI());
        btnSimpan.addActionListener(e -> tambahCardInfo());

        btnEdit = new JButton("Edit");
        btnEdit.setUI(new RButtonUI());
        btnEdit.addActionListener(e -> editCardInfo());

        btnHapus = new JButton("Hapus");
        btnHapus.setUI(new RButtonUI());
        btnHapus.addActionListener(e -> hapusCardInfo());

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
        formPanel.add(lblNama, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNama, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(lblCoa, gbc);
        gbc.gridx = 1;
        formPanel.add(cbCoa, gbc);

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
        // Table Card Info
        // -------------------------
        model = new DefaultTableModel(
                new Object[]{"ID", "Nama", "COA"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    selectedId = (Integer) model.getValueAt(r, 0);
                    txtNama.setText(model.getValueAt(r, 1).toString());

                    String coa = model.getValueAt(r, 2).toString();
                    if (coa != null) {
                        int coaId = (Integer) Integer.parseInt(coa);
                        boolean found = false;
                        ModelCoa currentCoa = getCoa(coaId);
                        for (int i = 1; i <= cbCoa.getItemCount(); i++) {
                            ModelCoa item = cbCoa.getItemAt(i);
                            if (item.getId() == currentCoa.getId()) {
                                cbCoa.setSelectedIndex(i);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            cbCoa.setSelectedIndex(0);
                        }
                    } else {
                        cbCoa.setSelectedIndex(0);
                    }
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

        loadParentCombo();
        loadData();
    }

    private void loadParentCombo() {
        cbCoa.removeAllItems();
        List<ModelCoa> list = coaDao.findAll();
        cbCoa.addItem(null);
        for(ModelCoa c: list){
            cbCoa.addItem(c);
        }
    }
    
    private ModelCoa getCoa(int id) {
        ModelCoa coa = coaDao.findById(id);
        return coa;
    }

    private void loadData() {
        model.setRowCount(0);
        List<ModelCardInfo> list = cardInfoDao.findAll();
        for(ModelCardInfo c: list){
            model.addRow(new Object[]{
                c.getId(),
                c.getNama(),
                c.getCoaId()
            });
        }
        selectedId = null;
    }

    private void resetForm() {
        txtId.setText("");
        txtNama.setText("");
        cbCoa.setSelectedIndex(0);
        selectedId = null;
        table.clearSelection();
    }

    private void tambahCardInfo() {
        String nama = txtNama.getText().trim();
        ModelCoa coa = (ModelCoa) cbCoa.getSelectedItem();
        if(nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Nama Card harus diisi!");
            return;
        }
        cardInfoDao.insert(new ModelCardInfo(coa.getId(), nama));
        loadData();
        resetForm();
    }

    private void editCardInfo() {
        if(selectedId == null) {
            JOptionPane.showMessageDialog(this,"Pilih data dulu!");
            return;
        }
        String nama = txtNama.getText().trim();
        ModelCoa coa = (ModelCoa) cbCoa.getSelectedItem();
        if(nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Nama Card harus diisi!");
            return;
        }
        ModelCardInfo m = new ModelCardInfo(selectedId, coa.getId(), nama);
        cardInfoDao.update(m);
        loadData();
        resetForm();
    }

    private void hapusCardInfo() {
        if(selectedId == null) {
            JOptionPane.showMessageDialog(this,"Pilih data dulu!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,"Yakin hapus Card Info ini?","Konfirmasi",JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        cardInfoDao.delete(selectedId);
        loadData();
        resetForm();
    }
}
