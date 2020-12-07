package com.myrtle.busschedule.domain.entities;

import org.jetbrains.annotations.NotNull;

/**
 * Класс, описывающий электричку.
 */
public final class Train {
    private final Time departure;
    private final Time arrival;
    private final Direction direction;
    private final Station station;

    private Train(String departure, String arrival, Direction direction, Station station) {
        this.departure = Time.of(departure);
        this.arrival = Time.of(arrival);
        this.direction = direction;
        this.station = station;
    }

    public static Train to(String departure, String arrival, Station station) {
        if (departure == null || arrival == null)
            throw new NullPointerException();
        if (departure.trim().split(":").length != 2
                || arrival.trim().split(":").length != 2)
            throw new IllegalArgumentException("Acceptable format - hours:minutes");
        if (departure.trim().split(":")[0].length() < 2) {
            departure = "0" + departure.trim().split(":")[0] + ":" + departure.trim().split(":")[1];
        }
        if (arrival.trim().split(":")[0].length() < 2) {
            arrival = "0" + arrival.trim().split(":")[0] + ":" + arrival.trim().split(":")[1];
        }
        return new Train(departure, arrival, Direction.TO, station);
    }

    public static Train from(String departure, String arrival, Station station) {
        if (departure == null || arrival == null)
            throw new NullPointerException();
        if (departure.trim().split(":").length != 2
                || arrival.trim().split(":").length != 2)
            throw new IllegalArgumentException("Acceptable format - hours:minutes");
        if (departure.trim().split(":")[0].length() < 2) {
            departure = "0" + departure.trim().split(":")[0] + ":" + departure.trim().split(":")[1];
        }
        if (arrival.trim().split(":")[0].length() < 2) {
            arrival = "0" + arrival.trim().split(":")[0] + ":" + arrival.trim().split(":")[1];
        }
        return new Train(departure, arrival, Direction.FROM, station);
    }

    public Time getArrival() {
        return arrival;
    }

    public Direction getDirection() {
        return direction;
    }

    public Station getArrivalStation() {
        if (direction.equals(Direction.FROM))
            return station;
        else
            return Station.ODINTSOVO;
    }

    public Time getDeparture() {
        return departure;
    }

    @NotNull
    @Override
    public String toString() {
        return "Train{" +
                "departure station='" + getDepartureStation() + '\''
                +", departure='" + departure.toString() + '\''
                +", arrival station='" + getArrivalStation() + '\''
                +", arrival='" + arrival.toString() + '\''
                +'}';
    }

    public Station getDepartureStation() {
        if (direction.equals(Direction.TO))
            return station;
        else
            return Station.ODINTSOVO;
    }
}
