package com.rushi.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.rushi.healthcare_app.databinding.ActivityDashboardBinding;
import androidx.activity.OnBackPressedCallback;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                0, 0);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Default item checked
        binding.navigationView.setCheckedItem(R.id.nav_dashboard);

        // Sidebar X Close Button setup
        View headerView = binding.navigationView.getHeaderView(0);
        ImageButton btnCloseSidebar = headerView.findViewById(R.id.btnCloseSidebar);
        btnCloseSidebar.setOnClickListener(v -> binding.drawerLayout.closeDrawer(GravityCompat.START));

        // Sign Out Button setup
        binding.btnSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }


}