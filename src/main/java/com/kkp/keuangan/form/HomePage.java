package com.kkp.keuangan.form;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {
    
    public HomePage() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
    }
    
    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Create welcome label with gradient text
        JLabel welcomeLabel = new JLabel("Selamat Datang") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient paint
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#1CB5E0"), 
                    0, getHeight(), Color.decode("#000046")
                );
                
                g2d.setPaint(gradient);
                g2d.setFont(getFont());
                
                // Draw the text
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        welcomeLabel.setPreferredSize(new Dimension(800, 80));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        add(welcomeLabel, gbc);
    }
    
    // For testing the component
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Home Page");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new HomePage());
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}