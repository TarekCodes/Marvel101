package com.tareksaidee.marvel101;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CharactersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);
    }




    public static ArrayList<Character> extractCharacters(String JSONResponse) {

        ArrayList<Character> characters = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject curr = results.getJSONObject(i);
                int id = curr.getInt("id");
                String name = curr.getString("name");
                String descrp = curr.getString("description");
                JSONObject image = curr.getJSONObject("thumbnail");
                String imageUrl = image.getString("path");
                String imageExt = image.getString("extension");
                Character character = new Character(name,id,descrp,imageUrl,imageExt);
                characters.add(character);
            }
        } catch (JSONException e) {

            Log.e("character JSON", "Problem parsing the character JSON results", e);
        }

        // Return the list of earthquakes
        return characters;
    }
}
