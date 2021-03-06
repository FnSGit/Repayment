package com.fs.dao;

import java.sql.Statement;
import java.util.Map;

public class FkxxDao extends Dao {

    public FkxxDao(String dbpool) {
        super(dbpool);
    }

    public FkxxDao(String dbpool, Statement statement) {
        super(dbpool, statement);
    }

    @Override
    protected void createStatement() {
        defaultStatement();
    }

    /**
     * 按分组号获取数据
     * @param params
     * @return
     */
    public String sel_Fkxx_ByGroupId(String... params) {
        Map paraMap = getParamMap(params);
        String sql = "select * from yizhi_fkxx where plfzuhao ='"+paraMap.get("plfzuhao")+"'";
        return sql;
    }

    /**
     * 放款信息表按分组号分组
     * @param
     * @return
     */
    public String fkxx_Group_byPlfzuhao() {
        String sql = "SELECT plfzuhao FROM yizhi_fkxx GROUP BY plfzuhao";
        return sql;
    }
}
