package com.android.jesse.biliparser.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.jesse.biliparser.db.bean.CollectionBean;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;

import java.util.List;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/13
 */
@Dao
public interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCollection(CollectionBean... collectionBeans);

    @Query("SELECT * FROM collection")
    List<CollectionBean> queryAllCollection();

    @Query("SELECT * FROM collection where videoId = :videoId")
    CollectionBean queryCollectionByVideoId(int videoId);

    @Query("DELETE FROM collection")
    int clearCollection();

    @Query("DELETE FROM collection where videoId = :videoId")
    int deleteCollectionByVideoId(int videoId);

    @Query("UPDATE collection SET currentIndex = :currentIndex where videoId = :videoId")
    int updateCollectionIndexByVideoId(int currentIndex, int videoId);

}
