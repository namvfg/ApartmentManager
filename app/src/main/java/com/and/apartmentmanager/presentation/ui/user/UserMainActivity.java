package com.and.apartmentmanager.presentation.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;
import com.and.apartmentmanager.presentation.ui.user.invoice.UserInvoiceListFragment;
import com.and.apartmentmanager.presentation.ui.user.notification.NotificationListFragment;

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

        initBottomNav();

        if (savedInstanceState == null) {
            selectHome();
            showFragment(new HomeFragment(), false);
        }
    }

    private void initBottomNav() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            selectHome();
            showFragment(new HomeFragment(), false);
        });

        findViewById(R.id.nav_invoice).setOnClickListener(v -> {
            // TODO: InvoiceFragment
            selectInvoice();
            showFragment(new UserInvoiceListFragment(), false);
        });

        findViewById(R.id.nav_service).setOnClickListener(v -> {
            // TODO: ServiceFragment
            selectService();
            showFragment(new ServiceFragment(), false);
        });

        findViewById(R.id.nav_tb).setOnClickListener(v -> {
            // TODO: NotificationFragment
            selectNotification();
            showFragment(new NotificationListFragment(), false);
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            selectProfile();
            showFragment(new ProfileFragment(), false);
        });
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

    private void resetNav() {
        int defaultColor = getResources().getColor(R.color.unselected); // hoặc #A8D5B5

        setNavItem(R.id.icon_home, R.id.text_home, defaultColor, false);
        setNavItem(R.id.icon_invoice, R.id.text_invoice, defaultColor, false);
        setNavItem(R.id.icon_service, R.id.text_service, defaultColor, false);
        setNavItem(R.id.icon_tb, R.id.text_tb, defaultColor, false);
        setNavItem(R.id.icon_profile, R.id.text_profile, defaultColor, false);
    }

    private void setNavItem(int iconId, int textId, int color, boolean selected) {
        ImageView icon = findViewById(iconId);
        TextView text = findViewById(textId);

        icon.setColorFilter(color);
        text.setTextColor(color);

        text.setTypeface(null, selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    private void selectHome() {
        resetNav();
        int activeColor = getResources().getColor(R.color.primary);
        setNavItem(R.id.icon_home, R.id.text_home, activeColor, true);
    }

    private void selectProfile() {
        resetNav();
        int activeColor = getResources().getColor(R.color.primary);
        setNavItem(R.id.icon_profile, R.id.text_profile, activeColor, true);
    }

    private void selectInvoice() {
        resetNav();
        int activeColor = getResources().getColor(R.color.primary);
        setNavItem(R.id.icon_invoice, R.id.text_invoice, activeColor, true);
    }

    private void selectService() {
        resetNav();
        int activeColor = getResources().getColor(R.color.primary);
        setNavItem(R.id.icon_service, R.id.text_service, activeColor, true);
    }

    private void selectNotification() {
        resetNav();
        int activeColor = getResources().getColor(R.color.primary);
        setNavItem(R.id.icon_tb, R.id.text_tb, activeColor, true);
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Ghi đè Intent cũ bằng Intent mới chứa kết quả của MoMo
    }

}

