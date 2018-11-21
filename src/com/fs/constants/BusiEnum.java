/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/21 0021 下午 23:50
 * Description:业务枚举
 */
package com.fs.constants;

public enum BusiEnum {
    NO("0","否"),
    YES("1","是")
    ;

    private String value;
    private String desc;

    BusiEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
