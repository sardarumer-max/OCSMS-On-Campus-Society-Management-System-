package ocsms.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * UTILITY — SupabaseClient
 * Handles all HTTP REST communication with the Supabase PostgREST API.
 * Uses Gson for JSON serialization/deserialization.
 */
public class SupabaseClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Automatically maps camelCase Java fields to snake_case Supabase columns
    public static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DATETIME_FORMATTER.format(src));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), DATETIME_FORMATTER);
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DATE_FORMATTER.format(src));
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDate.parse(json.getAsString(), DATE_FORMATTER);
                }
            })
            .create();

    /** Fetch a list of objects from a specific table */
    public static <T> List<T> fetchTable(String table, Class<T[]> clazz) {
        if (!SupabaseConfig.isConfigured()) return new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SupabaseConfig.SUPABASE_URL + "/rest/v1/" + table + "?select=*"))
                .header("apikey", SupabaseConfig.SUPABASE_KEY)
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_KEY)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                T[] array = gson.fromJson(response.body(), clazz);
                return new ArrayList<>(List.of(array));
            } else {
                System.err.println("Supabase Fetch Error (" + table + "): " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /** Insert a new object into a table */
    public static boolean insert(String table, Object data) {
        if (!SupabaseConfig.isConfigured()) return false;

        String json = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SupabaseConfig.SUPABASE_URL + "/rest/v1/" + table))
                .header("apikey", SupabaseConfig.SUPABASE_KEY)
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            } else {
                System.err.println("Supabase Insert Error (" + table + "): " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Update an object in a table based on its ID */
    public static boolean update(String table, String id, Object data) {
        if (!SupabaseConfig.isConfigured()) return false;

        String json = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SupabaseConfig.SUPABASE_URL + "/rest/v1/" + table + "?id=eq." + id))
                .header("apikey", SupabaseConfig.SUPABASE_KEY)
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Delete an object from a table based on its ID */
    public static boolean delete(String table, String id) {
        if (!SupabaseConfig.isConfigured()) return false;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SupabaseConfig.SUPABASE_URL + "/rest/v1/" + table + "?id=eq." + id))
                .header("apikey", SupabaseConfig.SUPABASE_KEY)
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_KEY)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
