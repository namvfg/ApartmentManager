package com.and.apartmentmanager.presentation.ui.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;

/**
 * Host fragment cho flow User (P1: Profile/Settings).
 */
public class UserMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        SessionManager sm = SessionManager.getInstance(getApplicationContext());
        if (!sm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (savedInstanceState == null) {
            showFragment(new ProfileFragment(), false);
        }
    }

    public void showSettings() {
        showFragment(new SettingsFragment(), true);
    }

    public void showProfile() {
        showFragment(new ProfileFragment(), true);
    }

    public void showProfileDetail() {
        showFragment(new ProfileDetailFragment(), true);
    }

    public void showChangePassword() {
        showFragment(new ChangePasswordFragment(), true);
    }

    private void showFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}

