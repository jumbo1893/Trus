package com.jumbo.trus.fine;

import com.jumbo.trus.Model;

import java.util.Objects;

public class ReceivedFine extends Model {

    private static final String TAG = "ReceivedFine";

    private Fine fine;
    private int count;

    public ReceivedFine() {
    }

    public ReceivedFine(Fine fine, int count) {
        super(fine.getName());
        this.fine = fine;
        this.count = count;
    }

    public Fine getFine() {
        return fine;
    }

    public void setFine(Fine fine) {
        this.fine = fine;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addFineCount() {
        count++;
    }

    public void addFineCount(int add) {
        count += add;
    }

    public void removeFineCount() {
        count--;
        if (count < 0) {
            count = 0;
        }
    }



    /**
     * @return vrací celkový částku jakou hráč zaplatil za všechny uložené pokuty tohoto druhu
     */
    public int returnAmountOfAllFines() {
        return count*fine.getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceivedFine) && !(o instanceof Fine)) return false;
        if (o instanceof ReceivedFine) {
            ReceivedFine that = (ReceivedFine) o;
            return fine.equals(that.fine);
        }
        else {
            Fine that = (Fine) o;
            return fine.getId().equals(that.getId());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(fine);
    }

    @Override
    public String toString() {
        return "ReceivedFine{" +
                "fine=" + fine +
                ", count=" + count +
                '}';
    }
}
