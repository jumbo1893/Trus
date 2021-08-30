package com.jumbo.trus.repayment;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;

import java.util.Objects;

public class Repayment extends Model {

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

    public String getDateOfTimestampInStringFormat() {
        Date date = new Date();
        return date.convertMillisToTextDate(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repayment)) return false;
        if (!super.equals(o)) return false;
        Repayment repayment = (Repayment) o;
        return amount == repayment.amount &&
                timestamp == repayment.timestamp &&
                Objects.equals(note, repayment.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount, timestamp, note);
    }
}
