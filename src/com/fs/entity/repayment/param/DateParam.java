package com.fs.entity.repayment.param;

public class DateParam extends PayParam{

   public String ksrq;
   public String jsrq;
   public String yhrq;

    private DateParam(String fkrq, String scrq, String hkri, int jiesFs,
                      int kouxiFs, int jixiFs, int qixian) {
        super();
        this.fkrq = fkrq;
        this.scrq = scrq;
        this.hkri = hkri;
        this.jiesFs = jiesFs;
        this.kouxiFs = kouxiFs;
        this.jixiFs = jixiFs;
        this.qixian=qixian;
    }

    public DateParam() {
    }
}
