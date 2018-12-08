package com.fs.task;

import com.fs.generate.target.entity.YizhiHkjihuaObj;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepaymentVariable extends TaskVariable {

    public static ThreadLocal<RepaymentVariable> variable= new ThreadLocal<>();
    public  long firstDays=0;
    public  String payedWyjRiqi="";
    public  List<YizhiHkjihuaObj> lstHkjh=new ArrayList<>();
    public  Map<String,Statement> statementMap=new HashMap<>();

    public RepaymentVariable(String taskName, String dbpool) {
        super(taskName, dbpool);
        variable.set(this);
    }


}
