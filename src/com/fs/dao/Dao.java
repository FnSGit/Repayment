package com.fs.dao;

import com.fs.util.db.DataBase;

import java.sql.Connection;
import java.sql.Statement;

public abstract class Dao {
    protected String dbpool;
    protected Statement statement;
    protected Connection connection;

    public Dao(String dbpool) {
       initDb(dbpool);
    }

    public void initDb(String dbpool) {
        this.dbpool=dbpool;
        this.connection = DataBase.getConn(dbpool);
        this.statement = DataBase.getStatement(dbpool);
    }


}
