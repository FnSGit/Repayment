package com.fs.dao;

import java.sql.Statement;

public class HkjhDao extends Dao {
    public HkjhDao(String dbpool) {
        super(dbpool);
    }

    public HkjhDao(String dbpool, Statement statement) {
        super(dbpool, statement);
    }

    @Override
    protected void createStatement() {
        defaultStatement();
    }
}
