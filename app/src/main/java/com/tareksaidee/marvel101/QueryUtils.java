package com.tareksaidee.marvel101;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.tareksaidee.marvel101.NetworkUtils.getBitmapFromURL;

/**
 * Created by tarek on 12/28/2016.
 */

public class QueryUtils {

    private QueryUtils() {
    }


    public static ArrayList<Character> extractCharacters(String JSONResponse) {

        ArrayList<Character> characters = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            JSONObject mainObject = jsonObject.getJSONObject("data");
            JSONArray results = mainObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject curr = results.getJSONObject(i);
                int id = curr.getInt("id");
                String name = curr.getString("name");
                String descrp = curr.getString("description");
                JSONObject image = curr.getJSONObject("thumbnail");
                String imageUrl = image.getString("path") + "." + image.getString("extension");
                Bitmap imageBitmap = getBitmapFromURL(imageUrl);
                Character character = new Character(name, id, descrp, imageBitmap);
                characters.add(character);
            }
        } catch (JSONException e) {

            Log.e("character JSON", "Problem parsing the character JSON results", e);
        }

        // Return the list of earthquakes
        return characters;
    }

    public static ArrayList<Comic> extractComics(String JSONResponse) {
        ArrayList<Comic> comics = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            JSONObject mainObject = jsonObject.getJSONObject("data");
            JSONArray results = mainObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject curr = results.getJSONObject(i);
                int id = curr.getInt("digitalId");
                String title = curr.getString("title");
                String synop = curr.getString("description");
                String creators = getCreators(curr);
                //TODO fix date
                String pubDate = getDate(curr);
                JSONObject cover = curr.getJSONObject("thumbnail");
                String imageUrl = cover.getString("path") + "." + cover.getString("extension");
                Bitmap imageBitmap = getBitmapFromURL(imageUrl);
                Comic comic = new Comic(title, id, synop, imageBitmap, pubDate, creators);
                comics.add(comic);
            }
        } catch (JSONException e) {
            Log.e("Comic JSON", "Problem parsing the comic JSON results", e);
        }
        return comics;
    }

    public static ArrayList<Creator> extractCreators(String JSONResponse) {

        ArrayList<Creator> creators = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            JSONObject mainObject = jsonObject.getJSONObject("data");
            JSONArray results = mainObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject curr = results.getJSONObject(i);
                int id = curr.getInt("id");
                String name = curr.getString("fullName");
                JSONObject image = curr.getJSONObject("thumbnail");
                JSONArray events = curr.getJSONObject("events").getJSONArray("items");
                StringBuilder eventsString = new StringBuilder();
                for (int x = 0; x < events.length() && x < 5; x++) {
                    JSONObject event = events.getJSONObject(x);
                    eventsString.append(event.getString("name"));
                    eventsString.append("\n");
                }
                String imageUrl = image.getString("path") + "." + image.getString("extension");
                Bitmap imageBitmap = getBitmapFromURL(imageUrl);
                Creator creator = new Creator(name, id, imageBitmap, eventsString.toString());
                creators.add(creator);
            }
        } catch (JSONException e) {

            Log.e("character JSON", "Problem parsing the character JSON results", e);
        }

        // Return the list of earthquakes
        return creators;
    }

    public static ArrayList<Event> extractEvents(String JSONResponse) {
        ArrayList<Event> events = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            JSONObject mainObject = jsonObject.getJSONObject("data");
            JSONArray results = mainObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject curr = results.getJSONObject(i);
                int id = curr.getInt("id");
                String title = curr.getString("title");
                String descrp = curr.getString("description");
                JSONObject image = curr.getJSONObject("thumbnail");
                String startDate = curr.getString("start");
                startDate = eventDateFormat(startDate);
                String endDate = curr.getString("end");
                endDate = eventDateFormat(endDate);
                String nextEvent = curr.getJSONObject("next").getString("name");
                String prevEvent = curr.getJSONObject("previous").getString("name");
                String imageUrl = image.getString("path") + "." + image.getString("extension");
                Bitmap imageBitmap = getBitmapFromURL(imageUrl);
                Event event = new Event(title, id, descrp, imageBitmap, startDate, endDate, nextEvent, prevEvent);
                events.add(event);
            }
        } catch (JSONException e) {

            Log.e("character JSON", "Problem parsing the character JSON results", e);
        }
        return events;
    }

    public static String getMD5Hash(String timeStamp) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update((timeStamp + SECRET_KEYS.PRIVATE_KEY + SECRET_KEYS.PUBLIC_KEY).getBytes());
            byte[] byteData = messageDigest.digest();
            for (byte single : byteData) {
                sb.append(Integer.toString((single & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Characters", "MD5 hash didn't work");
        }
        return sb.toString();
    }

    private static String getCreators(JSONObject curr) throws JSONException {
        JSONArray creators = curr.getJSONObject("creators").getJSONArray("items");
        StringBuilder creatorsString = new StringBuilder();
        for (int x = 0; x < creators.length(); x++) {
            JSONObject creator = creators.getJSONObject(x);
            creatorsString.append(creator.getString("role"));
            creatorsString.append(": ");
            creatorsString.append(creator.getString("name"));
            creatorsString.append("\n");
        }
        return creatorsString.toString();
    }

    private static String getDate(JSONObject curr) throws JSONException {
        JSONArray dates = curr.getJSONArray("dates");
        if (dates.length() != 0 && dates.getJSONObject(0).getString("type").equals("onsaleDate")) {
            String weirdDate = dates.getJSONObject(0).getString("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = new Date();
            try {
                date = sdf.parse(weirdDate);
            } catch (ParseException e) {
                Log.e("comics date", "couldn't parse weird date");
            }
            sdf = new SimpleDateFormat("MM-dd-yyyy");
            return sdf.format(date);
        }
        return "";
    }

    private static String eventDateFormat(String weirdDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = sdf.parse(weirdDate);
        } catch (ParseException e) {
            Log.e("date", "couldn't parse weird date");
        }
        sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(date);
    }
}
