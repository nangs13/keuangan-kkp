package com.kkp.keuangan.component;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.kkp.keuangan.event.EventMenuSelected;
import com.kkp.keuangan.model.Model_Menu;
import com.kkp.keuangan.model.Enum.MenuKey;
import com.kkp.keuangan.model.Enum.MenuType;
import com.kkp.keuangan.swing.ModernScrollBarUI;

public class Menu extends javax.swing.JPanel {

    private EventMenuSelected event;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
        listMenu1.addEventMenuSelected(event);
    }

    public Menu() {
        initComponents();
        setOpaque(false);
        listMenu1.setOpaque(false);
        init();
    }

    private void init() {
        // =========================
        // 📂 MENU APLIKASI UTAMA
        // =========================
        listMenu1.addItem(new Model_Menu("HOME", "Dashboard", MenuType.MENU, MenuKey.DASHBOARD));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));

        // =========================
        // 📘 MASTER DATA
        // =========================
        listMenu1.addItem(new Model_Menu("", "Master Data", MenuType.TITLE, MenuKey.TITLE));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));
        listMenu1.addItem(new Model_Menu("BOOK", "COA", MenuType.MENU, MenuKey.COA));
        listMenu1.addItem(new Model_Menu("ID_CARD", "Info Kartu", MenuType.MENU, MenuKey.CARD_INFO));
        listMenu1.addItem(new Model_Menu("USERS", "Pelanggan", MenuType.MENU, MenuKey.CUSTOMER));
        listMenu1.addItem(new Model_Menu("TRUCK", "Supplier", MenuType.MENU, MenuKey.SUPPLIER));
        listMenu1.addItem(new Model_Menu("BOX", "Produk", MenuType.MENU, MenuKey.PRODUK));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));

        // =========================
        // 💵 TRANSAKSI
        // =========================
        listMenu1.addItem(new Model_Menu("", "Transaksi", MenuType.TITLE, MenuKey.TITLE));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));
        listMenu1.addItem(new Model_Menu("CASH_REGISTER", "Penjualan", MenuType.MENU, MenuKey.PENJUALAN));
        listMenu1.addItem(new Model_Menu("CHART_LINE", "Monitor Penjualan", MenuType.MENU, MenuKey.MONITOR_PENJUALAN));
        listMenu1.addItem(new Model_Menu("SHOPPING_CART", "Pembelian", MenuType.MENU, MenuKey.PEMBELIAN));
        listMenu1.addItem(new Model_Menu("CHART_BAR", "Monitor Pembelian", MenuType.MENU, MenuKey.MONITOR_PEMBELIAN));
        listMenu1.addItem(new Model_Menu("MONEY_BILL_WAVE", "Biaya", MenuType.MENU, MenuKey.BIAYA));
        listMenu1.addItem(new Model_Menu("FILE_INVOICE_DOLLAR", "Monitor Biaya", MenuType.MENU, MenuKey.MONITOR_BIAYA));
        listMenu1.addItem(new Model_Menu("EXCHANGE_ALT", "Mutasi Kas", MenuType.MENU, MenuKey.MUTASI_KAS));
        listMenu1.addItem(new Model_Menu("CLIPBOARD_LIST", "Monitor Mutasi Kas", MenuType.MENU, MenuKey.MONITOR_MUTASI_KAS));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));

        // =========================
        // 📊 LAPORAN
        // =========================
        listMenu1.addItem(new Model_Menu("", "Laporan", MenuType.TITLE, MenuKey.TITLE));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));
        listMenu1.addItem(new Model_Menu("WALLET", "Kas", MenuType.MENU, MenuKey.LAP_KAS));
        listMenu1.addItem(new Model_Menu("BALANCE_SCALE", "Neraca", MenuType.MENU, MenuKey.LAP_NERACA));
        listMenu1.addItem(new Model_Menu("CHART_AREA", "Laba Rugi", MenuType.MENU, MenuKey.LAP_LABA_RUGI));
        listMenu1.addItem(new Model_Menu("STREAM", "Arus Kas", MenuType.MENU, MenuKey.LAP_ARUS_KAS));
        listMenu1.addItem(new Model_Menu("", "", MenuType.EMPTY, MenuKey.EMPTY));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMoving = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        listMenu1 = new com.kkp.keuangan.swing.ListMenu<>();
        listMenu1.setOpaque(true);
        listMenu1.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(listMenu1);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // TERAPKAN MODERN SCROLLBAR
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(12, Integer.MAX_VALUE));

        // Opsional: muncul hanya saat hover (macOS style)
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                scrollPane.getVerticalScrollBar().setVisible(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!scrollPane.getVerticalScrollBar().getBounds().contains(e.getPoint())) {
                    // Delay hide agar tidak kedip
                    javax.swing.Timer timer = new javax.swing.Timer(1000, ev -> {
                        if (!scrollPane.getVerticalScrollBar().isShowing()) return;
                        java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
                        java.awt.Rectangle bounds = scrollPane.getVerticalScrollBar().getBounds();
                        java.awt.Point loc = scrollPane.getLocationOnScreen();
                        if (!new java.awt.Rectangle(loc.x + bounds.x, loc.y + bounds.y, bounds.width, bounds.height).contains(p)) {
                            scrollPane.getVerticalScrollBar().setVisible(false);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });

        // Mulai: scrollbar tidak terlihat
        scrollPane.getVerticalScrollBar().setVisible(false);

        panelMoving.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kkp/keuangan/icon/logo.png"))); // NOI18N
        jLabel1.setText("Kedai Budhe");

        javax.swing.GroupLayout panelMovingLayout = new javax.swing.GroupLayout(panelMoving);
        panelMoving.setLayout(panelMovingLayout);
        panelMovingLayout.setHorizontalGroup(
            panelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMovingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMovingLayout.setVerticalGroup(
            panelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMovingLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMoving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelMoving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(0, 0, Color.decode("#1CB5E0"), 0, getHeight(), Color.decode("#000046"));
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(getWidth() - 20, 0, getWidth(), getHeight());
        super.paintChildren(grphcs);
    }

    private int x;
    private int y;

    public void initMoving(JFrame fram) {
        panelMoving.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                x = me.getX();
                y = me.getY();
            }

        });
        panelMoving.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                fram.setLocation(me.getXOnScreen() - x, me.getYOnScreen() - y);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private com.kkp.keuangan.swing.ListMenu<String> listMenu1;
    private javax.swing.JPanel panelMoving;
    // End of variables declaration//GEN-END:variables
}
