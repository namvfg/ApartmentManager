package com.and.apartmentmanager.presentation.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;
import com.and.apartmentmanager.presentation.ui.user.ChangePasswordFragment;

/**
 * Host fragment cho flow Admin (P1: Admin profile).
 */
public class AdminMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        SessionManager sm = SessionManager.getInstance(getApplicationContext());
        if (!sm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (savedInstanceState == null) {
            showFragment(new AdminProfileFragment(), false);
        }

        // Bottom nav: hiện tại chỉ để UI giống thiết kế, các tab khác để placeholder.
        View navHome = findViewById(R.id.nav_home);
        View navAdmin = findViewById(R.id.nav_admin);
        View navUser = findViewById(R.id.nav_user);
        View navProfile = findViewById(R.id.nav_profile);

        if (navHome != null) navHome.setOnClickListener(v -> showProfile());
        if (navAdmin != null) navAdmin.setOnClickListener(v -> showProfile());
        if (navUser != null) navUser.setOnClickListener(v -> showProfile());
        if (navProfile != null) navProfile.setOnClickListener(v -> showProfile());
    }

    public void showProfile() {
        showFragment(new AdminProfileFragment(), true);
    }

    public void showChangePassword() {
        showFragment(new ChangePasswordFragment(), true);
    }

    private void showFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) ft.addToBackStack(null);
        ft.commit();
    }
}

