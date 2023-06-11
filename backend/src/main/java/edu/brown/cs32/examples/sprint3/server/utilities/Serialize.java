package edu.brown.cs32.examples.sprint3.server.utilities;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class that serializes success and error events
 */
public class Serialize extends HashMap<String, Object> {

    private static final Moshi moshi = new Moshi.Builder().build();
    public static final JsonAdapter<Serialize> adapter = moshi.adapter(
            Types.newParameterizedType(Map.class, String.class, Object.class, List.class));

    /**
     * success method for successful serializing
     * @param map maps successful output
     * @return Serialized string
     */

    public static String success(Map<String, Object> map) {
        Serialize s = new Serialize();
        s.put("result", "success");
        s.putAll(map);
        return moshi.adapter(
                Types.newParameterizedType(Map.class, String.class, Object.class)).toJson(s);
    }

    /**
     *
     * error method for error checking
     * @param error being outputted
     * @param msg error description
     * @param map map to add to
     * @return Serialized string
     */
    public static String error(Object error, String msg, Map<String, Object> map) {
        Serialize s = new Serialize();
        s.put("result", error);
        s.put("message", msg);
        if (map != null) {
            s.putAll(map);
        }
        return moshi.adapter(
                Types.newParameterizedType(Map.class, String.class, Object.class)).toJson(s);
    }
    /**
     *
     * error method for error checking
     * @param error being outputted
     * @param msg error description
     * @return Serialized string
     */
    public static String error(Object error, String msg) {
        return error(error, msg, null);
    }

    public String toJson() {
        return adapter.toJson(this);
    }




}
