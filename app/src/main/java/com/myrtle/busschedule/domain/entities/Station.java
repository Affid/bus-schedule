package com.myrtle.busschedule.domain.entities;

public enum Station {
    ODINTSOVO("s9600721"), KUNTSEVSKAYA("s9601728"), FILI("s9600821"),
    BEGOVAYA("s9600821"), BELORUSSKAYA("s2000006");

    private final String id;

    Station(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
