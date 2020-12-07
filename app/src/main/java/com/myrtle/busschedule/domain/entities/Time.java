package com.myrtle.busschedule.domain.entities;


import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class Time implements Comparable<Time> {
    private final int hours;

    @Override
    public int compareTo(Time o) {
        int dHour = hours-o.hours;
        if(dHour == 0){
            return minutes-o.minutes;
        }
        return dHour;
    }

    private final int minutes;

    Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public static Time of(String time) {
        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);
        if (hours > 23 || hours < 0 || minutes > 59 || minutes < 0)
            throw new IllegalArgumentException();
        return new Time(hours,minutes);
    }

    public Time plusMinutes(int minutes){
        int dM = this.minutes+minutes;
        if(dM>=60){
            int dH = dM/60;
            dM = dM%60;
            return this.plusHours(dH).plusMinutes(dM);
        }
        return new Time(hours, dM);
    }

    public Time plusHours(int hours){
        int dH = this.hours + hours;
        if(dH >= 24){
            dH %= 24;
        }
        return new Time(dH,minutes);
    }

    @NotNull
    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(hours)+":"+format.format(minutes);
    }
}
