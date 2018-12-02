package com.fs.constant;

public enum FeeEnum {
        fwfFee(0,"服务费"),
        qdfFee(1,"渠道返费"),

        ;



        int index;
        String desc;
         FeeEnum(int index, String desc) {
            this.index = index;
            this.desc = desc;
        }

}
