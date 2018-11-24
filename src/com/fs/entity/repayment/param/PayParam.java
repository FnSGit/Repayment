package com.fs.entity.repayment.param;

import com.fs.constants.repayment.FeeEnum;
import com.fs.generate.target.entity.YizhiFkxxObj;

import java.util.HashMap;
import java.util.Map;

public class PayParam {
    protected String orderno;
    protected double fkje;
    protected String fkrq;
    protected String scrq;
    protected String hkri="20";
    protected int jiesFs=0;
    protected int kouxiFs=0;
    protected int jixiFs=0;
    protected int qixian;
    protected double lilv;
    protected int specialPro=0;
    protected Map<FeeEnum, Integer> feeFsMap=new HashMap<>();
    protected Map<FeeEnum, Double> feeLvMap=new HashMap<>();

    private void initParam(YizhiFkxxObj fkxxObj) {
        this.orderno=fkxxObj.getOrderno();
        this.fkje = Double.parseDouble(fkxxObj.getFkje());
        this.fkrq = fkxxObj.getFkrq();
        this.scrq = fkxxObj.getSchkr();
//        this.hkri = "20";//还款日，默认20号。（周期）
        this.jiesFs = Integer.parseInt(fkxxObj.getJiesfs());
        this.kouxiFs = Integer.parseInt(fkxxObj.getKouxifs());
        this.jixiFs = Integer.parseInt(fkxxObj.getHxfs());
        this.qixian = Integer.parseInt(fkxxObj.getQixian());
        this.lilv = Double.parseDouble(fkxxObj.getLilv());

        feeFsMap.put(FeeEnum.fwfFee, Integer.parseInt(fkxxObj.getFysqfs()));
        feeFsMap.put(FeeEnum.qdfFee, Integer.parseInt(fkxxObj.getQdffsqfs()));
        feeLvMap.put(FeeEnum.fwfFee, Double.parseDouble(fkxxObj.getFwflv()));
        feeLvMap.put(FeeEnum.qdfFee, Double.parseDouble(fkxxObj.getQudfflv()));

        try {
            this.specialPro = Integer.parseInt(fkxxObj.getSpeclpro());
        } catch (Exception e) {
            // 防止特殊操作标志为空或字符不符，否则默认为0
        }
    }

    public PayParam(YizhiFkxxObj fkxxObj) {
      initParam(fkxxObj);
    }

    public String getOrderno() {
        return orderno;
    }

    public double getFkje() {
        return fkje;
    }

    public String getFkrq() {
        return fkrq;
    }

    public String getScrq() {
        return scrq;
    }

    public String getHkri() {
        return hkri;
    }

    public int getJiesFs() {
        return jiesFs;
    }

    public int getKouxiFs() {
        return kouxiFs;
    }

    public int getJixiFs() {
        return jixiFs;
    }

    public int getQixian() {
        return qixian;
    }

    public double getLilv() {
        return lilv;
    }

    public int getSpecialPro() {
        return specialPro;
    }

    public Map<FeeEnum, Integer> getFeeFsMap() {
        return feeFsMap;
    }

    public Map<FeeEnum, Double> getFeeLvMap() {
        return feeLvMap;
    }
}
