package com.android.jesse.biliparser.db.base;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.jesse.biliparser.db.bean.CollectionBean;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.db.dao.CollectionDao;
import com.android.jesse.biliparser.db.dao.HistoryVideoDao;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/8
 */
@Database(entities = {HistoryVideoBean.class,CollectionBean.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    private static final String DB_NAME = "AppDatabase.db";

    private static volatile AppDataBase instance;

    static synchronized AppDataBase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static AppDataBase create(final Context context) {
        return Room.databaseBuilder(context, AppDataBase.class, DB_NAME)
                .addMigrations(migration_1_2)  //数据库升级时使用
                .build();
    }

    private static Migration migration_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE historyvideo ADD date TEXT DEFAULT ''");
        }
    };

    public abstract HistoryVideoDao getHistoryVideoDao();

    public abstract CollectionDao getCollectionDao();

}
