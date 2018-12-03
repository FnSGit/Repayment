package com.fs.task;

import com.fs.generate.target.entity.YizhiHkjihuaObj;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepaymentVariable extends TaskVariable {
    public RepaymentVariable(String taskName, String dbpool) {
        super(taskName, dbpool);
    }

    public  long firstDays;
    public  String payedWyjRiqi;
    public  List<YizhiHkjihuaObj> lstHkjh;
    public  Map<String,Statement> statementMap=new HashMap<>();

}
