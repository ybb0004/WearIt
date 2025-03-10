package com.example.wearit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;

public class W01_0000_Activity_Inicio_App extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w010000_inicio_app);
        FirebaseApp.initializeApp(this); // Inicializa Firebase

    }
}