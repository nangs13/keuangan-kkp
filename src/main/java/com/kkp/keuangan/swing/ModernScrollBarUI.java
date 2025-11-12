package com.kkp.keuangan.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private static final int SCROLL_BAR_ALPHA_ROLLOVER = 150;  // Hover: agak gelap
    private static final int SCROLL_BAR_ALPHA = 70;            // Normal: sedikit terlihat
    private static final int THUMB_BORDER_RADIUS = 8;
    private static final int THUMB_SIZE = 8;
    private static final Color THUMB_COLOR = new Color(0, 0, 0); // Hitam

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new InvisibleScrollBarButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new InvisibleScrollBarButton();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Kosong → track transparan
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
        int orientation = scrollbar.getOrientation();
        int width = orientation == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width;
        int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height : THUMB_SIZE;

        // Pastikan minimal ukuran
        width = Math.max(width, THUMB_SIZE);
        height = Math.max(height, THUMB_SIZE);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                           java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Posisi tengah
        int x = thumbBounds.x + (thumbBounds.width - width) / 2;
        int y = thumbBounds.y + (thumbBounds.height - height) / 2;

        g2.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
        g2.fillRoundRect(x, y, width, height, THUMB_BORDER_RADIUS, THUMB_BORDER_RADIUS);
        g2.dispose();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        scrollbar.setUnitIncrement(16); // Smooth scroll
    }

    private static class InvisibleScrollBarButton extends JButton {
        private InvisibleScrollBarButton() {
            setOpaque(false);
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        public void paint(Graphics g) {
            // Tidak menggambar apa-apa
        }
    }
}