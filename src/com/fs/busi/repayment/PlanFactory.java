package com.fs.busi.repayment;

import com.fs.entity.repayment.param.PayParam;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;

import java.util.List;

public abstract class PlanFactory {

    protected PayParam payParam;




    public PlanFactory(PayParam payParam) {
        this.payParam = payParam;
    }

    public abstract List<YizhiHkjihuaObj> getPlan(YizhiFkxxObj fkxxObj);
}
