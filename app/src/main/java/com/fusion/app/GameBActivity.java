package com.fusion.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusion.app.databinding.ActivityGameBBinding;

public class GameBActivity extends AppCompatActivity {

    ActivityGameBBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGameBBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
