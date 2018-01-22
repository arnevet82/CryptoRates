package com.crypto.velis.cryptorates;

/**
 * Created by natalie on 22/01/18.
 */

public class Model {

    String name, value, pct, flag;
    int arrow, icon;

    public Model(String top, String bottom, int arrow, int icon, String pct, String flag) {
        this.name = top;
        this.value = bottom;
        this.arrow = arrow;
        this.icon = icon;
        this.pct = pct;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getArrow() {return arrow;}

    public void setArrow(int arrow) {this.arrow = arrow;}

    public int getIcon() {return icon;}

    public void setIcon(int icon) {this.icon = icon;}

    public String getPct() {return pct;}

    public void setPct(String pct) {this.pct = pct;}

    public String getFlag() {return flag;}

    public void setFlag(String flag) {this.flag = flag;}
}