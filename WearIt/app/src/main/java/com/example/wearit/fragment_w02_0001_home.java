package com.example.wearit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_w02_0001_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_w02_0001_home extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout (ajusta el nombre del layout según tu archivo)
        View view = inflater.inflate(R.layout.fragment_w02_0001_home, container, false);

        // Inicializamos las vistas del LinearLayout
        ImageView dayNightIcon = view.findViewById(R.id.dayNightIcon);
        TextView timeText = view.findViewById(R.id.timeText);
        ImageView weatherIcon = view.findViewById(R.id.weatherIcon);
        TextView temperatureText = view.findViewById(R.id.temperatureText);

        // Configurar Día/Noche y Hora
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(calendar.getTime());

        if (hour >= 6 && hour < 18) {
            // Día
            dayNightIcon.setImageResource(R.drawable.wb_sunny_24px);
        } else {
            // Noche
            dayNightIcon.setImageResource(R.drawable.dark_mode_24px);
        }
        timeText.setText(currentTime);

        // Configurar Clima y Temperatura (simulación)
        // Nota: Para datos reales, necesitarías una API de clima como OpenWeatherMap
        weatherIcon.setImageResource(R.drawable.cloud_24px); // Ejemplo estático
        temperatureText.setText("25°C"); // Ejemplo estático

        return view;
    }
}