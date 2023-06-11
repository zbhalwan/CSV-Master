package edu.brown.cs32.examples.sprint3.JsonHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
/**
 * class that contains a method to parse a json and return it as a in hashmap form
 */
public class ParseJSON {
    private Map<String, Object> data;
    static final Type jsonType = Types.newParameterizedType(Map.class, String.class, Object.class);

    public ParseJSON(String json) {
        this.parse(json);
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    private void parse(String json) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(jsonType);
        try {
            this.data = adapter.fromJson(json);
        } catch (IOException e) {
            System.err.println("Unable to get value from JSON. Error: " + e.getMessage());
        }
    }
}
