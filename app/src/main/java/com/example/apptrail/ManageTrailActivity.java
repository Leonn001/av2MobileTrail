package com.example.apptrail;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageTrailActivity extends AppCompatActivity {

    private ListView trailListView;
    private TrilhasDB trilhadb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trail);

        trailListView = findViewById(R.id.trailListView);
        trilhadb = new TrilhasDB(this);

        ArrayList<Trail> trails = trilhadb.getAllTrails();
        ArrayList<String> trailInfo = new ArrayList<>();
        for (Trail trail : trails) {
            String info = "Título: " + trail.getTitle() + "\n" +
                    "Distância: " + trail.getDistance() + " metros\n" +
                    "Tempo: " + trail.getTime() + " segundos\n" +
                    "Velocidade: " + trail.getSpeed() + " m/s";
            trailInfo.add(info);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, trailInfo);
        trailListView.setAdapter(adapter);
    }
}
