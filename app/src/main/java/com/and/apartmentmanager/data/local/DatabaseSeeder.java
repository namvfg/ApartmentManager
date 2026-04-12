package com.and.apartmentmanager.data.local;

import com.and.apartmentmanager.data.local.entity.*;
import com.and.apartmentmanager.util.DateTimeUtil;

import java.util.Arrays;

/**
 * Seed data mẫu cho 2 chung cư và toàn bộ dữ liệu liên quan.
 * Gọi trong RoomDatabase.Callback.onCreate() — chỉ chạy 1 lần khi DB tạo lần đầu.
 *
 * ID mapping sau khi seed:
 * Users        : 1=Admin Sunrise, 2=Admin Green, 3-6=Cư dân Sunrise, 7-9=Cư dân Green
 * Apartments   : 1=Sunrise City, 2=Green Park
 * Blocks       : 1-2=Sunrise (A,B), 3-4=Green (C,D)
 * Units        : 1-6=Sunrise, 7-11=Green
 * Services     : 1-4=Sunrise, 5-8=Green
 */
public class DatabaseSeeder {

    public static void seed(AppDatabase db) {
        seedUsers(db);
        seedApartments(db);
        seedBlocks(db);
        seedUnits(db);
        seedUserApartments(db);
        seedInviteCodes(db);
        seedContracts(db);
        seedServices(db);
        seedServicePriceHistory(db);
        seedUtilityReadings(db);
        seedInvoices(db);
        seedNotifications(db);
    }

    // ─────────────────────────────────────────────────────────────
    // 1. USERS
    // AllArgsConstructor: id, name, email, password, phone, role, isActive, isDeleted, deletedAt
    //
    // id=1 : Admin Sunrise City
    // id=2 : Admin Green Park
    // id=3 : Lê Văn Cường    (Sunrise, active)
    // id=4 : Phạm Thị Dung   (Sunrise, active)
    // id=5 : Hoàng Văn Em    (Sunrise, active)
    // id=6 : Vũ Thị Phương   (Sunrise, inactive)
    // id=7 : Đặng Văn Giang  (Green, active)
    // id=8 : Bùi Thị Hoa     (Green, active)
    // id=9 : Ngô Văn Ích     (Green, inactive)
    // ─────────────────────────────────────────────────────────────
    private static void seedUsers(AppDatabase db) {
        db.userDao().insert(new UserEntity(0, "Nguyễn Văn An",  "admin.sunrise@gmail.com", "admin123", "0901111111", "admin", true,  false, null,true));
        db.userDao().insert(new UserEntity(0, "Trần Thị Bảo",   "admin.green@gmail.com",   "admin123", "0902222222", "admin", true,  false, null,true));
        db.userDao().insert(new UserEntity(0, "Lê Văn Cường",   "cuong@gmail.com",          "user123",  "0911111111", "user",  true,  false, null,true));
        db.userDao().insert(new UserEntity(0, "Phạm Thị Dung",  "dung@gmail.com",           "user123",  "0912222222", "user",  true,  false, null,false));
        db.userDao().insert(new UserEntity(0, "Hoàng Văn Em",   "em@gmail.com",             "user123",  "0913333333", "user",  true,  false, null,false));
        db.userDao().insert(new UserEntity(0, "Vũ Thị Phương",  "phuong@gmail.com",         "user123",  "0914444444", "user",  false, false, null,false));
        db.userDao().insert(new UserEntity(0, "Đặng Văn Giang", "giang@gmail.com",          "user123",  "0921111111", "user",  true,  false, null,false));
        db.userDao().insert(new UserEntity(0, "Bùi Thị Hoa",    "hoa@gmail.com",            "user123",  "0922222222", "user",  true,  false, null,false));
        db.userDao().insert(new UserEntity(0, "Ngô Văn Ích",    "ich@gmail.com",            "user123",  "0923333333", "user",  false, false, null,false));
    }

