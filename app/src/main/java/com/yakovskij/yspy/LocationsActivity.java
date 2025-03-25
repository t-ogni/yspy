package com.yakovskij.yspy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {

    private ListView listViewlocations;
    private CustomAdapter adapter;
    private DBHelper db;
    private ArrayList<String> locationList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        db = new DBHelper(this.getApplicationContext());

        listViewlocations = findViewById(R.id.list_locations);
        Button buttonBack = findViewById(R.id.button_back);
        Button buttonAddlocation = findViewById(R.id.button_add_location);

        locationList = (ArrayList<String>) db.getAllLocations();
        adapter = new CustomAdapter(this, locationList);
        listViewlocations.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonAddlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Здесь вы можете открыть диалоговое окно для ввода имени игрока
                showAddlocationDialog();
            }
        });

        listViewlocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDelDialog(position);
            }
        });
    }
    private void showDelDialog(final int position) {
        // Создаем AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтвердите");

        // Кнопка "Сохранить"
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged(); // Обновляем адаптер
            }
        });

        // Кнопка "Удалить"
        builder.setNegativeButton("Оставить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged(); // Обновляем адаптер
            }
        });

        // Показываем диалог
        builder.show();
    }

    private void showAddlocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить локацию");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String locationName = input.getText().toString();
                if (!locationName.isEmpty()) {
                    db.addLocation(locationName);
                    db.fetchAndSaveLocations();
                    locationList = (ArrayList<String>) db.getAllLocations();
                    adapter.notifyDataSetChanged();

                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
