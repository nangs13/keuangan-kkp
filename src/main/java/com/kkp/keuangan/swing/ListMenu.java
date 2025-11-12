package com.kkp.keuangan.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import com.kkp.keuangan.event.EventMenuSelected;
import com.kkp.keuangan.model.Model_Menu;
import com.kkp.keuangan.model.Enum.MenuKey;
import com.kkp.keuangan.model.Enum.MenuType;

public class ListMenu<E extends Object> extends JList<E> {

    private final DefaultListModel model;
    private int selectedIndex = -1;
    private int overIndex = -1;
    private EventMenuSelected event;

    public ListMenu() {
        model = new DefaultListModel();
        setModel(model);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    int index = locationToIndex(me.getPoint());
                    Object o = model.getElementAt(index);

                    if (o instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) o;
                        if (menu.getType() == MenuType.MENU) {
                            selectedIndex = index;
                            if (event != null) {
                                event.selected(menu.getKey());
                            }
                        }
                    } else {
                        selectedIndex = index;
                    }

                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent me) {
                overIndex = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                int index = locationToIndex(me.getPoint());

                if (index != overIndex) {
                    Object o = model.getElementAt(index);

                    if (o instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) o;

                        if (menu.getType() == MenuType.MENU) {
                            overIndex = index;
                        } else {
                            overIndex = -1;
                        }

                        repaint();
                    }
                }
            }
        });
    }

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
    }

    public void addItem(Model_Menu data) {
        model.addElement(data);
    }

    @Override
    public ListCellRenderer<? super E> getCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> jlist,
                    Object o,
                    int index,
                    boolean selected,
                    boolean focus) {
                Model_Menu data;

                if (o instanceof Model_Menu) {
                    data = (Model_Menu) o;
                } else {
                    data = new Model_Menu("", o + "", MenuType.EMPTY, MenuKey.EMPTY);
                }

                MenuItem item = new MenuItem(data);
                item.setSelected(selectedIndex == index);
                item.setOver(overIndex == index);

                return item;
            }
        };
    }
}
