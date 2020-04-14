package com.android.jesse.biliparser.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.jesse.biliparser.db.bean.HistoryVideoBean;

import java.util.List;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/13
 */
@Dao
public interface HistoryVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertHistoryVideo(HistoryVideoBean... historyVideoBeans);

    @Query("SELECT * FROM historyvideo")
    List<HistoryVideoBean> queryAll();

    @Query("SELECT * FROM historyvideo where videoId = :videoId")
    HistoryVideoBean queryByVideoId(int videoId);

    @Query("DELETE FROM historyvideo")
    int clear();

    @Query("DELETE FROM historyvideo where videoId = :videoId")
    int deleteByVideoId(int videoId);

    @Query("UPDATE historyvideo SET currentIndex = :currentIndex where videoId = :videoId")
    int updateIndexByVideoId(int currentIndex,int videoId);


}
