package com.and.apartmentmanager.presentation.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.dto.ServiceDTO;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.ApartmentRepository;
import com.and.apartmentmanager.data.repository.ContractRepository;
import com.and.apartmentmanager.data.repository.InvoiceRepository;
import com.and.apartmentmanager.data.repository.ServiceRepository;
import com.and.apartmentmanager.data.repository.UnitRepository;
import com.and.apartmentmanager.data.repository.UserApartmentRepository;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.databinding.FragmentWorkspaceBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.ServiceRowAdapter;
import com.and.apartmentmanager.presentation.ui.user.statistic.ContractDetailFragment;
import com.and.apartmentmanager.presentation.ui.user.statistic.UserStatisticsFragment;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceFragment extends Fragment {

    private FragmentWorkspaceBinding binding;

    private ApartmentRepository apartmentRepository;
    private UnitRepository unitRepository;
    private UserApartmentRepository userApartmentRepository;
    private InvoiceRepository invoiceRepository;
    private ContractRepository contractRepository;
    private ServiceRepository serviceRepository;

    private ServiceRowAdapter serviceAdapter;

    private static final String ARG_APARTMENT_ID = "apartment_id";
    private static final String ARG_UNIT_ID = "unit_id";
    private int currentInvoiceId = -1;
    private String currentInvoiceStatus = "";
    private int adminId = -1;
    private String adminName = "Quản trị viên";

    public static WorkspaceFragment newInstance(long apartmentId, long unitId) {
        WorkspaceFragment f = new WorkspaceFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_APARTMENT_ID, apartmentId);
        args.putLong(ARG_UNIT_ID, unitId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkspaceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apartmentRepository = new ApartmentRepository(requireActivity().getApplication());
        unitRepository = new UnitRepository(requireActivity().getApplication());
        userApartmentRepository = new UserApartmentRepository(requireActivity().getApplication());
        invoiceRepository = new InvoiceRepository(requireActivity().getApplication());
        contractRepository = new ContractRepository(requireActivity().getApplication());
        serviceRepository = new ServiceRepository(requireActivity().getApplication());

        long apartmentId = getArguments() != null ? getArguments().getLong(ARG_APARTMENT_ID) : -1;
        long unitId = getArguments() != null ? getArguments().getLong(ARG_UNIT_ID) : -1;

        serviceAdapter = new ServiceRowAdapter();
        binding.recyclerServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerServices.setAdapter(serviceAdapter);


        new Thread(() -> {

            // 1. Lấy thông tin Apartment & Admin ID ngay từ đầu
            var apartment = apartmentRepository.getByIdSync((int) apartmentId);
            String apartmentName = "Unknown";

            if (apartment != null) {
                apartmentName = apartment.getName();
                this.adminId = apartment.getAdminId(); // Cập nhật adminId toàn cục

                // 2. Lấy thông tin Admin từ Database
                UserEntity adminUser = new UserRepository(requireActivity().getApplication()).getByIdSync(adminId);
                if (adminUser != null) {
                    this.adminName = adminUser.getName(); // Cập nhật adminName thực tế
                }
            }

            // ===== SERVICES (ĐƯA LÊN TRƯỚC) =====
            List<ServiceRowAdapter.ServiceItem> serviceItems = new ArrayList<>();

            try {
                List<ServiceDTO> services = serviceRepository.getServicesByUnitIdBlocking((int) unitId);

                if (services != null) {
                    for (ServiceDTO s : services) {

                        String detail = "Không rõ";

                        if ("fixed".equals(s.getPricingType())) {
                            detail = "Cố định";
                        } else if ("variable".equals(s.getPricingType())) {
                            detail = "Theo sử dụng";
                        }

                        if (s.getDescription() != null && !s.getDescription().isEmpty()) {
                            detail += " • " + s.getDescription();
                        }

                        int icon = getServiceIcon(s.getName());

                        serviceItems.add(new ServiceRowAdapter.ServiceItem(
                                s.getName(),
                                detail,
                                icon
                        ));
                    }
                }

            } catch (Exception e) {
                Log.e("ServiceError", "Load services failed", e);
            }

            // ===== Unit =====
            String unitName = unitRepository.getFullUnitName((int) unitId);

            // ===== Invoice =====
            InvoiceEntity invoice = invoiceRepository.getLatestByUnitIdBlocking(unitId);
            int invoiceId = -1;
            String invoiceStatus = "";

            String amount = "0đ";
            String month = "--";
            String due = "--";

            String statusText = "Chờ thanh toán";
            int statusBg = R.drawable.bg_chip_yellow;
            int statusColor = requireContext().getColor(R.color.warning);

            if (invoice != null) {

                invoiceId = invoice.getId();
                invoiceStatus = invoice.getStatus();
                amount = ((int) invoice.getTotalAmount()) + "đ";

                if (invoice.getMonth() != null && invoice.getMonth().contains("-")) {
                    String[] parts = invoice.getMonth().split("-");
                    if (parts.length == 2) {
                        month = "Tháng " + parts[1] + "/" + parts[0];
                        due = "Hạn: 15/" + parts[1] + "/" + parts[0];
                    }
                }

                String status = invoice.getStatus();

                if ("paid".equals(status)) {
                    statusText = "Đã thanh toán";
                    statusBg = R.drawable.bg_chip_green;
                    statusColor = requireContext().getColor(R.color.primary);

                } else if ("overdue".equals(status)) {
                    statusText = "Quá hạn";
                    statusBg = R.drawable.bg_chip_red;
                    statusColor = requireContext().getColor(R.color.error);

                } else if ("confirmed".equals(status)) {
                    statusText = "Chờ thanh toán";
                } else {
                    statusText = "Nháp";
                    statusBg = R.drawable.bg_chip_gray;
                    statusColor = requireContext().getColor(R.color.text_hint);
                }
            }

            // ===== Contract =====
            ContractEntity contract = contractRepository.getActiveByUnitIdBlocking((int) unitId);

            String contractDays = "--";
            if (contract != null) {
                long diff = contract.getEndDate() - System.currentTimeMillis();

                if (diff <= 0) {
                    contractDays = "Hết hạn";
                } else {
                    long days = diff / (1000 * 60 * 60 * 24);
                    contractDays = "Còn " + days + " ngày";
                }
            }

            // ===== UI =====
            String finalApartmentName = apartmentName;
            String finalUnitName = unitName;
            String finalAmount = amount;
            String finalMonth = month;
            String finalDue = due;
            String finalContractDays = contractDays;

            String finalStatusText = statusText;
            int finalStatusBg = statusBg;
            int finalStatusColor = statusColor;

            int finalInvoiceId = invoiceId;
            String finalInvoiceStatus = invoiceStatus;

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;

                currentInvoiceId = finalInvoiceId;
                currentInvoiceStatus = finalInvoiceStatus;

                if ("paid".equals(finalInvoiceStatus)) {
                    binding.btnPayNow.setVisibility(View.GONE);
                } else {
                    binding.btnPayNow.setVisibility(View.VISIBLE);
                }

                // 🔥 SERVICES (giờ đã có data)
                serviceAdapter.setItems(serviceItems);

                // Header
                binding.textApartmentName.setText(finalApartmentName);
                binding.textUnitInfo.setText(finalUnitName != null ? finalUnitName : "--");

                // Invoice
                binding.textInvoiceAmount.setText(finalAmount);
                binding.textInvoiceMonth.setText(finalMonth);
                binding.textInvoiceDue.setText(finalDue);
                binding.textInvoiceTotal.setText(finalAmount);

                binding.chipInvoiceStatus.setText(finalStatusText);
                binding.chipInvoiceStatus.setBackgroundResource(finalStatusBg);
                binding.chipInvoiceStatus.setTextColor(finalStatusColor);

                binding.chipLatestInvoiceStatus.setText(finalStatusText);
                binding.chipLatestInvoiceStatus.setBackgroundResource(finalStatusBg);
                binding.chipLatestInvoiceStatus.setTextColor(finalStatusColor);

                binding.textContractDays.setText(finalContractDays);
                // Cập nhật UI Header
                binding.textApartmentName.setText(finalApartmentName);

                // Cập nhật lại listener cho nút chat với thông tin mới nhất
                setupChatButton();
            });

            if (apartment != null) {
                apartmentName = apartment.getName();
                this.adminId = apartment.getAdminId(); // Lấy adminId từ đây

                // 2. Lấy tên Admin từ adminId
                UserEntity adminUser = new UserRepository(requireActivity().getApplication()).getByIdSync(adminId);
                if (adminUser != null) {
                    this.adminName = adminUser.getName();
                }
            }

        }).start();

        // Payment
        binding.btnPayNow.setOnClickListener(v -> {
            if (currentInvoiceId == -1) {
                return;
            }

            // ✅ Check đã thanh toán chưa
            if ("paid".equals(currentInvoiceStatus)) {
                android.widget.Toast.makeText(getContext(), "Hóa đơn đã thanh toán rồi", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putInt("invoiceId", currentInvoiceId);

            com.and.apartmentmanager.presentation.ui.user.invoice.UserInvoiceDetailFragment fragment =
                    new com.and.apartmentmanager.presentation.ui.user.invoice.UserInvoiceDetailFragment();

            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();


        });

        binding.layoutContract.setOnClickListener(v -> {
            ContractDetailFragment fragment = new ContractDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putLong("unitId", unitId); // truyền nếu cần
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.layoutInvoice.setOnClickListener(v -> {

            UserStatisticsFragment fragment = new UserStatisticsFragment();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int getServiceIcon(String name) {
        return R.drawable.ic_home;
    }

    private void setupChatButton() {
        binding.btnChatWithAdmin.setOnClickListener(v -> {
            if (adminId == -1) {
                android.widget.Toast.makeText(getContext(), "Đang tải thông tin quản lý...", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            long mySelfId = SessionManager.getInstance(requireContext()).getUserId();

            // LẤY APARTMENT ID TỪ CHÍNH WORKSPACE FRAGMENT HIỆN TẠI
            long currentApartmentId = getArguments() != null ? getArguments().getLong(ARG_APARTMENT_ID) : -1;

            // Truyền thêm currentApartmentId vào đầu tiên
            com.and.apartmentmanager.presentation.ui.admin.chat.ChatFragment chatFragment =
                    com.and.apartmentmanager.presentation.ui.admin.chat.ChatFragment.newInstance(
                            currentApartmentId, // ĐẢM BẢO CHÍNH XÁC ID CHUNG CƯ
                            mySelfId,
                            this.adminName,
                            "Ban quản lý"
                    );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}