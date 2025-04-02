package com.example.wearit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class fragment_w02_0006_garment_form extends Fragment {
    private ShapeableImageView garmentImage;
    private Spinner sizeSpinner;
    private AutoCompleteTextView brandInput;
    private TextInputLayout brandInputLayout;
    private Spinner seasonSpinner;
    private AutoCompleteTextView colorInput;
    private TextInputLayout colorInputLayout;
    private View colorPreview;
    private Spinner partSpinner;
    private Spinner styleSpinner;
    private MaterialButton backButton;
    private MaterialButton saveButton;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private GarmentManager garmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_w02_0006_garment_form, container, false);

        garmentImage = view.findViewById(R.id.garmentImage);
        sizeSpinner = view.findViewById(R.id.sizeSpinner);
        brandInput = view.findViewById(R.id.brandInput);
        brandInputLayout = view.findViewById(R.id.brandInputLayout);
        seasonSpinner = view.findViewById(R.id.seasonSpinner);
        colorInput = view.findViewById(R.id.colorInput);
        colorInputLayout = view.findViewById(R.id.colorInputLayout);
        colorPreview = view.findViewById(R.id.colorPreview);
        partSpinner = view.findViewById(R.id.partSpinner);
        styleSpinner = view.findViewById(R.id.styleSpinner);
        backButton = view.findViewById(R.id.backButton);
        saveButton = view.findViewById(R.id.saveButton);

        garmentManager = new GarmentManager();

        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        garmentImage.startAnimation(fadeInAnimation);
        sizeSpinner.startAnimation(fadeInAnimation);
        brandInput.startAnimation(fadeInAnimation);
        seasonSpinner.startAnimation(fadeInAnimation);
        colorInput.startAnimation(fadeInAnimation);
        partSpinner.startAnimation(fadeInAnimation);
        styleSpinner.startAnimation(fadeInAnimation);
        saveButton.startAnimation(fadeInAnimation);

        sizeSpinner.setSelection(2);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                garmentImage.setImageURI(imageUri);
                Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
                garmentImage.startAnimation(scaleAnimation);
            }
        });

        garmentImage.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        });

        /*colorInputLayout.setEndIconOnClickListener(v -> {
            new ColorPickerDialog.Builder(getContext())
                    .setColorPickerColors(getResources().getIntArray(R.array.color_picker_colors))
                    .setColorListener((color, colorHex) -> {
                        colorInput.setText(colorHex);
                        updateColorPreview(colorHex);
                    })
                    .show();
        });

        colorInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          /*  @Override
         /*   public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateColorPreview(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        backButton.setOnClickListener(v -> {
            v.startAnimation(fadeInAnimation);
            getActivity().getSupportFragmentManager().popBackStack();
        });*/

        saveButton.setOnClickListener(v -> {
            v.startAnimation(fadeInAnimation);

            String size = sizeSpinner.getSelectedItem().toString();
            String brand = brandInput.getText().toString().trim();
            String season = seasonSpinner.getSelectedItem().toString();
            String color = colorInput.getText().toString().trim();
            String part = partSpinner.getSelectedItem().toString();
            String style = styleSpinner.getSelectedItem().toString();

            boolean isValid = true;

            if (size.equals("-")) {
                ((TextInputLayout) sizeSpinner.getParent().getParent()).setError("Selecciona una talla");
                isValid = false;
            } else {
                ((TextInputLayout) sizeSpinner.getParent().getParent()).setError(null);
            }

            if (brand.isEmpty()) {
                brandInputLayout.setError("Ingresa una marca");
                isValid = false;
            } else {
                brandInputLayout.setError(null);
            }

            if (season.equals("-")) {
                ((TextInputLayout) seasonSpinner.getParent().getParent()).setError("Selecciona una temporada");
                isValid = false;
            } else {
                ((TextInputLayout) seasonSpinner.getParent().getParent()).setError(null);
            }

            if (color.isEmpty()) {
                colorInputLayout.setError("Ingresa un color");
                isValid = false;
            } else {
                colorInputLayout.setError(null);
            }

            if (part.equals("-")) {
                ((TextInputLayout) partSpinner.getParent().getParent()).setError("Selecciona una parte");
                isValid = false;
            } else {
                ((TextInputLayout) partSpinner.getParent().getParent()).setError(null);
            }

            if (style.equals("-")) {
                ((TextInputLayout) styleSpinner.getParent().getParent()).setError("Selecciona un estilo");
                isValid = false;
            } else {
                ((TextInputLayout) styleSpinner.getParent().getParent()).setError(null);
            }

            if (imageUri == null) {
                Snackbar.make(view, "Por favor, toma una foto de la prenda", Snackbar.LENGTH_SHORT).show();
                isValid = false;
            }

            if (!isValid) {
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Snackbar.make(view, "Debes iniciar sesión para guardar la prenda", Snackbar.LENGTH_SHORT).show();
                return;
            }

            String userId = currentUser.getUid();
            String username = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Usuario";
            String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";

            garmentManager.saveGarment(size, brand, season, color, part, style, imageUri.toString(), userId, username, email,
                    new GarmentManager.SaveGarmentCallback() {
                        @Override
                        public void onSuccess(String garmentId) {
                            Snackbar.make(view, "Prenda guardada correctamente", Snackbar.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Snackbar.make(view, "Error: " + errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                    });


        });

       /* private void updateColorPreview(String colorText) {
            try {
                int color = Color.parseColor(colorText);
                GradientDrawable drawable = (GradientDrawable) colorPreview.getBackground();
                drawable.setColor(color);
            } catch (IllegalArgumentException e) {
                GradientDrawable drawable = (GradientDrawable) colorPreview.getBackground();
                drawable.setColor(Color.GRAY);
            }*/
        return view;
    }
    }
