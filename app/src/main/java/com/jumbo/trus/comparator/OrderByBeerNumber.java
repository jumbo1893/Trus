package com.jumbo.trus.comparator;

import com.jumbo.trus.Model;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByBeerNumber implements Comparator<Model> {

    private boolean desc;

    public OrderByBeerNumber(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(Model o1, Model o2) {
        if (desc) {
            if (o1 instanceof Player && o2 instanceof Player) {
                return ((Player) o2).getNumberOfBeersInMatches() - ((Player) o1).getNumberOfBeersInMatches();
            } else if (o1 instanceof Match && o2 instanceof Match) {
                return ((Match) o2).returnNumberOfBeersInMatch() - ((Match) o1).returnNumberOfBeersInMatch();
            }
        }
        else {
            if (o1 instanceof Player && o2 instanceof Player) {
                return ((Player) o1).getNumberOfBeersInMatches() - ((Player) o2).getNumberOfBeersInMatches();
            } else if (o1 instanceof Match && o2 instanceof Match) {
                return ((Match) o1).returnNumberOfBeersInMatch() - ((Match) o2).returnNumberOfBeersInMatch();
            }
        }
        return 0;
    }
}
