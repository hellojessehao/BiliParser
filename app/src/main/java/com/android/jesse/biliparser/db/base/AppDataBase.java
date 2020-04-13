package com.android.jesse.biliparser.db.base;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.db.dao.HistoryVideoDao;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/8
 */
@Database(entities = {HistoryVideoBean.class}, version = 1, exportSchema = false)
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
//                .addMigrations(migration_1_2)  数据库升级时使用
                .build();
    }


    public abstract HistoryVideoDao getHistoryVideoDao();
}
