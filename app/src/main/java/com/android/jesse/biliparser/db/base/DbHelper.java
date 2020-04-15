package com.android.jesse.biliparser.db.base;

import android.content.Context;

import com.android.jesse.biliparser.db.bean.CollectionBean;
import com.android.jesse.biliparser.db.bean.HistoryVideoBean;
import com.android.jesse.biliparser.db.dao.CollectionDao;
import com.android.jesse.biliparser.db.dao.HistoryVideoDao;

import java.util.List;

/**
 * @Description: 数据库调用方法类
 * @author: zhangshihao
 * @date: 2020/4/13
 */
public class DbHelper implements HistoryVideoDao,CollectionDao {

    private static final String TAG = DbHelper.class.getSimpleName();
    private static DbHelper instance;
    private Context mContext;
    private AppDataBase appDataBase;
    private HistoryVideoDao historyVideoDao;
    private CollectionDao collectionDao;

    public DbHelper(Context context){
        this.mContext = context;
        appDataBase = AppDataBase.getInstance(this.mContext);
        historyVideoDao = appDataBase.getHistoryVideoDao();
        collectionDao = appDataBase.getCollectionDao();
    }

    /**
     * 必须在使用前执行，否则报空指针异常
     */
    public static void initInstance(Context context){
        if(instance == null){
            instance = new DbHelper(context.getApplicationContext());
        }
    }

    public static DbHelper getInstance(){
        return instance;
    }

    @Override
    public List<Long> insertHistoryVideo(HistoryVideoBean... historyVideoBeans) {
        return historyVideoDao.insertHistoryVideo(historyVideoBeans);
    }

    @Override
    public List<HistoryVideoBean> queryAll() {
        return historyVideoDao.queryAll();
    }

    @Override
    public HistoryVideoBean queryByVideoId(int videoId) {
        return historyVideoDao.queryByVideoId(videoId);
    }

    @Override
    public int clear() {
        return historyVideoDao.clear();
    }

    @Override
    public int deleteByVideoId(int videoId) {
        return historyVideoDao.deleteByVideoId(videoId);
    }

    @Override
    public int updateIndexByVideoId(int currentIndex, int videoId) {
        return historyVideoDao.updateIndexByVideoId(currentIndex,videoId);
    }

    @Override
    public List<Long> insertCollection(CollectionBean... collectionBeans) {
        return collectionDao.insertCollection(collectionBeans);
    }

    @Override
    public List<CollectionBean> queryAllCollection() {
        return collectionDao.queryAllCollection();
    }

    @Override
    public CollectionBean queryCollectionByVideoId(int videoId) {
        return collectionDao.queryCollectionByVideoId(videoId);
    }

    @Override
    public int clearCollection() {
        return collectionDao.clearCollection();
    }

    @Override
    public int deleteCollectionByVideoId(int videoId) {
        return collectionDao.deleteCollectionByVideoId(videoId);
    }

    @Override
    public int updateCollectionIndexByVideoId(int currentIndex, int videoId) {
        return collectionDao.updateCollectionIndexByVideoId(currentIndex,videoId);
    }
}
