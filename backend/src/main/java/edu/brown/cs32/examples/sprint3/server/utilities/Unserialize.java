package edu.brown.cs32.examples.sprint3.server.utilities;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Map;

public class Unserialize {


    public static Map<String,Object> unserialize(String currJson) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Map> adapter = moshi.adapter(Map.class);
        try {
            //un-json-ifying the json string and turning it into a map
            Map<String,Object> data = adapter.fromJson(currJson);
            return data;
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
    }



}
