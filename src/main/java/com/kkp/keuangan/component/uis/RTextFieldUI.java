package com.kkp.keuangan.component.uis;

import com.kkp.keuangan.components.utils.RDefaultUI;
import com.kkp.keuangan.components.utils.RShadowRender;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 *
 * @author Raven
 */
public class RTextFieldUI extends BasicTextFieldUI {

    public static ComponentUI createUI(JComponent c) {
        return new RTextFieldUI();
    }

    private RShadowRender rShadowRender;

    public RTextFieldUI() {
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        init((JTextField) c);
    }

    private void init(JTextField c) {
        c.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                rShadowRender.updateUI();
                c.repaint();
            }
        });
        c.addPropertyChangeListener("editable", evt -> {
            if (c.isEditable()) {
                c.setForeground(RDefaultUI.FORGROUND);
                c.setBackground(Color.WHITE);
            } else {
                c.setForeground(Color.GRAY);
                c.setBackground(new Color(230, 230, 230));
            }
            c.repaint();
        });
        rShadowRender = new RShadowRender(c);
        c.setOpaque(false);
        c.setBackground(Color.WHITE);
        c.setForeground(RDefaultUI.FORGROUND);
        c.setSelectedTextColor(Color.WHITE);
        c.setSelectionColor(RDefaultUI.SELECTTION_BACKGROUND);
    }

    @Override
    protected void paintSafely(Graphics g) {
        rShadowRender.paint(g);
        super.paintSafely(g);
    }
}
