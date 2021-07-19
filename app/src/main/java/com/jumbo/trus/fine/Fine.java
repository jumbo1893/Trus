package com.jumbo.trus.fine;

import android.util.Log;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;

import java.util.Objects;

public class Fine extends Model {

    private static final String TAG = "Fine";

    private int amount;

    public Fine(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    public Fine(String name) {
        super(name);
    }

    public Fine() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceivedFine) && !(o instanceof Fine)) return false;
        if (o instanceof ReceivedFine) {
            ReceivedFine that = (ReceivedFine) o;
            return this.getId().equals(that.getFine().getId());
        }
        Fine fine = (Fine) o;
        return amount == fine.amount &&
                name.equals(fine.name);
    }

    @Override
    public String toString() {
        return "Fine{" +
                "id=" + id +
                "amount=" + amount +
                ", name='" + name + '\'' +
                '}';
    }
}
