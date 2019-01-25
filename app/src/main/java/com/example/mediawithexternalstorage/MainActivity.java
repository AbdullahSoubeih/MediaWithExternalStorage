package com.example.mediawithexternalstorage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button ImageActivityBtn,AudioActivityBtn,VideoActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageActivityBtn = findViewById(R.id.ImageActivityBtn);
        AudioActivityBtn = findViewById(R.id.AudioActivityBtn);
        VideoActivityBtn = findViewById(R.id.VideoActivityBtn);


        ImageActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ImageActivity.class));
            }
        });

        AudioActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AudioActivity.class));
            }
        });

        VideoActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,VideoActivity.class));
            }
        });
    }
}
