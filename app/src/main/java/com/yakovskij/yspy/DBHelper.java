package com.yakovskij.yspy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GameDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PLAYERS = "Players";
    private static final String TABLE_LOCATIONS = "Locations";

    private static final String BASE_URL = "http://188.68.223.156:8080";

    private OkHttpClient client = new OkHttpClient();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PLAYERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_LOCATIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");

        fetchAndSaveLocations();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public void addPlayer(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert(TABLE_PLAYERS, null, values);
        db.close();
    }

    public List<String> getAllPlayers() {
        List<String> players = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_PLAYERS, null);

        if (cursor.moveToFirst()) {
            do {
                players.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        if(players.isEmpty()) {
            this.addPlayer("Игрок 1");
            this.addPlayer("Игрок 2");
            this.addPlayer("Игрок 3");
            return this.getAllPlayers();
        }
        return players;
    }

    public void deletePlayer(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS, "name = ?", new String[]{name});
        db.close();
    }

    public void fetchAndSaveLocations() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/location/getAllLocations")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace(); // Обработка ошибок
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        saveLocationsToDatabase(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace(); // Обработка ошибок

                    }
                }
            }
        });
    }

    private void saveLocationsToDatabase(String jsonData) throws JSONException {
        // Преобразование jsonData в список локаций и сохранение в БД

         JSONArray jsonArray = new JSONArray(jsonData);
         SQLiteDatabase db = this.getWritableDatabase();

        // Удаление всех существующих записей из таблицы
        db.delete(TABLE_LOCATIONS, null, null);

         for (int i = 0; i < jsonArray.length(); i++) {
             String locationName = jsonArray.getString(i);
             ContentValues values = new ContentValues();
             values.put("name", locationName);
             db.insert(TABLE_LOCATIONS, null, values);
         }
         db.close();
    }

    public void addLocation(String name) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/location/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Обработка ошибок сети

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    // Если добавление на сервер прошло успешно, загружаем новые локации
                    fetchAndSaveLocations();
                } else {
                    // Обработка ошибки при добавлении локации

                }
            }
        });
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_LOCATIONS, null);

        if (cursor.moveToFirst()) {
            do {
                locations.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if(locations.isEmpty()) {
            return Arrays.asList(new String[]{"Loc1", "Loc2"});
        }
        return locations;
    }
}
