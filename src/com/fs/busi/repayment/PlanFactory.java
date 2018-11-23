package com.fs.busi.repayment;

import com.fs.constants.repayment.JihuaParam;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;

import java.util.List;

public abstract class PlanFactory {

    protected JihuaParam jihuaParam;

    protected YizhiFkxxObj fkxxObj;



    public PlanFactory(JihuaParam jihuaParam, YizhiFkxxObj fkxxObj) {
        this.jihuaParam = jihuaParam;
        this.fkxxObj = fkxxObj;
    }

    public abstract List<YizhiHkjihuaObj> getPlan();
}
