package com.fs.dao;

import com.fs.util.db.DataBase;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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

    protected Map<String, String> getParamMap(String... params) {
        Map<String, String> paraMap = new HashMap<>();
        for (int i=0;i<params.length;i+=2)
            paraMap.put(params[i],params[i+1]);
        return paraMap;
    }

}
