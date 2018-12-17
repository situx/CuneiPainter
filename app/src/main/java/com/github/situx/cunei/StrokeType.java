package com.github.situx.cunei;

/**
 * Created by timo on 15.12.18 .
 */
public enum StrokeType {
    A("a"),B("b"),C("c"),D("d"),E("e"),F("f"),INV_A("!a"),INV_B("!b");

    String label;

    private StrokeType(String label){
        this.label=label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