    // ─────────────────────────────────────────────────────────────
    // 2. APARTMENTS
    // AllArgsConstructor: id, name, address, isActive, adminId
    //
    // id=1 : Sunrise City — admin_id=1
    // id=2 : Green Park   — admin_id=2
    // ─────────────────────────────────────────────────────────────
    private static void seedApartments(AppDatabase db) {
        db.apartmentDao().insert(new ApartmentEntity(0, "Sunrise City", "123 Nguyễn Văn Linh, Quận 7, TP.HCM", true, 1));
        db.apartmentDao().insert(new ApartmentEntity(0, "Green Park",   "456 Phạm Văn Đồng, Thủ Đức, TP.HCM",  true, 2));
    }

    // ─────────────────────────────────────────────────────────────
    // 3. BLOCKS
    // AllArgsConstructor: id, apartmentId, name
    //
    // id=1 : Block A (Sunrise)
    // id=2 : Block B (Sunrise)
    // id=3 : Block C (Green)
    // id=4 : Block D (Green)
    // ─────────────────────────────────────────────────────────────
    private static void seedBlocks(AppDatabase db) {
        db.blockDao().insert(new BlockEntity(0, 1, "Block A"));
        db.blockDao().insert(new BlockEntity(0, 1, "Block B"));
        db.blockDao().insert(new BlockEntity(0, 2, "Block C"));
        db.blockDao().insert(new BlockEntity(0, 2, "Block D"));
    }

    // ─────────────────────────────────────────────────────────────
    // 4. UNITS
    // AllArgsConstructor: id, blockId, name, floor
    //
    // Sunrise – Block A (block_id=1): id=1(A101), 2(A102), 3(A201), 4(A202)
    // Sunrise – Block B (block_id=2): id=5(B101), 6(B102)
    // Green   – Block C (block_id=3): id=7(C101), 8(C102), 9(C201)
    // Green   – Block D (block_id=4): id=10(D101), 11(D102)
    // ─────────────────────────────────────────────────────────────
    private static void seedUnits(AppDatabase db) {
        // Sunrise – Block A
        db.unitDao().insert(new UnitEntity(0, 1, "A101", 1));
        db.unitDao().insert(new UnitEntity(0, 1, "A102", 1));
        db.unitDao().insert(new UnitEntity(0, 1, "A201", 2));
        db.unitDao().insert(new UnitEntity(0, 1, "A202", 2));
        // Sunrise – Block B
        db.unitDao().insert(new UnitEntity(0, 2, "B101", 1));
        db.unitDao().insert(new UnitEntity(0, 2, "B102", 1));
        // Green – Block C
        db.unitDao().insert(new UnitEntity(0, 3, "C101", 1));
        db.unitDao().insert(new UnitEntity(0, 3, "C102", 1));
        db.unitDao().insert(new UnitEntity(0, 3, "C201", 2));
        // Green – Block D
        db.unitDao().insert(new UnitEntity(0, 4, "D101", 1));
        db.unitDao().insert(new UnitEntity(0, 4, "D102", 1));
    }

    // ─────────────────────────────────────────────────────────────
    // 5. USER_APARTMENTS
    // AllArgsConstructor: id, userId, apartmentId, unitId, status, inviteCodeUsed, joinedAt
    // ─────────────────────────────────────────────────────────────
    private static void seedUserApartments(AppDatabase db) {
        long now = System.currentTimeMillis();
        // Sunrise City (apartment_id=1)
        db.userApartmentDao().insert(new UserApartmentEntity(0, 3, 1, 1, "active",   "INV-A101", now));
        db.userApartmentDao().insert(new UserApartmentEntity(0, 4, 1, 2, "active",   "INV-A102", now));
        db.userApartmentDao().insert(new UserApartmentEntity(0, 5, 1, 3, "active",   "INV-A201", now));
        db.userApartmentDao().insert(new UserApartmentEntity(0, 6, 1, 4, "inactive", "INV-A202", now));
        // Green Park (apartment_id=2)
        db.userApartmentDao().insert(new UserApartmentEntity(0, 7, 2, 7,  "active",   "INV-C101", now));
        db.userApartmentDao().insert(new UserApartmentEntity(0, 8, 2, 8,  "active",   "INV-C102", now));
        db.userApartmentDao().insert(new UserApartmentEntity(0, 9, 2, 9,  "inactive", "INV-C201", now));
    }

