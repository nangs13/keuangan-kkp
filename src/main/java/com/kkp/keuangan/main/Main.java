/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kkp.keuangan.main;

import java.awt.Color;

import javax.swing.JComponent;

import com.kkp.keuangan.event.EventMenuSelected;
import com.kkp.keuangan.form.FormBiaya;
import com.kkp.keuangan.form.FormCardInfo;
import com.kkp.keuangan.form.FormCoa;
import com.kkp.keuangan.form.FormCustomer;
import com.kkp.keuangan.form.FormLaporanLabarugi;
import com.kkp.keuangan.form.FormMutasikas;
import com.kkp.keuangan.form.FormPembelian;
import com.kkp.keuangan.form.FormPenjualan;
import com.kkp.keuangan.form.FormProduk;
import com.kkp.keuangan.form.FormSupplier;
import com.kkp.keuangan.form.Form_1;
import com.kkp.keuangan.form.Form_2;
import com.kkp.keuangan.form.Form_3;
import com.kkp.keuangan.form.Form_Home;
import com.kkp.keuangan.form.HomePage;
import com.kkp.keuangan.laporan.LaporanArusKas;
import com.kkp.keuangan.laporan.LaporanKas;
import com.kkp.keuangan.laporan.LaporanNeraca;
import com.kkp.keuangan.laporan.LaporanLabaRugi;
import com.kkp.keuangan.model.Enum.MenuKey;
import com.kkp.keuangan.monitor.MonitorMutasiKas;
import com.kkp.keuangan.monitor.MonitorBiaya;
import com.kkp.keuangan.monitor.MonitorPembelian;
import com.kkp.keuangan.monitor.MonitorPenjualan;
/**
 *
 * @author RAVEN
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    private Form_Home home;
    private Form_1 form1;
    private Form_2 form2;
    private Form_3 form3;
    private HomePage homepage;

    // Form Page
    private FormProduk formProduk;
    private FormCoa formCoa;
    private FormCardInfo formCardInfo;
    private FormPenjualan formPenjualan;
    private FormSupplier formSupplier;
    private FormCustomer formCustomer;
    private FormPembelian formPembelian;
    private FormMutasikas formMutasikas;
    private FormBiaya formBiaya;

    // Monitor Page
    private MonitorPenjualan monitorPenjualan;
    private MonitorPembelian monitorPembelian;
    private MonitorMutasiKas monitorMutasiKas;
    private MonitorBiaya monitorBiaya;

    // Report Page
    private LaporanKas laporanKas;
    private LaporanArusKas laporanArusKas;
    private LaporanNeraca laporanNeraca;
    private LaporanLabaRugi laporanLabaRugi;
    
    public Main() {
        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        home = new Form_Home();
        homepage = new HomePage();

        // Form Page
        formProduk = new FormProduk();
        formCoa = new FormCoa();
        formCardInfo = new FormCardInfo();
        formPenjualan = new FormPenjualan();
        formCustomer = new FormCustomer();
        formSupplier = new FormSupplier();
        formPembelian = new FormPembelian();
        formMutasikas = new FormMutasikas();
        formBiaya = new FormBiaya();

        // Monitor Page
        monitorPenjualan = new MonitorPenjualan();
        monitorPembelian = new MonitorPembelian();
        monitorMutasiKas = new MonitorMutasiKas();
        monitorBiaya = new MonitorBiaya();

        // Report Page
        laporanKas = new LaporanKas();
        laporanArusKas = new LaporanArusKas();
        laporanNeraca = new LaporanNeraca();
        laporanLabaRugi = new LaporanLabaRugi();
        
        menu.initMoving(Main.this);
        menu.addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(MenuKey key) {
                switch (key) {
                    case DASHBOARD:
                        setForm(homepage);
                        break;
                    case COA:
                        setForm(formCoa);
                        break;
                    case CARD_INFO:
                        setForm(formCardInfo);
                        break;
                    case CUSTOMER:
                        setForm(formCustomer);
                        break;
                    case SUPPLIER:
                        setForm(formSupplier);
                        break;
                    case PRODUK:
                        setForm(formProduk);
                        break;
                    case PENJUALAN:
                        setForm(formPenjualan);
                        break;
                    case PEMBELIAN:
                        setForm(formPembelian);
                        break;
                    case BIAYA:
                        setForm(formBiaya);
                        break;
                    case MUTASI_KAS:
                        setForm(formMutasikas);
                        break;
                    case LAP_KAS:
                        setForm(laporanKas);
                        break;
                    case LAP_NERACA:
                        setForm(laporanNeraca);
                        break;
                    case LAP_LABA_RUGI:
                        setForm(laporanLabaRugi);
                        break;
                    case LAP_ARUS_KAS:
                        setForm(laporanArusKas);
                        break;
                    case MONITOR_PENJUALAN:
                        setForm(monitorPenjualan);
                        break;
                    case MONITOR_PEMBELIAN:
                        setForm(monitorPembelian);
                        break;
                    case MONITOR_MUTASI_KAS:
                        setForm(monitorMutasiKas);
                        break;
                    case MONITOR_BIAYA:
                        setForm(monitorBiaya);
                        break;
                    default:
                        // For empty
                        break;
                }
            }
        });        
        // set when system open start with home form
        setForm(new HomePage());
    }

    private void setForm(JComponent com) {
        mainPanel.removeAll();
        mainPanel.add(com);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new com.kkp.keuangan.swing.PanelBorder();
        menu = new com.kkp.keuangan.component.Menu();
        header2 = new com.kkp.keuangan.component.Header();
        mainPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        header2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
                panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(panelBorder1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(header2, javax.swing.GroupLayout.DEFAULT_SIZE, 965,
                                                Short.MAX_VALUE)
                                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap()))));
        panelBorder1Layout.setVerticalGroup(
                panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addComponent(header2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.kkp.keuangan.component.Header header2;
    private javax.swing.JPanel mainPanel;
    private com.kkp.keuangan.component.Menu menu;
    private com.kkp.keuangan.swing.PanelBorder panelBorder1;
    // End of variables declaration//GEN-END:variables
}
