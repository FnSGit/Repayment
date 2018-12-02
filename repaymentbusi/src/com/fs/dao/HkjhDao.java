package com.fs.dao;

import com.fs.dao.Dao;

public class HkjhDao extends Dao {
    public HkjhDao(String dbpool) {
        super(dbpool);
    }

    @Override
    protected void createStatement() {
        defaultStatement();
    }
}
