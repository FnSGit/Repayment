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

    public String value;
    public String desc;

    BusiEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
