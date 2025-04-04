package com.example.wearit;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class fragment_w02_0001_home extends Fragment {
    private Handler handler;
    private Runnable updateTimeRunnable;
    private TextView timeText;
    private ImageView dayNightIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0001_home, container, false);

        // Inicializar vistas
        dayNightIcon = view.findViewById(R.id.dayNightIcon);
        timeText = view.findViewById(R.id.timeText);
        ImageView weatherIcon = view.findViewById(R.id.weatherIcon);
        TextView temperatureText = view.findViewById(R.id.temperatureText);

        // Configuración inicial
        updateTimeAndDayNight();

        // Configurar Clima y Temperatura (simulación)
        weatherIcon.setImageResource(R.drawable.cloud_24px);
        temperatureText.setText("25°C");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdatingTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdatingTime();
    }

    private void startUpdatingTime() {
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndDayNight();
                handler.postDelayed(this, 30000); // Actualizar cada 30s
            }
        };
        handler.post(updateTimeRunnable);
    }

    private void stopUpdatingTime() {
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    private void updateTimeAndDayNight() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(calendar.getTime());
        /*
        if (hour >= 6 && hour < 18) {
            dayNightIcon.setImageResource(R.drawable.reloj);
        } else {
            dayNightIcon.setImageResource(R.drawable.reloj);
        }
        */
        dayNightIcon.setImageResource(R.drawable.reloj); // poner icono reloj

        timeText.setText(currentTime); //Actualizamos la hora cada min
    }
}