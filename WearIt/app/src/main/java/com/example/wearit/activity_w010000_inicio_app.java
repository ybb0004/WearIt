package com.example.wearit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class activity_w010000_inicio_app extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w010000_inicio_app);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inicoRegistro, new fragment_w01_0001_inicio_registro())
                    .commit();
        }
    }
}
