package com.fs.entity.repayment.entity;

import com.fs.entity.repayment.param.PayParam;
import com.fs.generate.target.entity.YizhiFkxxObj;

public class BudgetEntity extends PayParam {
    protected double lixi=0;
    protected double benj=0;
    protected double fee=0;
    protected double fwfee=0;
    protected double qdffee=0;
    protected long days=30;//默认计息天数

    public BudgetEntity(YizhiFkxxObj fkxxObj) {
        super(fkxxObj);
    }

    public BudgetEntity(PayParam payParam) {
        super(payParam);
    }
}
