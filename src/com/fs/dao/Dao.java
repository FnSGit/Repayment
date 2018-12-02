package com.fs.dao;

import com.fs.util.db.DataBase;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static com.fs.entity.repayment.staticData.PayPlanStatic.statementMap;

public abstract class Dao {
    protected String dbpool;


    public Dao(String dbpool) {
        this.dbpool = dbpool;
        createStatement();
    }

    protected abstract void createStatement();

    protected void defaultStatement() {
        statementMap.put(this.getClass().getName(), DataBase.getStatement(dbpool));
    }
    protected Map<String, String> getParamMap(String... params) {
        Map<String, String> paraMap = new HashMap<>();
        for (int i=0;i<params.length;i+=2)
            paraMap.put(params[i],params[i+1]);
        return paraMap;
    }

    public void stateClose(String daoName)  {
        try {
            statementMap.get(daoName).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit(String daoName) {
        DataBase.commit(dbpool,statementMap.get(daoName));
    }

    public Statement getStatement() {
        if (statementMap.get(dbpool)==null)
            defaultStatement();
        return statementMap.get(this.getClass().getName());
    }

}
