package com.and.apartmentmanager.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ContractDao;
import com.and.apartmentmanager.data.local.dao.UserApartmentDao;
import com.and.apartmentmanager.data.local.dao.UserDao;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao dao;
    private final UserApartmentDao userApartmentDao;
    private final ContractDao contractDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.userDao();
        userApartmentDao = db.userApartmentDao();
        contractDao = db.contractDao();
    }

    public LiveData<List<UserEntity>> getAll() {
        return dao.getAll();
    }

    public LiveData<UserEntity> getById(int id) {
        return dao.getById(id);
    }

    /**
     * Blocking — dùng trong background thread.
     * Dùng cho màn ProfileDetail (Người 1).
     */
    public UserEntity getByIdBlocking(int id) {
        return dao.getByIdBlocking(id);
    }

    // Blocking — dùng trong background thread (ví dụ: kiểm tra login)
    public UserEntity getByEmail(String email) {
        return dao.getByEmail(email);
    }

    public LiveData<List<UserEntity>> getByRole(String role) {
        return dao.getByRole(role);
    }

    public void insert(UserEntity user) {
        executor.execute(() -> dao.insert(user));
    }

    public void update(UserEntity user) {
        executor.execute(() -> dao.update(user));
    }

    public void softDelete(int id) {
        executor.execute(() -> dao.softDelete(id, System.currentTimeMillis()));
    }

    // -----------------------------
    // Auth/Profile (Người 1)
    // -----------------------------

    public static class LoginResult {
        public final long userId;
        public final String role;
        public final long apartmentId;

        public LoginResult(long userId, String role, long apartmentId) {
            this.userId = userId;
            this.role = role;
            this.apartmentId = apartmentId;
        }
    }

    /**
     * Blocking — gọi từ background thread.
     * UC03: Đăng nhập + kiểm tra isActive + route theo role.
     */
    public LoginResult loginBlocking(String email, String password) {
        UserEntity user = dao.getByEmail(email);
        if (user == null) return null;
        if (user.isDeleted() || !user.isActive()) return null;
        if (user.getPassword() == null || !user.getPassword().equals(password)) return null;

        Integer apartmentId = userApartmentDao.getActiveApartmentIdByUserId(user.getId());
        long apId = apartmentId == null ? -1L : apartmentId.longValue();
        return new LoginResult(user.getId(), user.getRole(), apId);
    }

    /**
     * Blocking — gọi từ background thread.
     * UC01: Đăng ký (role mặc định = "user").
     */
    public UserEntity registerBlocking(String name,
                                         String email,
                                         String phone,
                                         String password) {
        UserEntity existed = dao.getByEmail(email);
        if (existed != null && !existed.isDeleted()) {
            return null; // email đã tồn tại
        }

        // Không gắn chung cư ngay khi đăng ký (workspace sẽ chỉ định sau bằng mã mời / contract).
        UserEntity newUser = UserEntity.builder()
                .id(0)
                .name(name)
                .email(email)
                .password(password)
                .phone(phone)
                .role("user")
                .isActive(true)
                .isDeleted(false)
                .deletedAt(null)
                .build();

        dao.insert(newUser);
        // Dao.insert() là void nên lấy lại id bằng query theo email.
        return dao.getByEmail(email);
    }

    /**
     * Blocking — gọi từ background thread.
     * UC06: Đổi mật khẩu (Manager bắt buộc đổi lần đầu không làm ở P1).
     */
    public boolean changePasswordBlocking(int userId, String oldPassword, String newPassword) {
        UserEntity user = dao.getByIdBlocking(userId);
        if (user == null) return false;
        if (user.isDeleted() || !user.isActive()) return false;
        if (user.getPassword() == null || !user.getPassword().equals(oldPassword)) return false;

        user.setPassword(newPassword);
        dao.update(user);
        return true;
    }

    /**
     * Blocking — gọi từ background thread.
     * Dùng cho Forgot/OTP flow.
     */
    public boolean resetPasswordBlocking(String email, String newPassword) {
        UserEntity user = dao.getByEmail(email);
        if (user == null) return false;
        if (user.isDeleted() || !user.isActive()) return false;

        user.setPassword(newPassword);
        dao.update(user);
        return true;
    }

    /**
     * Blocking — gọi từ background thread.
     * UC05: Sửa thông tin cá nhân (name, phone).
     */
    public boolean updateProfileBlocking(int userId, String newName, String newPhone) {
        UserEntity user = dao.getByIdBlocking(userId);
        if (user == null) return false;
        if (user.isDeleted() || !user.isActive()) return false;

        user.setName(newName);
        user.setPhone(newPhone);
        dao.update(user);
        return true;
    }

    /**
     * Blocking — gọi từ background thread.
     * UC07: Yêu cầu xóa tài khoản (soft delete).
     * Chỉ cho phép nếu user KHÔNG có hợp đồng active.
     */
    public boolean requestDeleteAccountBlocking(int userId) {
        if (contractDao.countActiveContractsByUserId(userId) > 0) {
            return false;
        }
        dao.softDelete(userId, System.currentTimeMillis());
        Log.d("UserRepository", "requestDeleteAccountBlocking success, userId=" + userId);
        return true;
    }

}
