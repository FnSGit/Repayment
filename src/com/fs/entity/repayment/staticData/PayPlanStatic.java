/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/25 0025 下午 21:49
 * Description:静态公用变量
 */
package com.fs.entity.repayment.staticData;

import com.fs.generate.target.entity.YizhiHkjihuaObj;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  PayPlanStatic {
    public static long firstDays;
    public static String payedWyjRiqi;
    public static List<YizhiHkjihuaObj> lstHkjh;
    public static Map<String,Statement> statementMap=new HashMap<>();
}
