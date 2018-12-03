package com.fs.dao;

import com.fs.generate.target.entity.YizhiHklsxxObj;
import com.fs.util.db.DataBase;
import com.fs.util.object.ObjectUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HklsDao extends Dao {
    public HklsDao(String dbpool, Statement statement) {
        super(dbpool,statement);
    }

    @Override
    protected void createStatement() {
        defaultStatement();
    }

    public HklsDao(String dbpool) {
        super(dbpool);
    }

    /**
     *
     * @param params 订单号
     * @return
     * @throws SQLException
     */
    public List<YizhiHklsxxObj> sel_hkls(String... params) throws SQLException {
        Map paraMap = getParamMap(params);
        List<YizhiHklsxxObj> hklsxxObjList = new ArrayList<>();
        String sql = "select * from yizhi_hklsxx where orderno='"+paraMap.get("orderno")+"' order by hkriqi,liushuih";
        ResultSet resultSet=DataBase.getResultset(statement, sql);
        while (resultSet.next()){
            YizhiHklsxxObj hklsxxObj = new YizhiHklsxxObj();
            ObjectUtil.loadResult(hklsxxObj, resultSet);
            hklsxxObjList.add(hklsxxObj);
        }
        return hklsxxObjList;
    }
}
