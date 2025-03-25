package com.yakovskij.yspy;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import java.util.Objects;

public class PlayersListActivity extends AppCompatActivity {

    private ListView listViewPlayers;
    private CustomAdapter adapter;
    private ArrayList<String> playerList;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        listViewPlayers = findViewById(R.id.list_view_players);
        Button buttonBack = findViewById(R.id.button_back);
        Button buttonAddPlayer = findViewById(R.id.button_add_player);
        db = new DBHelper(this.getApplicationContext());
        playerList = (ArrayList<String>) db.getAllPlayers();

        adapter = new CustomAdapter(this, playerList);
        listViewPlayers.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Здесь вы можете открыть диалоговое окно для ввода имени игрока
                showAddPlayerDialog();
            }
        });

        listViewPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog(position);
            }
        });
    }
    private void showEditDialog(final int position) {
        // Создаем AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать игрока");

        // Создаем EditText для ввода имени игрока
        final EditText input = new EditText(this);
        input.setText(playerList.get(position)); // Устанавливаем текущее имя игрока
        builder.setView(input);

        // Кнопка "Сохранить"
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                playerList.set(position, newName); // Обновляем имя игрока
                if (!Objects.equals(playerList.get(position), newName)){
                    db.deletePlayer(playerList.get(position));
                    db.addPlayer(newName);
                    playerList.clear();
                    playerList.addAll(db.getAllPlayers());
                    adapter.notifyDataSetChanged(); // Обновляем адаптер
                }
            }
        });

        // Кнопка "Удалить"
        builder.setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deletePlayer(playerList.get(position));
                playerList.clear();
                playerList.addAll(db.getAllPlayers());
                adapter.notifyDataSetChanged(); // Обновляем адаптер
            }
        });

        // Показываем диалог
        builder.show();
    }

    private void showAddPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить игрока");

        final EditText input = new EditText(this);
        input.setText("Игрок " + (playerList.size() + 1));
        builder.setView(input);

        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                if (!playerName.isEmpty()) {
                    db.addPlayer(playerName);
                    playerList.clear();
                    playerList.addAll(db.getAllPlayers());
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
