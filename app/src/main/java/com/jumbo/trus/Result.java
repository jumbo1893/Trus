package com.jumbo.trus;

public class Result {

    private boolean isTrue;
    private String text;

    public Result(boolean isTrue, String text) {
        this.isTrue = isTrue;
        this.text = text;
    }

    public Result(boolean isTrue) {
        this.isTrue = isTrue;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
