package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByGoalMatchesRatio implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByGoalMatchesRatio(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            return Float.compare(o2.getGoalMatchesRatio(), o1.getGoalMatchesRatio());
        }
        else {
            return Float.compare(o1.getGoalMatchesRatio(), o2.getGoalMatchesRatio());
        }
    }
}
