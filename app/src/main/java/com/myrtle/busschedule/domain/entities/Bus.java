package com.myrtle.busschedule.domain.entities;

import org.jetbrains.annotations.NotNull;

/**
 * Класс, описывающий автобус.
 */
public class Bus implements Comparable<Bus> {
    private final Time departure;
    private final Direction direction;

    Bus(Direction dir, String departure) {
        direction = dir;
        this.departure = Time.of(departure);
    }

    public static Bus to(String departure) {
        if (departure == null)
            throw new NullPointerException();
        if (departure.trim().split(":").length != 2)
            throw new IllegalArgumentException("Acceptable format - hours:minutes");
        if (departure.trim().split(":")[0].length() < 2) {
            departure = "0" + departure.trim().split(":")[0] + ":" + departure.trim().split(":")[1];
        }
        return new Bus(Direction.TO, departure);
    }

    public static Bus from(String departure) {
        if (departure == null)
            throw new NullPointerException();
        if (departure.trim().split(":").length != 2)
            throw new IllegalArgumentException("Acceptable format - hours:minutes");
        if (departure.trim().split(":")[0].length() < 2) {
            departure = "0" + departure.trim().split(":")[0] + ":" + departure.trim().split(":")[1];
        }
        return new Bus(Direction.FROM, departure);
    }

    public Time getDeparture() {
        return departure;
    }

    public Direction getDirection() {
        return direction;
    }

    @NotNull
    @Override
    public String toString() {
        return "Bus{" +
                "departure='" + departure + '\'' +
                ", direction=" + direction +
                '}';
    }

    @Override
    public int compareTo(Bus o) {
        return departure.compareTo(o.departure);
    }
}
