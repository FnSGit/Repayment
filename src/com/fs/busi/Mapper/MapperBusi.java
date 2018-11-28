/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/26 0026 下午 23:17
 * Description:mapper
 */
package com.fs.busi.Mapper;

import com.fs.busi.BusiProcess;
import com.fs.entity.TaskEntity;
import com.fs.group.Group;
import com.fs.util.math.MathUtil;

import java.math.BigDecimal;
import java.util.List;

public class MapperBusi extends BusiProcess {

    protected BigDecimal dataSize=MathUtil.getPow(1024,2).multiply(BigDecimal.valueOf(64));
    @Override
    public List<TaskEntity> getProcessData(Group param) {
        return null;
    }

    @Override
    public void process(List<TaskEntity> taskEntityList) {

    }
}
