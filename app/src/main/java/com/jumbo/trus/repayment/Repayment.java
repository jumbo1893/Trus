package com.jumbo.trus.repayment;

public class Repayment {

    private int amount;
    private long timestamp;
    private String note;

    public Repayment(int amount, long timestamp, String note) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.note = note;
    }

    public Repayment(int amount, long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Repayment() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
