package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByReceivedGoalsRatio implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByReceivedGoalsRatio(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            return Float.compare(o1.getReceivedGoalsGoalkeepingMinutesRatio(), o2.getReceivedGoalsGoalkeepingMinutesRatio());
        }
        else {
            return Float.compare(o2.getReceivedGoalsGoalkeepingMinutesRatio(), o1.getReceivedGoalsGoalkeepingMinutesRatio());
        }
    }
}