    // ─────────────────────────────────────────────────────────────
    // 6. INVITE CODES
    // AllArgsConstructor: id, apartmentId, unitId, adminId, code, expiresAt, isUsed, usedBy
    // ─────────────────────────────────────────────────────────────
    private static void seedInviteCodes(AppDatabase db) {
        long in30days = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000;
        long expired  = System.currentTimeMillis() -  7L * 24 * 60 * 60 * 1000;

        // Sunrise City (apartment_id=1, admin_id=1)
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 1, 1, 1, "INV-A101",     in30days, true,  3));    // đã dùng bởi Cường
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 1, 2, 1, "INV-A102",     in30days, true,  4));    // đã dùng bởi Dung
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 1, 3, 1, "INV-A201",     in30days, true,  5));    // đã dùng bởi Em
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 1, 4, 1, "INV-A202",     in30days, true,  6));    // đã dùng bởi Phương
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 1, 5, 1, "INV-B101-NEW", in30days, false, null)); // chưa dùng, còn hạn
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 1, 6, 1, "INV-B101-EXP", expired,  false, null)); // hết hạn

        // Green Park (apartment_id=2, admin_id=2)
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 2, 7,  2, "INV-C101",     in30days, true,  7));    // đã dùng bởi Giang
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 2, 8,  2, "INV-C102",     in30days, true,  8));    // đã dùng bởi Hoa
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 2, 9,  2, "INV-C201",     in30days, true,  9));    // đã dùng bởi Ích
        db.inviteCodeDao().insert(new InviteCodeEntity(0, 2, 10, 2, "INV-D101-NEW", in30days, false, null)); // chưa dùng
    }

    // ─────────────────────────────────────────────────────────────
    // 7. CONTRACTS
    // AllArgsConstructor: id, userId, apartmentId, unitId, startDate, endDate,
    //                     billingDay, rentPrice, status, contractUrl,
    //                     createdBy, terminatedAt, terminatedBy, terminateReason, createdAt
    // ─────────────────────────────────────────────────────────────
    private static void seedContracts(AppDatabase db) {
        long now = System.currentTimeMillis();

        // Sunrise City (apartment_id=1, createdBy=1)
        db.contractDao().insert(new ContractEntity(0, 3, 1, 1,
                DateTimeUtil.monthOffset(-12), DateTimeUtil.monthOffset(12),  5,  4_500_000, "active",
                "https://cloudinary.com/sunrise/contract_cuong.pdf",
                1, null, null, null, DateTimeUtil.monthOffset(-12)));

        db.contractDao().insert(new ContractEntity(0, 4, 1, 2,
                DateTimeUtil.monthOffset(-6),  DateTimeUtil.monthOffset(18), 10,  5_000_000, "active",
                "https://cloudinary.com/sunrise/contract_dung.pdf",
                1, null, null, null, DateTimeUtil.monthOffset(-6)));

        db.contractDao().insert(new ContractEntity(0, 5, 1, 3,
                DateTimeUtil.monthOffset(-3),  DateTimeUtil.monthOffset(9),  15,  4_800_000, "active",
                "https://cloudinary.com/sunrise/contract_em.pdf",
                1, null, null, null, DateTimeUtil.monthOffset(-3)));

        db.contractDao().insert(new ContractEntity(0, 6, 1, 4,
                DateTimeUtil.monthOffset(-24), DateTimeUtil.monthOffset(-1),  5,  4_200_000, "expired",
                "https://cloudinary.com/sunrise/contract_phuong.pdf",
                1, null, null, null, DateTimeUtil.monthOffset(-24)));

        // Green Park (apartment_id=2, createdBy=2)
        db.contractDao().insert(new ContractEntity(0, 7, 2, 7,
                DateTimeUtil.monthOffset(-8),  DateTimeUtil.monthOffset(16),  1,  6_000_000, "active",
                "https://cloudinary.com/green/contract_giang.pdf",
                2, null, null, null, DateTimeUtil.monthOffset(-8)));

        db.contractDao().insert(new ContractEntity(0, 8, 2, 8,
                DateTimeUtil.monthOffset(-2),  DateTimeUtil.monthOffset(22), 20,  5_500_000, "active",
                "https://cloudinary.com/green/contract_hoa.pdf",
                2, null, null, null, DateTimeUtil.monthOffset(-2)));

        // Ích — terminated
        db.contractDao().insert(new ContractEntity(0, 9, 2, 9,
                DateTimeUtil.monthOffset(-18), DateTimeUtil.monthOffset(6),  10,  5_200_000, "terminated",
                "https://cloudinary.com/green/contract_ich.pdf",
                2, DateTimeUtil.monthOffset(-3), 2, "Cư dân chủ động chấm dứt hợp đồng trước hạn", DateTimeUtil.monthOffset(-18)));
    }

    // ─────────────────────────────────────────────────────────────
    // 8. SERVICES
    // AllArgsConstructor: id, apartmentId, name, pricingType, description
    //
    // Sunrise: id=1(Điện), 2(Nước), 3(Rác), 4(Gửi xe)
    // Green  : id=5(Điện), 6(Nước), 7(Rác), 8(Bảo vệ)
    // ─────────────────────────────────────────────────────────────
    private static void seedServices(AppDatabase db) {
        // Sunrise City (apartment_id=1)
        db.serviceDao().insert(new ServiceEntity(0, 1, "Điện",   "variable", "Điện tiêu thụ theo chỉ số công tơ"));
        db.serviceDao().insert(new ServiceEntity(0, 1, "Nước",   "variable", "Nước tiêu thụ theo chỉ số đồng hồ"));
        db.serviceDao().insert(new ServiceEntity(0, 1, "Rác",    "fixed",    "Phí vệ sinh môi trường cố định"));
        db.serviceDao().insert(new ServiceEntity(0, 1, "Gửi xe", "fixed",    "Phí giữ xe máy cố định hàng tháng"));
        // Green Park (apartment_id=2)
        db.serviceDao().insert(new ServiceEntity(0, 2, "Điện",   "variable", "Điện tiêu thụ theo chỉ số công tơ"));
        db.serviceDao().insert(new ServiceEntity(0, 2, "Nước",   "variable", "Nước tiêu thụ theo chỉ số đồng hồ"));
        db.serviceDao().insert(new ServiceEntity(0, 2, "Rác",    "fixed",    "Phí vệ sinh môi trường cố định"));
        db.serviceDao().insert(new ServiceEntity(0, 2, "Bảo vệ", "fixed",    "Phí an ninh bảo vệ 24/7"));
    }

    // ─────────────────────────────────────────────────────────────
    // 9. SERVICE PRICE HISTORY
    // AllArgsConstructor: id, serviceId, price, effectiveFrom,
    //                     applyFromNextCycle, isActive, changedBy, changedAt
    // ─────────────────────────────────────────────────────────────
    private static void seedServicePriceHistory(AppDatabase db) {
        // ── Sunrise City ──────────────────────────────────────────
        // Điện (service_id=1) — giá cũ inactive, giá mới active
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 1,  3_500, DateTimeUtil.monthOffset(-6),  false, false, 1, DateTimeUtil.monthOffset(-6)));
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 1,  3_800, DateTimeUtil.monthOffset(-1),  false, true,  1, DateTimeUtil.monthOffset(-1)));
        // Nước (service_id=2)
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 2, 15_000, DateTimeUtil.monthOffset(-6),  false, false, 1, DateTimeUtil.monthOffset(-6)));
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 2, 18_000, DateTimeUtil.monthOffset(-2),  false, true,  1, DateTimeUtil.monthOffset(-2)));
        // Rác (service_id=3) — fixed, không đổi
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 3, 50_000, DateTimeUtil.monthOffset(-12), false, true,  1, DateTimeUtil.monthOffset(-12)));
        // Gửi xe (service_id=4) — fixed, không đổi
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 4,100_000, DateTimeUtil.monthOffset(-12), false, true,  1, DateTimeUtil.monthOffset(-12)));

        // ── Green Park ────────────────────────────────────────────
        // Điện (service_id=5) — giá mới applyFromNextCycle=true
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 5,  3_600, DateTimeUtil.monthOffset(-3),  false, false, 2, DateTimeUtil.monthOffset(-3)));
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 5,  3_900, DateTimeUtil.monthOffset(-1),  true,  true,  2, DateTimeUtil.monthOffset(-1)));
        // Nước (service_id=6)
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 6, 16_000, DateTimeUtil.monthOffset(-6),  false, true,  2, DateTimeUtil.monthOffset(-6)));
        // Rác (service_id=7)
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 7, 55_000, DateTimeUtil.monthOffset(-12), false, true,  2, DateTimeUtil.monthOffset(-12)));
        // Bảo vệ (service_id=8)
        db.servicePriceHistoryDao().insert(new ServicePriceHistoryEntity(0, 8,150_000, DateTimeUtil.monthOffset(-12), false, true,  2, DateTimeUtil.monthOffset(-12)));
    }

    // ─────────────────────────────────────────────────────────────
    // 10. UTILITY READINGS
    // AllArgsConstructor: id, serviceId, unitId, month, previousReading,
    //                     currentReading, consumption, recordedBy, recordedAt
    // ─────────────────────────────────────────────────────────────
    private static void seedUtilityReadings(AppDatabase db) {
        long now    = System.currentTimeMillis();
        String prev = DateTimeUtil.monthString(-2);
        String last = DateTimeUtil.monthString(-1);

        // ── Sunrise City ──────────────────────────────────────────
        // A101 (unit_id=1) — Điện(1), Nước(2)
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 1, 1, prev, 1200, 1285, 85, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 2, 1, prev,  340,  356, 16, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 1, 1, last, 1285, 1374, 89, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 2, 1, last,  356,  373, 17, 1, now));
        // A102 (unit_id=2) — Điện(1), Nước(2)
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 1, 2, prev,  980, 1052, 72, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 2, 2, prev,  210,  224, 14, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 1, 2, last, 1052, 1130, 78, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 2, 2, last,  224,  238, 14, 1, now));
        // A201 (unit_id=3) — Điện(1), Nước(2)
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 1, 3, prev, 2100, 2198, 98, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 2, 3, prev,  520,  541, 21, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 1, 3, last, 2198, 2290, 92, 1, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 2, 3, last,  541,  558, 17, 1, now));

        // ── Green Park ────────────────────────────────────────────
        // C101 (unit_id=7) — Điện(5), Nước(6)
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 5, 7, prev,  500,  589, 89, 2, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 6, 7, prev,  120,  134, 14, 2, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 5, 7, last,  589,  672, 83, 2, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 6, 7, last,  134,  149, 15, 2, now));
        // C102 (unit_id=8) — Điện(5), Nước(6)
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 5, 8, prev,  750,  831, 81, 2, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 6, 8, prev,  180,  196, 16, 2, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 5, 8, last,  831,  918, 87, 2, now));
        db.utilityReadingDao().insert(new UtilityReadingEntity(0, 6, 8, last,  196,  213, 17, 2, now));
    }

    // ─────────────────────────────────────────────────────────────
    // 11. INVOICES + INVOICE ITEMS
    // InvoiceEntity AllArgsConstructor:
    //   id, unitId, apartmentId, month, totalAmount, status,
    //   createdAt, confirmedAt, confirmedBy, note
    //
    // InvoiceItemEntity AllArgsConstructor:
    //   id, invoiceId, serviceId, serviceName, price, consumption, total, type
    // ─────────────────────────────────────────────────────────────
    private static void seedInvoices(AppDatabase db) {
        long now     = System.currentTimeMillis();
        String month = DateTimeUtil.monthString(-1);

        // ── Sunrise City ──────────────────────────────────────────

        // A101 (unit=1) — paid
        // Rent:4.500.000 | Điện:89×3.800=338.200 | Nước:17×18.000=306.000 | Rác:50.000 | Gửi xe:100.000 | Total:5.294.200
        long id1 = db.invoiceDao().insert(new InvoiceEntity(0, 1, 1, month, 5_294_200, "paid", now, now, 1, null));
        db.invoiceItemDao().insertAll(Arrays.asList(
                new InvoiceItemEntity(0, (int)id1, null, "Tiền thuê", 4_500_000, null, 4_500_000, "rent"),
                new InvoiceItemEntity(0, (int)id1, 1, "Điện",          3_800, 89.0,   338_200, "variable"),
                new InvoiceItemEntity(0, (int)id1, 2, "Nước",         18_000, 17.0,   306_000, "variable"),
                new InvoiceItemEntity(0, (int)id1, 3, "Rác",          50_000, null,    50_000, "fixed"),
                new InvoiceItemEntity(0, (int)id1, 4, "Gửi xe",      100_000, null,   100_000, "fixed")
        ));

        // A102 (unit=2) — confirmed
        // Rent:5.000.000 | Điện:78×3.800=296.400 | Nước:14×18.000=252.000 | Rác:50.000 | Gửi xe:100.000 | Total:5.698.400
        long id2 = db.invoiceDao().insert(new InvoiceEntity(0, 2, 1, month, 5_698_400, "confirmed", now, now, 1, null));
        db.invoiceItemDao().insertAll(Arrays.asList(
                new InvoiceItemEntity(0, (int)id2, null, "Tiền thuê", 5_000_000, null, 5_000_000, "rent"),
                new InvoiceItemEntity(0, (int)id2, 1, "Điện",          3_800, 78.0,   296_400, "variable"),
                new InvoiceItemEntity(0, (int)id2, 2, "Nước",         18_000, 14.0,   252_000, "variable"),
                new InvoiceItemEntity(0, (int)id2, 3, "Rác",          50_000, null,    50_000, "fixed"),
                new InvoiceItemEntity(0, (int)id2, 4, "Gửi xe",      100_000, null,   100_000, "fixed")
        ));

        // A201 (unit=3) — draft
        // Rent:4.800.000 | Điện:92×3.800=349.600 | Nước:17×18.000=306.000 | Rác:50.000 | Gửi xe:100.000 | Total:5.605.600
        long id3 = db.invoiceDao().insert(new InvoiceEntity(0, 3, 1, month, 5_605_600, "draft", now, null, null, null));
        db.invoiceItemDao().insertAll(Arrays.asList(
                new InvoiceItemEntity(0, (int)id3, null, "Tiền thuê", 4_800_000, null, 4_800_000, "rent"),
                new InvoiceItemEntity(0, (int)id3, 1, "Điện",          3_800, 92.0,   349_600, "variable"),
                new InvoiceItemEntity(0, (int)id3, 2, "Nước",         18_000, 17.0,   306_000, "variable"),
                new InvoiceItemEntity(0, (int)id3, 3, "Rác",          50_000, null,    50_000, "fixed"),
                new InvoiceItemEntity(0, (int)id3, 4, "Gửi xe",      100_000, null,   100_000, "fixed")
        ));

        // ── Green Park ────────────────────────────────────────────

        // C101 (unit=7) — paid
        // Rent:6.000.000 | Điện:83×3.900=323.700 | Nước:15×16.000=240.000 | Rác:55.000 | Bảo vệ:150.000 | Total:6.768.700
        long id4 = db.invoiceDao().insert(new InvoiceEntity(0, 7, 2, month, 6_768_700, "paid", now, now, 2, null));
        db.invoiceItemDao().insertAll(Arrays.asList(
                new InvoiceItemEntity(0, (int)id4, null, "Tiền thuê", 6_000_000, null, 6_000_000, "rent"),
                new InvoiceItemEntity(0, (int)id4, 5, "Điện",          3_900, 83.0,   323_700, "variable"),
                new InvoiceItemEntity(0, (int)id4, 6, "Nước",         16_000, 15.0,   240_000, "variable"),
                new InvoiceItemEntity(0, (int)id4, 7, "Rác",          55_000, null,    55_000, "fixed"),
                new InvoiceItemEntity(0, (int)id4, 8, "Bảo vệ",      150_000, null,   150_000, "fixed")
        ));

        // C102 (unit=8) — overdue
        // Rent:5.500.000 | Điện:87×3.900=339.300 | Nước:17×16.000=272.000 | Rác:55.000 | Bảo vệ:150.000 | Total:6.316.300
        long id5 = db.invoiceDao().insert(new InvoiceEntity(0, 8, 2, month, 6_316_300, "overdue", now, now, 2, "Quá hạn thanh toán, vui lòng liên hệ ban quản lý"));
        db.invoiceItemDao().insertAll(Arrays.asList(
                new InvoiceItemEntity(0, (int)id5, null, "Tiền thuê", 5_500_000, null, 5_500_000, "rent"),
                new InvoiceItemEntity(0, (int)id5, 5, "Điện",          3_900, 87.0,   339_300, "variable"),
                new InvoiceItemEntity(0, (int)id5, 6, "Nước",         16_000, 17.0,   272_000, "variable"),
                new InvoiceItemEntity(0, (int)id5, 7, "Rác",          55_000, null,    55_000, "fixed"),
                new InvoiceItemEntity(0, (int)id5, 8, "Bảo vệ",      150_000, null,   150_000, "fixed")
        ));
    }

    // ─────────────────────────────────────────────────────────────
    // 12. NOTIFICATIONS
    // AllArgsConstructor: id, apartmentId, title, content, type, target, targetUserId, createdBy, createdAt
    // ─────────────────────────────────────────────────────────────
    private static void seedNotifications(AppDatabase db) {
        long now = System.currentTimeMillis();

        // ── Sunrise City (apartment_id=1, createdBy=1) ────────────
        db.notificationDao().insert(new NotificationEntity(0, 1,
                "Thông báo bảo trì thang máy",
                "Thang máy Block A sẽ bảo trì vào ngày 20/03/2026 từ 8h-12h. Cư dân vui lòng sử dụng cầu thang bộ trong thời gian này.",
                "manual", "all", null, 1, now));

        db.notificationDao().insert(new NotificationEntity(0, 1,
                "Hóa đơn tháng " + DateTimeUtil.monthString(-1) + " đã sẵn sàng",
                "Hóa đơn tháng " + DateTimeUtil.monthString(-1) + " đã được tạo. Vui lòng kiểm tra và thanh toán trước ngày 15 tháng này.",
                "auto", "all", null, 1, now));

        db.notificationDao().insert(new NotificationEntity(0, 1,
                "Nhắc nhở thanh toán căn hộ A102",
                "Hóa đơn tháng " + DateTimeUtil.monthString(-1) + " của căn hộ A102 sắp đến hạn. Vui lòng thanh toán sớm để tránh phát sinh phí trễ hạn.",
                "auto", "user", 4, 1, now)); // gửi riêng cho Dung (user_id=4)

        // ── Green Park (apartment_id=2, createdBy=2) ──────────────
        db.notificationDao().insert(new NotificationEntity(0, 2,
                "Lịch vệ sinh hồ bơi tháng 3",
                "Hồ bơi tòa nhà sẽ ngừng hoạt động để vệ sinh định kỳ từ ngày 18-19/03/2026. Xin lỗi vì sự bất tiện này.",
                "manual", "all", null, 2, now));

        db.notificationDao().insert(new NotificationEntity(0, 2,
                "Hóa đơn tháng " + DateTimeUtil.monthString(-1) + " đã sẵn sàng",
                "Hóa đơn tháng " + DateTimeUtil.monthString(-1) + " đã được tạo. Vui lòng kiểm tra và thanh toán trước ngày 15 tháng này.",
                "auto", "all", null, 2, now));

        db.notificationDao().insert(new NotificationEntity(0, 2,
                "Hóa đơn quá hạn căn hộ C102",
                "Hóa đơn tháng " + DateTimeUtil.monthString(-1) + " của căn hộ C102 đã quá hạn thanh toán. Vui lòng liên hệ ban quản lý để được hỗ trợ.",
                "auto", "user", 8, 2, now)); // gửi riêng cho Hoa (user_id=8)
    }
}