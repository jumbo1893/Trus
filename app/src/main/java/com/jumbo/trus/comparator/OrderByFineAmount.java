package com.jumbo.trus.comparator;

import com.jumbo.trus.Model;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.Comparator;

public class OrderByFineAmount implements Comparator<Model> {

    private boolean desc;

    public OrderByFineAmount(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(Model o1, Model o2) {
        if (desc) {
            if (o1 instanceof Player && o2 instanceof Player) {
                return ((Player) o2).getAmountOfFinesInMatches() - ((Player) o1).getAmountOfFinesInMatches();
            } else if (o1 instanceof Match && o2 instanceof Match) {
                return ((Match) o2).getAmountOfFinesInMatch() - ((Match) o1).getAmountOfFinesInMatch() ;
            }
        }
        else {
            if (o1 instanceof Player && o2 instanceof Player) {
                return ((Player) o1).getAmountOfFinesInMatches() - ((Player) o2).getAmountOfFinesInMatches();
            } else if (o1 instanceof Match && o2 instanceof Match) {
                return ((Match) o1).getAmountOfFinesInMatch()  - ((Match) o2).getAmountOfFinesInMatch() ;
            }
        }
        return 0;
    }
}
