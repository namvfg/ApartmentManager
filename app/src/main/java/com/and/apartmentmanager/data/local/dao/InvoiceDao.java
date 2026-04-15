package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.dto.UnitBlockDTO;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;

import java.util.List;

@Dao
public interface InvoiceDao {
    @Insert
    long insert(InvoiceEntity invoice);

    @Update
    void update(InvoiceEntity invoice);

    @Query("SELECT * FROM invoices WHERE unit_id = :unitId ORDER BY created_at DESC")
    LiveData<List<InvoiceEntity>> getByUnitId(int unitId);

    @Query("SELECT * FROM invoices WHERE unit_id = :unitId ORDER BY created_at DESC LIMIT 1")
    InvoiceEntity getLatestByUnitId(long unitId);

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    InvoiceEntity getByIdSync(long invoiceId);

    @Query("UPDATE invoices SET status = 'confirmed', confirmed_at = :time, confirmed_by = :adminId WHERE id = :invoiceId")
    void confirm(long invoiceId, long time, int adminId);

    @Query("SELECT * FROM invoices ORDER BY created_at DESC")
    LiveData<List<InvoiceEntity>> getAllInvoices();

    // Truy vấn kết hợp bảng Units và Blocks để lấy tên Tòa theo mã Phòng
    @Query("SELECT u.id AS unit_id, b.name AS block_name FROM Units u INNER JOIN Blocks b ON u.block_id = b.id")
    androidx.lifecycle.LiveData<java.util.List<UnitBlockDTO>> getUnitBlockMappings();

    // Lệnh càn quét: Chuyển tất cả hóa đơn "Chờ TT" thành "Quá hạn"
    @androidx.room.Query("UPDATE Invoices SET status = 'overdue' WHERE status = 'confirmed'")
    void updateStatusToOverdue();

    // Nối bảng để lấy hóa đơn dựa vào userId đang đăng nhập
    @androidx.room.Query("SELECT invoices.* FROM invoices " +
            "INNER JOIN user_apartments ON invoices.unit_id = user_apartments.unit_id " +
            "WHERE user_apartments.user_id = :userId")
    androidx.lifecycle.LiveData<java.util.List<com.and.apartmentmanager.data.local.entity.InvoiceEntity>> getInvoicesByUserId(int userId);

    // Đi tìm tên Phòng (ví dụ A102) dựa vào ID
    @androidx.room.Query("SELECT name FROM units WHERE id = :unitId")
    androidx.lifecycle.LiveData<String> getUnitNameById(int unitId);

    // Dùng tạm UnitBlockDTO để gom ID và Tên phòng (Lách luật gán name thành block_name)
    @androidx.room.Query("SELECT id AS unit_id, name AS block_name FROM units")
    androidx.lifecycle.LiveData<java.util.List<UnitBlockDTO>> getAllUnitNames();

    @Query("SELECT COUNT(*) FROM invoices WHERE status = 'draft'")
    int getDraftInvoices();



}
