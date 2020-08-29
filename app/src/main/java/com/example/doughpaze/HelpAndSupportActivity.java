package com.example.doughpaze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HelpAndSupportActivity extends AppCompatActivity {

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(HelpAndSupportActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
    }
}