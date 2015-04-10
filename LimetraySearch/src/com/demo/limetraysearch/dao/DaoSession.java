package com.demo.limetraysearch.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.demo.limetraysearch.dao.SearchData;

import com.demo.limetraysearch.dao.SearchDataDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig searchDataDaoConfig;

    private final SearchDataDao searchDataDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        searchDataDaoConfig = daoConfigMap.get(SearchDataDao.class).clone();
        searchDataDaoConfig.initIdentityScope(type);

        searchDataDao = new SearchDataDao(searchDataDaoConfig, this);

        registerDao(SearchData.class, searchDataDao);
    }
    
    public void clear() {
        searchDataDaoConfig.getIdentityScope().clear();
    }

    public SearchDataDao getSearchDataDao() {
        return searchDataDao;
    }

}