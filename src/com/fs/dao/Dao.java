package com.fs.dao;

import com.fs.util.db.DataBase;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public abstract class Dao {
    protected String dbpool;
    protected Statement statement;

    public Dao(String dbpool) {
        this.dbpool = dbpool;
        createStatement();
    }

    public Dao(String dbpool,Statement statement) {
        this.dbpool=dbpool;
        this.statement=statement;
    }
    protected abstract void createStatement();

    protected void defaultStatement() {
       statement=DataBase.getStatement(dbpool);
    }
    protected Map<String, String> getParamMap(String... params) {
        Map<String, String> paraMap = new HashMap<>();
        for (int i=0;i<params.length;i+=2)
            paraMap.put(params[i],params[i+1]);
        return paraMap;
    }

    public void stateClose(String daoName)  {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit(String daoName) {
        DataBase.commit(dbpool,statement);
    }

    public Statement getStatement() {
        if (statement==null)
            defaultStatement();
        return statement;
    }

}
