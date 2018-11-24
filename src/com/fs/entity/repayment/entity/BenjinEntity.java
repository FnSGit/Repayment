package com.fs.entity.repayment.entity;

import com.fs.entity.repayment.param.PayParam;
import com.fs.generate.target.entity.YizhiFkxxObj;

public class BenjinEntity extends BudgetEntity{
    public boolean isLast;

    public BenjinEntity(YizhiFkxxObj fkxxObj) {
        super(fkxxObj);
    }

    public BenjinEntity(PayParam payParam) {
        super(payParam);
    }
}
