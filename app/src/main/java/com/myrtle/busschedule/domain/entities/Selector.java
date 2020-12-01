package com.myrtle.busschedule.domain.entities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Класс, осуществляющий сопоставление автобусов и электричек.
 */
public class Selector {
    private final Direction direction;
    private final ArrayList<Bus> buses;
    private final ArrayList<Train> trains;
    private final Station station;

    public Direction getDirection() {
        return direction;
    }

    public ArrayList<Bus> getBuses() {
        return new ArrayList<>(buses);
    }

    public ArrayList<Train> getTrains() {
        return new ArrayList<>(trains);
    }

    private Selector(Direction direction, ArrayList<Bus> buses, ArrayList<Train> trains, Station station) {
        this.direction = direction;
        this.buses = buses;
        this.trains = trains;
        this.station = station;
    }

    public static Selector to(Station departureStation, GregorianCalendar date) throws IOException, JSONException {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        if (date.before(cal))
            throw new IllegalArgumentException("Выбор электричек ранее, чем вчера, недоступен");
        ArrayList<Bus> buses1 = checkBuses(Direction.TO, date.get(Calendar.DAY_OF_WEEK));
        ArrayList<Train> trains1 = checkTrains((date.get(Calendar.YEAR) + "-"
                        + (date.get(Calendar.MONTH) + 1) + "-"
                        + date.get(Calendar.DAY_OF_MONTH)),
                departureStation, Station.ODINTSOVO, 0);

        Collections.sort(buses1);
        Collections.sort(trains1, (o1, o2) -> o1.getArrival().compareTo(o2.getArrival()));

        return new Selector(Direction.TO, buses1, trains1,departureStation);
    }

    public static Selector from(Station arrivalStation, GregorianCalendar date) throws IOException, JSONException {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        if (date.before(cal))
            throw new IllegalArgumentException("Выбор электричек ранее, чем вчера, недоступен");
        ArrayList<Bus> buses1 = checkBuses(Direction.FROM, date.get(Calendar.DAY_OF_WEEK));
        ArrayList<Train> trains1 = checkTrains((date.get(Calendar.YEAR) + "-"
                        + (date.get(Calendar.MONTH) + 1) + "-"
                        + date.get(Calendar.DAY_OF_MONTH)),
                Station.ODINTSOVO, arrivalStation, 0);
        Collections.sort(buses1);
        Collections.sort(trains1, (o1, o2) -> o1.getArrival().compareTo(o2.getArrival()));
        return new Selector(Direction.FROM, buses1, trains1, arrivalStation);
    }

    public static Selector toDemo(Station departureStation, GregorianCalendar date) {
        ArrayList<Bus> buses1 = new ArrayList<>();
        ArrayList<Train> trains1 = new ArrayList<>();
        for (int i = 7; i < 22; i++) {
            buses1.add(Bus.to(i + ":15"));
            buses1.add(Bus.to(i + ":55"));
            trains1.add(Train.to(i + ":45", i + 1 + ":05", departureStation));
            trains1.add(Train.to(i + ":15", i + ":35", departureStation));
        }
        Collections.sort(buses1);
        Collections.sort(trains1, (o1, o2) -> o1.getArrival().compareTo(o2.getArrival()));

        return new Selector(Direction.TO, buses1, trains1, departureStation);
    }

    private static ArrayList<Bus> checkBuses(Direction dir, int dayOfWeek) throws IOException {
        if (dayOfWeek > 7 || dayOfWeek < 1)
            throw new IllegalArgumentException("Illegal day:" + dayOfWeek + "Legal values of day: 1 to 7");
        ArrayList<Bus> busArrayList = new ArrayList<>();
        try {
            JSONArray buses;
            switch (dayOfWeek) {
                case 7:
                    buses = getBus(dir, "sunday");
                    break;
                case 6:
                    buses = getBus(dir, "saturday");
                    break;
                default:
                    buses = getBus(dir, "weekday");
                    break;
            }
            for (int i = 0; i < buses.length(); i++) {
                busArrayList.add(new Bus(dir, buses.getString(i)));
            }
        } catch (JSONException | IOException e) {
            throw new IOException(e.getCause());
        }

        return busArrayList;
    }

    private static JSONArray getBus(Direction direction, String day) throws JSONException, IOException {
        URL url = new URL("https://busschedule-c253d.firebaseio.com/" +
                day +
                (direction != null ? "/" + direction.name().toLowerCase() : "") + ".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setDoInput(true);
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = reader.readLine();

        con.disconnect();
        reader.close();

        return new JSONArray(line);
    }

    private static ArrayList<Train> checkTrains(String date, Station stationFrom, Station stationTo, int offset) throws IOException, JSONException {
        URL url = new URL(String.format(Locale.ENGLISH, "https://api.rasp.yandex.net/v3.0/search/?" +
                        "apikey=9f256970-436a-4fae-8cd6-31080528d277&format=json" +
                        "&from=%s&to=%s&lang=ru_RU&offset=%d&date=%s",
                stationFrom.getId(), stationTo.getId(), offset, date));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setDoInput(true);
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = reader.readLine();

        con.disconnect();
        reader.close();

        JSONObject response = new JSONObject(line);
        JSONArray segments = response.getJSONArray("segments");
        ArrayList<Train> trains = new ArrayList<>();
        Direction dir = stationFrom.equals(Station.ODINTSOVO) ? Direction.FROM : Direction.TO;
        for (int i = 0; i < segments.length(); i++) {
            String arrival = segments.getJSONObject(i).getString("arrival").split("T")[1].substring(0, 5);
            String departure = segments.getJSONObject(i).getString("departure").split("T")[1].substring(0, 5);
            if (dir.equals(Direction.FROM)) {
                trains.add(Train.from(departure, arrival, stationTo));
            } else {
                trains.add(Train.to(departure, arrival, stationFrom));
            }
        }
        segments = null;
        int total = response.getJSONObject("pagination").getInt("total");
        int limit = response.getJSONObject("pagination").getInt("limit");
        if (offset + limit < total)
            trains.addAll(checkTrains(date, stationFrom, stationTo, limit + offset));
        return trains;
    }

    public Train getTrain(Bus bus) {
        int i;
        Time time = bus.getDeparture();
        if (direction.equals(Direction.TO)) {
            i = Collections.binarySearch(trains, Train.to("00:00", time.toString(), station),
                    (o1, o2) -> o1.getArrival().compareTo(o2.getArrival()));
        } else {
            Collections.sort(trains,(o1, o2) -> o1.getDeparture().compareTo(o2.getDeparture()));
            Time arrive = time.plusMinutes(20);
            i = Collections.binarySearch(trains, Train.from((arrive.toString()), "00:00"
                    , station),
                    (o1, o2) -> o1.getDeparture().compareTo(o2.getDeparture()));
        }
        int s;
        if (i < 0) {
            s = (-i - 1);
            if (-i >= trains.size()) {
                s = trains.size() - 1;
            }
            Train train;
            if (direction.equals(Direction.TO)) {
                for (int j = 0; j < trains.size() && s - j >= 0; j++) {
                    train = trains.get(s - j);
                    if (train.getArrival().compareTo(bus.getDeparture()) <= 0) {
                        return train;
                    }
                }
            }
            return trains.get(s);
        } else
            return trains.get(i);
    }

    public Bus getBus(Train train) {
        return null;
    }
}