package com.yakovskij.yspy;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean isFront = true;

    Animator front_animation;
    Animator back_animation;
    Animator back_triple_animation;
    private List<String> players;
    private String location;
    private String spy;
    float scale;
    DBHelper db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton buttonPlayerList = findViewById(R.id.button_player_list);
        buttonPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                                Intent intent = new Intent(MainActivity.this, PlayersListActivity.class);
                                startActivity(intent);
            }
        });
        ImageButton settingsbutton = findViewById(R.id.settings);
        settingsbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                                Intent intent = new Intent(MainActivity.this, LocationsActivity.class);
                                startActivity(intent);
            }
        });


        front_animation = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.front_animator);
        back_animation = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_animator);
        back_triple_animation = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_triple_animator);
        db = new DBHelper(this.getApplicationContext());
        scale = getResources().getDisplayMetrics().density;
        db.fetchAndSaveLocations();

        activeCard = generateNewGameCard();
        activeCard.setCameraDistance(8000 * scale);
        activeCard.setOnClickListener(flipper);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                0,
                0
        );
        layoutParams.setMargins(32, 32, 32, 32);
        layoutParams.dimensionRatio = "2:3";

        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

        ConstraintLayout parentLayout = findViewById(R.id.main);
        parentLayout.addView(activeCard, layoutParams);

        newGame();

    }
    ConstraintLayout generateNewGameCard(){
        ConstraintLayout newCard = (ConstraintLayout) getLayoutInflater().inflate(R.layout.card_yellow, null);


        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.old_front_card);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;

        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(
                new float[] {50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f}, // радиусы углов
                null, // нет внутреннего контура
                null // нет внутреннего контура
        ));

        shapeDrawable.getPaint().setColor(Color.TRANSPARENT);

        bitmapDrawable.setColorFilter(Color.argb(0.1f, 30, 70, 0), PorterDuff.Mode.SRC_ATOP); // Затемнение

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shapeDrawable, bitmapDrawable});
        newCard.setBackground(layerDrawable);

        TextView text = new TextView(this.getApplicationContext());
        text.setText("Нажмите для начала \n новой игры");
        text.setGravity(Gravity.CENTER);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTextColor(Color.WHITE);
        text.setTextSize(22);

        ConstraintLayout.LayoutParams textlayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textlayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.setMargins(32, 40, 32, 40);
        textlayoutParams.verticalBias = 0.85f;
        newCard.addView(text, textlayoutParams);

        return newCard;
    }
    void newGame(){
        this.players = new ArrayList<>();
        players.addAll(db.getAllPlayers());
        List<String> locations = db.getAllLocations();
        Collections.shuffle(locations);
        this.location = locations.get(0);

        Collections.shuffle(players);
        this.spy = players.get(0);
        Collections.sort(players);
    }
    View.OnClickListener flipper = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onCardClick();
        }
    };

    ConstraintLayout activeCard;
    boolean Front = true;
    int now_index = -1;
    ConstraintLayout generatePlayerCardFront(){
        ConstraintLayout newCard = (ConstraintLayout) getLayoutInflater().inflate(R.layout.card_blue, null);

        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.front_card);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;

        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(
                new float[] {50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f}, // радиусы углов
                null, // нет внутреннего контура
                null // нет внутреннего контура
        ));

        shapeDrawable.getPaint().setColor(Color.TRANSPARENT);

        bitmapDrawable.setColorFilter(Color.parseColor("#148C4646"), PorterDuff.Mode.SRC_ATOP); // Затемнение

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shapeDrawable, bitmapDrawable});
        newCard.setBackground(layerDrawable);
        TextView text = new TextView(this.getApplicationContext());
        TextView text_bottom = new TextView(this.getApplicationContext());

        if(players.get(now_index).equals(spy)){
            text.setText("Вы - шпион.");
            text_bottom.setText("Ваша цель - выяснить локацию");
        } else {
            text.setText("Вы - мирный. ");
            text_bottom.setText("Вы находитесь на локации: \n" + this.location);
        }

        text.setGravity(Gravity.CENTER);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTextColor(Color.WHITE);
        text.setTextSize(20);

        text_bottom.setGravity(Gravity.CENTER);
        text_bottom.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text_bottom.setTextColor(Color.WHITE);
        text_bottom.setTextSize(18);

        ConstraintLayout.LayoutParams textlayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textlayoutParams.verticalBias=0.2f;
        textlayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.setMargins(64, 64, 64, 64);

        ConstraintLayout.LayoutParams text_bottomlayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        text_bottomlayoutParams.verticalBias=0.85f;
        text_bottomlayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        text_bottomlayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        text_bottomlayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        text_bottomlayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        text_bottomlayoutParams.setMargins(72, 64, 72, 40);
        newCard.addView(text, textlayoutParams);
        newCard.addView(text_bottom, text_bottomlayoutParams);

        return newCard;
    }
    ConstraintLayout generatePlayerCardBack(){
        ConstraintLayout newCard = (ConstraintLayout) getLayoutInflater().inflate(R.layout.card_yellow, null);
        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.back_card);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;

        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(
                new float[] {50f, 50f, 50f, 50f, 50f, 50f, 50f, 50f}, // радиусы углов
                null, // нет внутреннего контура
                null // нет внутреннего контура
        ));

        shapeDrawable.getPaint().setColor(Color.TRANSPARENT);

        bitmapDrawable.setColorFilter(Color.parseColor("#428C4646"), PorterDuff.Mode.SRC_ATOP); // Затемнение

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shapeDrawable, bitmapDrawable});
        newCard.setBackground(layerDrawable);
        TextView text = new TextView(this.getApplicationContext());
        text.setText("Карточка игрока\n" + players.get(now_index));
        text.setGravity(Gravity.CENTER);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTextColor(Color.WHITE);
        text.setTextSize(24);

        ConstraintLayout.LayoutParams textlayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textlayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        textlayoutParams.setMargins(32, 64, 64, 40);
        textlayoutParams.verticalBias = 0.5f;
        newCard.addView(text, textlayoutParams);
        return newCard;
    }

    void onCardClick() {
        Animator animation = back_animation;
        ConstraintLayout newCard;
        if (now_index < players.size() - 1 || !Front){


            if(Front){
                now_index += 1;
                newCard = generatePlayerCardBack();
            } else {
                newCard = generatePlayerCardFront();
            }
            Front = !Front;

        } else {
            newCard = generateNewGameCard();
            newGame();
            Front = true;
            now_index = -1;
            animation = back_triple_animation;
        }


        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0,0);
        layoutParams.setMargins(32, 32, 32, 32); // Установка отступов
        layoutParams.dimensionRatio = "2:3";

        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

        newCard.setId(View.generateViewId()); // Генерируем новый ID для новой карточки

        // Добавляем newCard в родительский ConstraintLayout
        ConstraintLayout parentLayout = findViewById(R.id.main);
        parentLayout.addView(newCard, layoutParams);


        newCard.setVisibility(View.INVISIBLE);
        front_animation.setTarget(activeCard);
        animation.setTarget(newCard);
        front_animation.start();
        animation.start();
        activeCard.setClickable(false);

        front_animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                newCard.setVisibility(View.VISIBLE);
                activeCard.setVisibility(View.GONE);
                // Обновляем ссылку на активную карточку
                activeCard = newCard; // Теперь activeCard ссылается на новую карточку
                activeCard.setCameraDistance(8000 * scale);
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activeCard.setOnClickListener(flipper);
            }

        });
    }

}