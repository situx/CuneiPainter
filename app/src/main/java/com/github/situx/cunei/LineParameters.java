package com.github.situx.cunei;

/**
 * Created by timo on 14.12.18 .
 */
public class LineParameters {

    float startX;

    float startY;

    float endX;

    float endY;

    float deltaX;

    float deltaY;

    StrokeType type;

    @Override
    public String toString() {
        return "LineParameters{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", deltaX=" + deltaX +
                ", deltaY=" + deltaY +
                ", type=" + type +
                '}';
    }

    public LineParameters(float startX, float startY, float endX, float endY, float deltaX, float deltaY, StrokeType type) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.deltaX=deltaX;
        this.deltaY=deltaY;
        this.type=type;
    }
}
