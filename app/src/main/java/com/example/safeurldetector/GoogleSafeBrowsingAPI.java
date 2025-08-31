package com.example.safeurldetector;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class GoogleSafeBrowsingAPI {
    private static final String TAG = "GoogleSafeBrowsingAPI";
    private static final String API_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=";
    private static final OkHttpClient client = new OkHttpClient();
    public static boolean isUrlSafe(String url, String apiKey) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                Log.e(TAG, "❌ API key is missing!");
                return false;
            }
            JSONObject requestJson = new JSONObject();
            JSONObject clientJson = new JSONObject();
            clientJson.put("clientId", "your-app-name");
            clientJson.put("clientVersion", "1.0");

            JSONObject threatInfoJson = new JSONObject();
            threatInfoJson.put("threatTypes", new JSONArray("[\"MALWARE\", \"SOCIAL_ENGINEERING\", \"UNWANTED_SOFTWARE\"]"));
            threatInfoJson.put("platformTypes", new JSONArray("[\"ANY_PLATFORM\"]"));
            threatInfoJson.put("threatEntryTypes", new JSONArray("[\"URL\"]"));

            JSONArray threatEntriesArray = new JSONArray();
            JSONObject threatEntry = new JSONObject();
            threatEntry.put("url", url);
            threatEntriesArray.put(threatEntry);
            threatInfoJson.put("threatEntries", threatEntriesArray);

            requestJson.put("client", clientJson);
            requestJson.put("threatInfo", threatInfoJson);

            RequestBody body = RequestBody.create(requestJson.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(API_URL + apiKey)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Log.d(TAG, "🔍 Google Safe Browsing Response: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                return !jsonResponse.has("matches"); // If matches exist, it's unsafe
            } else {
                Log.e(TAG, "❌ Failed to check URL. Response code: " + response.code());
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "❌ Exception in isUrlSafe(): " + e.getMessage(), e);
        }
        return false; // Assume unsafe if an error occurs
    }
}
