package com.fs.entity.param;

import com.fs.generate.target.entity.YizhiFkxxObj;

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
    protected String extraDate;
    protected double extraMoney;



    public PayParam(YizhiFkxxObj fkxxObj) {
      initParam(fkxxObj);
    }

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

        try {
            this.specialPro = Integer.parseInt(fkxxObj.getSpeclpro());
            this.extraMoney = Double.parseDouble(fkxxObj.getExtrmony());
            this.extraDate=fkxxObj.getExtrdate();
        } catch (Exception e) {
            // 防止特殊操作标志为空或字符不符，否则默认为0
        }
    }

    public String getExtraDate() {
        return extraDate;
    }

    public void setExtraDate(String extraDate) {
        this.extraDate = extraDate;
    }

    public double getExtraMoney() {
        return extraMoney;
    }

    public void setExtraMoney(double extraMoney) {
        this.extraMoney = extraMoney;
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


    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public void setFkje(double fkje) {
        this.fkje = fkje;
    }

    public void setFkrq(String fkrq) {
        this.fkrq = fkrq;
    }

    public void setScrq(String scrq) {
        this.scrq = scrq;
    }

    public void setHkri(String hkri) {
        this.hkri = hkri;
    }

    public void setJiesFs(int jiesFs) {
        this.jiesFs = jiesFs;
    }

    public void setKouxiFs(int kouxiFs) {
        this.kouxiFs = kouxiFs;
    }

    public void setJixiFs(int jixiFs) {
        this.jixiFs = jixiFs;
    }

    public void setQixian(int qixian) {
        this.qixian = qixian;
    }

    public void setLilv(double lilv) {
        this.lilv = lilv;
    }

    public void setSpecialPro(int specialPro) {
        this.specialPro = specialPro;
    }

}
