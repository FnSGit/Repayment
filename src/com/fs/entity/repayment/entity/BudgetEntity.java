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


    public double getLixi() {
        return lixi;
    }

    public void setLixi(double lixi) {
        this.lixi = lixi;
    }

    public double getBenj() {
        return benj;
    }

    public void setBenj(double benj) {
        this.benj = benj;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getFwfee() {
        return fwfee;
    }

    public void setFwfee(double fwfee) {
        this.fwfee = fwfee;
    }

    public double getQdffee() {
        return qdffee;
    }

    public void setQdffee(double qdffee) {
        this.qdffee = qdffee;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}
