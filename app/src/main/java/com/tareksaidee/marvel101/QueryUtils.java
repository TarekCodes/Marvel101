package com.tareksaidee.marvel101;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static com.tareksaidee.marvel101.NetworkUtils.getBitmapFromURL;

/**
 * Created by tarek on 12/28/2016.
 */

public class QueryUtils {

    private QueryUtils(){}


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

    public static String getMD5Hash(String timeStamp){
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update((timeStamp + SECRET_KEYS.PRIVATE_KEY + SECRET_KEYS.PUBLIC_KEY).getBytes());
            byte [] byteData = messageDigest.digest();
            for (byte single:byteData) {
                sb.append(Integer.toString((single & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Characters", "MD5 hash didn't work");
        }
        return sb.toString();
    }
}
