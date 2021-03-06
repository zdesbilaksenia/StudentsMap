package com.example.studentmap;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParsePlace {

    private ResponseCallback callback;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public ParsePlace(ResponseCallback callback){
        this.callback = callback;
    }

    final String[] finalData = new String[1];

    public void Parse(String url, Location location) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                List<Place> listPlace = new ArrayList<>();
                String dataUrl = null;
                try {
                    dataUrl = downloadUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finalData[0] = dataUrl;

                JsonParser jsonParser = new JsonParser();
                List<HashMap<String, String>> mapList = null;
                JSONObject object = null;

                try {
                    object = new JSONObject(finalData[0]);
                    mapList = jsonParser.parseResult(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < mapList.size(); i++) {
                    HashMap<String, String> hashMapList = mapList.get(i);
                    Place temp = new Place();
                    temp.setLatitude(Double.parseDouble(hashMapList.get("lat")));
                    temp.setLongitude(Double.parseDouble(hashMapList.get("lng")));
                    temp.setName(hashMapList.get("name"));
                    temp.setIcon(hashMapList.get("icon"));
                    temp.setRating(Double.parseDouble(hashMapList.get("rating")));
                    temp.setVicinity(hashMapList.get("vicinity"));

                    if (hashMapList.get("photo") != null) {
                        temp.setPhoto(hashMapList.get("photo"));
                    }

                    String urlDistance = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                            "units=metric&origins=" + location.getLatitude() + "," + location.getLongitude() +
                            "&destinations=" + temp.getLatitude() + "," + temp.getLongitude() +
                            "&mode=walking&sensor=true&key=AIzaSyBomRHM2cJo2o33ZULSbZHbisJs4JZQSKE";
                    String distanceUrl = null;
                    try {
                        distanceUrl = downloadUrl(urlDistance);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    finalData[0] = distanceUrl;

                    JsonParser jsonParserDist = new JsonParser();
                    List<HashMap<String, String>> mapListDist = null;
                    JSONObject dist = null;
                    try {
                        dist = new JSONObject(finalData[0]);
                        mapListDist = jsonParserDist.parseResultDist(dist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    temp.setDistance(mapListDist.get(0).get("distance"));

                    listPlace.add(temp);

                }
                callback.response(listPlace);
            }
        });
        executor.shutdown();

    }

    public void ParseRoute(String url) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String routeUrl = null;

                try {
                    routeUrl = downloadUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finalData[0] = routeUrl;

                JsonParser jsonParser = new JsonParser();
                List<List<HashMap<String,String>>> route = null;
                JSONObject object = null;

                try {
                    object = new JSONObject(finalData[0]);
                    route = jsonParser.parse(object);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                callback.responseRoute(route);
            }
        });
        executor.shutdown();

    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader((new InputStreamReader(stream)));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }
}