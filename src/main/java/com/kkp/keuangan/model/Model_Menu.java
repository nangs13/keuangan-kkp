package com.kkp.keuangan.model;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.kkp.keuangan.model.Enum.MenuKey;
import com.kkp.keuangan.model.Enum.MenuType;

public class Model_Menu {

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public void setType(MenuType type) {
        this.type = type;
    }
    
    public MenuKey getKey() {
        return key;
    }

    public void setKey(MenuKey key) {
        this.key = key;
    }

    public Model_Menu(String icon, String name, MenuType type, MenuKey key) {
        this.icon = icon;
        this.name = name;
        this.type = type;
        this.key = key;
    }

    public Model_Menu() {
    }

    private String icon;
    private String name;
    private MenuType type;
    private MenuKey key;

    public Icon toIcon() {
        return new ImageIcon(getClass().getResource("/com/kkp/keuangan/icon/" + icon + ".png"));
    }
}
