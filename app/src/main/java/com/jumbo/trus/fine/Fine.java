package com.jumbo.trus.fine;

import com.jumbo.trus.Model;
import com.jumbo.trus.match.Match;

import java.util.List;

public class Fine extends Model {

    private static final String TAG = "Fine";

    private int amount;
    private boolean forNonPlayers;

    public Fine(String name, int amount) {
        super(name);
        this.amount = amount;
    }

    public Fine(String name, int amount, boolean forNonPlayers) {
        super(name);
        this.amount = amount;
        this.forNonPlayers = forNonPlayers;
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

    public boolean isForNonPlayers() {
        return forNonPlayers;
    }

    public void setForNonPlayers(boolean forNonPlayers) {
        this.forNonPlayers = forNonPlayers;
    }

    public int returnNumberOfFineInMatches(List<Match> matchList) {
        int count = 0;
        for (Match match : matchList) {
            count += match.returnNumberOfReceviedFineInMatch(this);
        }
        return count;
    }

    public int returnAmountOfFineInMatches(List<Match> matchList) {
        int count = 0;
        for (Match match : matchList) {
            count += match.returnAmountOfReceviedFineInMatch(this);
        }
        return count;
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
                name.equals(fine.name) &&
                forNonPlayers == fine.forNonPlayers;
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
