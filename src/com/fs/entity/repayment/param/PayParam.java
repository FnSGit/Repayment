package com.fs.entity.repayment.param;

import com.fs.constants.repayment.FeeEnum;
import com.fs.generate.target.entity.YizhiFkxxObj;

import java.util.HashMap;
import java.util.Map;

public class PayParam {
    protected double fkje;
    protected String fkrq;
    protected String scrq;
    protected String hkri;
    protected int jiesFs;
    protected int kouxiFs;
    protected int jixiFs;
    protected int qixian;
    protected double lilv;
    protected Map<FeeEnum, Integer> feeFsMap=new HashMap<>();

    public void initParam(YizhiFkxxObj fkxxObj) {
        this.fkje = Double.parseDouble(fkxxObj.getFkje());
        this.fkrq = fkxxObj.getFkrq();
        this.scrq = fkxxObj.getSchkr();
        this.hkri = "20";//还款日，默认20号。（周期）
        this.jiesFs = Integer.parseInt(fkxxObj.getJiesfs());
        this.kouxiFs = Integer.parseInt(fkxxObj.getKouxifs());
        this.jixiFs = Integer.parseInt(fkxxObj.getHxfs());
        this.qixian = qixian;
        this.lilv = lilv;
        this.feeFsMap = feeFsMap;
    }

    public PayParam(YizhiFkxxObj fkxxObj) {
      initParam(fkxxObj);
    }
}
