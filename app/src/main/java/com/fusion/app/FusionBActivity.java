package com.fusion.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusion.app.databinding.ActivityFusionBBinding;

public class FusionBActivity extends AppCompatActivity {

    ActivityFusionBBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFusionBBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
