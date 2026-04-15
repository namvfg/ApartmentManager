package com.and.apartmentmanager.presentation.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.ActivityAdminMainBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.admin.apartment.ApartmentFragment;
import com.and.apartmentmanager.presentation.ui.admin.chat.ChatFragment;
import com.and.apartmentmanager.presentation.ui.admin.dashboard.DashboardFragment;
import com.and.apartmentmanager.presentation.ui.admin.invoice.AdminInvoiceListFragment;
import com.and.apartmentmanager.presentation.ui.admin.statistic.AdminStatisticsFragment;
import com.and.apartmentmanager.presentation.ui.user.ServiceFragment;
import com.and.apartmentmanager.presentation.ui.user.ChangePasswordFragment;

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
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();

            if (id == R.id.dashboardFragment) {
                selected = new DashboardFragment();
            } else if (id == R.id.apartmentFragment) {
                selected = new ApartmentFragment();
            } else if (id == R.id.residentFragment) {
                selected = new DeleteUserFragment();
            } else if (id == R.id.serviceFragment) {
                selected = new ServiceFragment();
            } else if (id == R.id.invoiceFragment) {
                selected = new AdminInvoiceListFragment();
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
    public void showChangePassword() {
        showFragment(new ChangePasswordFragment(), true);
    }

    public void showStatistic() {
        showFragment(new AdminStatisticsFragment(), true);
    }

    public void showProfile() {
        showFragment(new AdminProfileFragment(), true);
    }

    private void showFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("OPEN_CHAT", false)) {
            long userId = intent.getLongExtra("userId", -1);
            String userName = intent.getStringExtra("userName");
            String userRoom = intent.getStringExtra("userRoom");

            // SỬA Ở ĐÂY: Lấy apartmentId từ Intent
            long aptIdFromIntent = intent.getLongExtra("apartmentId", -1);

            // Đề phòng trường hợp lỗi, fallback về SessionManager
            long adminAptId = (aptIdFromIntent != -1) ? aptIdFromIntent : com.and.apartmentmanager.helper.SessionManager.getInstance(this).getApartmentId();

            // Truyền adminAptId vào ChatFragment
            ChatFragment chatFragment = ChatFragment.newInstance(adminAptId, userId, userName, userRoom);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}