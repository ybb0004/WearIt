package com.example.wearit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activity_w020000_home_app extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_w020000_home_app);

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            // Obtener el NavHostFragment primero
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainerView2);

            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();

                // Configurar NavigationUI (maneja automáticamente la navegación)
                NavigationUI.setupWithNavController(bottomNavigationView, navController);

                // Opcional: Si necesitas comportamiento personalizado
                bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                    try {
                        int itemId = item.getItemId();
                        if (itemId == R.id.item_1) {
                            navController.navigate(R.id.action_global_home);
                            return true;
                        } else if (itemId == R.id.item_3) {
                            navController.navigate(R.id.action_global_garment_form);
                            return true;
                        } else if (itemId == R.id.item_5) {
                            navController.navigate(R.id.action_global_perfil);
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                });
            }
        }
    }