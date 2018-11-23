package com.fs.entity.repayment.param;

public class DateParam {

   public String fkrq;
   public String scrq;
   public String hkri;
   public String ksrq;
   public String jsrq;
   public String yhrq;
   public int jiesFs;
   public int kouxiFs;
   public int jixiFs;
   public int qixian;
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

}
