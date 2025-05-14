package com.example.wearit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activity_w020000_home_app extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w020000_home_app);

        // Configurar el BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtener el NavController
                NavController navController = Navigation.findNavController(activity_w020000_home_app.this, R.id.fragmentContainerView2);

                // Manejar la navegación según el ítem seleccionado
                int itemId = item.getItemId();
                if (itemId == R.id.item_1) {
                    navController.navigate(R.id.action_fragment_w02_0001_home_self);

                    return true;

                } else if (itemId == R.id.item_3) {
                    // Navegar al fragmento de la cámara
                    navController.navigate(R.id.action_fragment_w02_0001_home_to_fragment_w02_0006_garment_form);
                    return true;
                }  else if (itemId == R.id.item_5) {
                    // Navegar a otro fragmento (si es necesario)
                    navController.navigate(R.id.fragment_w02_0005_perfil);
                    return true;
                }
                return false;
            }
        });
    }
}