package com.fs.entity.repayment.entity;

import com.fs.constants.repayment.FeeEnum;
import com.fs.generate.target.entity.YizhiFkxxObj;

import java.util.HashMap;
import java.util.Map;

public class FeeEntity extends BudgetEntity{

    public Map<FeeEnum, Integer> feeFsMap=new HashMap<>();
    public Map<FeeEnum, Double> feeLvMap=new HashMap<>();

    public FeeEntity(YizhiFkxxObj fkxxObj) {
        super(fkxxObj);
        feeFsMap.put(FeeEnum.fwfFee, Integer.parseInt(fkxxObj.getFysqfs()));
        feeFsMap.put(FeeEnum.qdfFee, Integer.parseInt(fkxxObj.getQdffsqfs()));
        feeLvMap.put(FeeEnum.fwfFee, Double.parseDouble(fkxxObj.getFwflv()));
        feeLvMap.put(FeeEnum.qdfFee, Double.parseDouble(fkxxObj.getQudfflv()));
    }

}
