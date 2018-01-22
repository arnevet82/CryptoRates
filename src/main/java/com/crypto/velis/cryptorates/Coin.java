package com.crypto.velis.cryptorates;

/**
 * Created by natalie on 22/01/18.
 */

public class Coin {
    double newRate;
    double closingRate;
    double pctChange;
    String name;
    int arrow;
    int icon;
    double changePercentAlert;


    public Coin(String name, double newRate, double closingRate, int icon) {
        this.newRate = newRate;
        this.closingRate = closingRate;
        this.name = name;
        this.icon = icon;
    }
}
