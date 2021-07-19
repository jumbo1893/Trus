package com.jumbo.trus.season;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;

import java.util.Objects;

public class Season extends Model {

    private long seasonStart;
    private long seasonEnd;

    public Season(String name, long seasonStart, long seasonEnd) {
        super(name);
        this.seasonStart = seasonStart;
        this.seasonEnd = seasonEnd;
    }

    public Season(String name) {
        super(name);
    }

    public Season() {
    }


    public long getSeasonStart() {
        return seasonStart;
    }

    public void setSeasonStart(long seasonStart) {
        this.seasonStart = seasonStart;
    }

    public long getSeasonEnd() {
        return seasonEnd;
    }

    public void setSeasonEnd(long seasonEnd) {
        this.seasonEnd = seasonEnd;
    }

    public String getSeasonStartInStringFormat() {
        Date date = new Date();
        return date.convertMillisToTextDate(seasonStart);
    }

    public String getSeasonEndInStringFormat() {
        Date date = new Date();
        return date.convertMillisToTextDate(seasonEnd);
    }

    public boolean equalsForSeasonsFields(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season)) return false;
        Season season = (Season) o;
        return seasonStart == season.seasonStart &&
                seasonEnd == season.seasonEnd &&
                name.equals(season.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), seasonStart, seasonEnd);
    }

    public String toStringForRecycleView() {
        return name + ", " + getSeasonStartInStringFormat() + " - " + getSeasonEndInStringFormat();

    }

    @Override
    public String toString() {
        return "Season{" +
                "id=" + id +
                "seasonStart=" + seasonStart +
                ", seasonEnd=" + seasonEnd +
                ", name='" + name + '\'' +
                '}';
    }
}
