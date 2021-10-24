package com.jumbo.trus.comparator;

import com.jumbo.trus.Model;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByBeerAndLiquorNumber implements Comparator<Model> {

    private boolean desc;

    public OrderByBeerAndLiquorNumber(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(Model o1, Model o2) {
        if (desc) {
            if (o1 instanceof Player && o2 instanceof Player) {
                return (((Player) o2).getNumberOfBeersInMatches()+((Player) o2).getNumberOfLiquorsInMatches()) -
                        (((Player) o1).getNumberOfBeersInMatches()+((Player) o1).getNumberOfLiquorsInMatches());
            } else if (o1 instanceof Match && o2 instanceof Match) {
                return (((Match) o2).returnNumberOfBeersInMatch()+((Match) o2).returnNumberOfLiquorsInMatch()) -
                        (((Match) o1).returnNumberOfBeersInMatch()+((Match) o1).returnNumberOfLiquorsInMatch());
            }
        }
        else {
            if (o1 instanceof Player && o2 instanceof Player) {
                return (((Player) o1).getNumberOfBeersInMatches()+((Player) o1).getNumberOfLiquorsInMatches()) -
                        (((Player) o2).getNumberOfBeersInMatches()+((Player) o2).getNumberOfLiquorsInMatches());
            } else if (o1 instanceof Match && o2 instanceof Match) {
                return (((Match) o1).returnNumberOfBeersInMatch()+((Match) o1).returnNumberOfLiquorsInMatch()) -
                        (((Match) o2).returnNumberOfBeersInMatch()+((Match) o2).returnNumberOfLiquorsInMatch());
            }
        }
        return 0;
    }
}
