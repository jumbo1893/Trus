package com.jumbo.trus.layout;

import java.util.ArrayList;
import java.util.List;

public class PlayerLines {
    private List<Float> x1;
    private List<Float> x2;
    private List<Float> y1;
    private List<Float> y2;

    private List<Float> liquor_x1;
    private List<Float> liquor_x2;
    private List<Float> liquor_y1;
    private List<Float> liquor_y2;
    private boolean isLiquorImage;

    public PlayerLines() {
        x1 = new ArrayList<>();
        x2 = new ArrayList<>();
        y1 = new ArrayList<>();
        y2 = new ArrayList<>();
        liquor_x1 = new ArrayList<>();
        liquor_x2 = new ArrayList<>();
        liquor_y1 = new ArrayList<>();
        liquor_y2 = new ArrayList<>();
        isLiquorImage = false;
    }

    public void addAllBeerPositions(float x1, float x2, float y1, float y2) {
        this.x1.add(x1);
        this.x2.add(x2);
        this.y1.add(y1);
        this.y2.add(y2);
    }

    public void addAllLiquorPositions(float x1, float x2, float y1, float y2) {
        this.liquor_x1.add(x1);
        this.liquor_x2.add(x2);
        this.liquor_y1.add(y1);
        this.liquor_y2.add(y2);
    }

    public void removeLastBeerPosition() {
        if (x1.size() > 0) {
            x1.remove(x1.size() - 1);
            x2.remove(x2.size() - 1);
            y1.remove(y1.size() - 1);
            y2.remove(y2.size() - 1);
        }
    }

    public void removeLastLiquorPosition() {
        if (liquor_x1.size() > 0) {
            liquor_x1.remove(liquor_x1.size() - 1);
            liquor_x2.remove(liquor_x2.size() - 1);
            liquor_y1.remove(liquor_y1.size() - 1);
            liquor_y2.remove(liquor_y2.size() - 1);
        }
    }

    public List<Float> getX1() {
        return x1;
    }

    public void setX1(List<Float> x1) {
        this.x1 = x1;
    }

    public List<Float> getX2() {
        return x2;
    }

    public void setX2(List<Float> x2) {
        this.x2 = x2;
    }

    public List<Float> getY1() {
        return y1;
    }

    public void setY1(List<Float> y1) {
        this.y1 = y1;
    }

    public List<Float> getY2() {
        return y2;
    }

    public void setY2(List<Float> y2) {
        this.y2 = y2;
    }

    public List<Float> getLiquor_x1() {
        return liquor_x1;
    }

    public List<Float> getLiquor_x2() {
        return liquor_x2;
    }

    public List<Float> getLiquor_y1() {
        return liquor_y1;
    }

    public List<Float> getLiquor_y2() {
        return liquor_y2;
    }

    public boolean isLiquorImage() {
        return isLiquorImage;
    }

    public void setLiquorImage(boolean liquorImage) {
        isLiquorImage = liquorImage;
    }
}
