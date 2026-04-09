package com.and.apartmentmanager.presentation.ui.admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.ActivityAdminMainBinding;
import com.and.apartmentmanager.presentation.ui.admin.chat.ChatListFragment;
import com.and.apartmentmanager.presentation.ui.admin.dashboard.DashboardFragment;
import com.and.apartmentmanager.presentation.ui.user.notification.NotificationListFragment;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new NotificationListFragment())
                    .commit();
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();

            if (id == R.id.dashboardFragment) {
                selected = new DashboardFragment();
//            } else if (id == R.id.residentFragment) {
//                selected = new ResidentListFragment();
//            } else if (id == R.id.serviceFragment) {
//                selected = new ServiceListFragment();
//            } else if (id == R.id.invoiceFragment) {
//                selected = new InvoiceListFragment();
            } else {
                return false;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selected)
                    .commit();

            return true;
        });
    }
}