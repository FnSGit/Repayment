/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/24 0024 下午 23:29
 * Description:业务逻辑工具
 */
package com.fs.busi.repayment;

import com.fs.constants.repayment.JihuaParam;
import com.fs.util.date.DateUtil;

public class RepayTool {

    public static String theYearToPay(String fkrq,int month,int jiesFs){
        //月息年本中间期还本日期处理
        String year=DateUtil.getNextMonth(fkrq, month);
        //对日减一处理
        if(jiesFs==JihuaParam.jiesFs3){
            year=DateUtil.getNextDate(year, -1);
        }
        return year;
    }
}
