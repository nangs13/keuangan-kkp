package com.kkp.keuangan.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.kkp.keuangan.component.uis.RButtonUI;

public class Header extends javax.swing.JPanel {

    private JButton btnMinimize;
    private JButton btnMaximize;
    private JButton btnClose;
    private Point initialClick;

    public Header() {
        initComponents();
        setOpaque(false);

        // Tambahkan panel tombol ke kanan atas
        JPanel windowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowButtons.setOpaque(false);

        btnMinimize = makeButton("−", new Color(120, 120, 120));   // Abu
        btnMaximize = makeButton("□", new Color(120, 120, 120));   // Abu
        btnClose    = makeButton("×", new Color(232, 17, 35));     // MERAH (Windows 11)

        windowButtons.add(btnMinimize);
        windowButtons.add(btnMaximize);
        windowButtons.add(btnClose);

        // Ganti layout utama sedikit biar tombol di kanan atas
        setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // leftPanel.add(jLabel1);
        // leftPanel.add(searchText1);

        add(leftPanel, BorderLayout.CENTER);
        // add(jLabel2, BorderLayout.WEST);
        add(windowButtons, BorderLayout.EAST);

        // Aksi tombol window
        btnMinimize.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setState(Frame.ICONIFIED);
        });

        btnMaximize.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                frame.setExtendedState(Frame.NORMAL);
            } else {
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });

        btnClose.addActionListener(e -> {
            System.exit(0);
        });

        // Tambahkan kemampuan drag window
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(Header.this);
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                frame.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
    }

    private JButton makeButton(String icon, Color hoverColor) {
        JButton btn = new JButton();
        btn.setUI(new RButtonUI());
        btn.setPreferredSize(new Dimension(46, 32));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));

        // Set ikon (bisa pakai text atau SVG nanti)
        btn.setText(icon);

        // Hover effect
        Color normalBg = new Color(0, 0, 0, 0); // transparan
        btn.setBackground(normalBg);

        btn.addMouseListener(new MouseAdapter() {
            private final Timer timer = new Timer(10, null);
            private int alpha = 0;
            private final int targetAlpha;
            private final Color baseColor;

            {
                baseColor = hoverColor;
                targetAlpha = hoverColor.getAlpha();
                timer.addActionListener(e -> {
                    alpha = Math.min(255, Math.max(0, alpha + (btn.getMousePosition() != null ? 25 : -25)));
                    btn.setBackground(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha));
                    if (alpha == 0 || alpha == targetAlpha) timer.stop();
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                timer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                timer.start();
            }
        });

        return btn;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        searchText1 = new com.kkp.keuangan.swing.SearchText();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kkp/keuangan/icon/search.png"))); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kkp/keuangan/icon/menu.png"))); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(0, 0, 25, getHeight());
        g2.fillRect(getWidth() - 25, getHeight() - 25, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private com.kkp.keuangan.swing.SearchText searchText1;
    // End of variables declaration
}
