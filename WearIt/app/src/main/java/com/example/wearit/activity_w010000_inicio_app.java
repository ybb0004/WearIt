package com.example.wearit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class activity_w010000_inicio_app extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w010000_inicio_app);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.color_naranja_claro));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inicoRegistro, new fragment_w01_0001_inicio_registro())
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_w02_0001_home, new fragment_w02_0001_home())
                    .commit();
        }

    }
}
