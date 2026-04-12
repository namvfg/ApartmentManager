package com.and.apartmentmanager.data.local;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.and.apartmentmanager.data.local.dao.ApartmentDao;
import com.and.apartmentmanager.data.local.dao.BlockDao;
import com.and.apartmentmanager.data.local.dao.ContractDao;
import com.and.apartmentmanager.data.local.dao.InviteCodeDao;
import com.and.apartmentmanager.data.local.dao.InvoiceDao;
import com.and.apartmentmanager.data.local.dao.InvoiceItemDao;
import com.and.apartmentmanager.data.local.dao.NotificationDao;
import com.and.apartmentmanager.data.local.dao.ServiceDao;
import com.and.apartmentmanager.data.local.dao.ServicePriceHistoryDao;
import com.and.apartmentmanager.data.local.dao.UnitDao;
import com.and.apartmentmanager.data.local.dao.UserApartmentDao;
import com.and.apartmentmanager.data.local.dao.UserDao;
import com.and.apartmentmanager.data.local.dao.UtilityReadingDao;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.InviteCodeEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceItemEntity;
import com.and.apartmentmanager.data.local.entity.NotificationEntity;
import com.and.apartmentmanager.data.local.entity.ServiceEntity;
import com.and.apartmentmanager.data.local.entity.ServicePriceHistoryEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.local.entity.UtilityReadingEntity;

import java.util.concurrent.Executors;

@Database(
        entities = {
                UserEntity.class,
                ApartmentEntity.class,
                UserApartmentEntity.class,
                InviteCodeEntity.class,
                BlockEntity.class,
                UnitEntity.class,
                ContractEntity.class,
                ServiceEntity.class,
                ServicePriceHistoryEntity.class,
                UtilityReadingEntity.class,
                InvoiceEntity.class,
                InvoiceItemEntity.class,
                NotificationEntity.class,
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract ApartmentDao apartmentDao();

    public abstract UserApartmentDao userApartmentDao();

    public abstract InviteCodeDao inviteCodeDao();

    public abstract BlockDao blockDao();

    public abstract UnitDao unitDao();

    public abstract ContractDao contractDao();

    public abstract ServiceDao serviceDao();

    public abstract ServicePriceHistoryDao servicePriceHistoryDao();

    public abstract UtilityReadingDao utilityReadingDao();

    public abstract InvoiceDao invoiceDao();

    public abstract InvoiceItemDao invoiceItemDao();

    public abstract NotificationDao notificationDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "apartment_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        db.execSQL("PRAGMA foreign_keys = ON");
                        Log.d("DB_SEED", "onOpen called");
                    }

                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase database = getInstance(context);
                            DatabaseSeeder.seed(database);
                        });
                    }
                })
                    .build();
        }
        return INSTANCE;
    }
}
