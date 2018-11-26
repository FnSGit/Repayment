/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/26 0026 下午 23:20
 * Description:
 */
package com.fs.group;

import java.io.File;
import java.util.List;

public class MapperGroup extends Group {

    private String dir;
    public MapperGroup(String inputDir) {
        dir=inputDir;
    }

    @Override
    public List<Group> groupParamBuild() {
        long byteSiez;
        StringBuilder stringBuilder = new StringBuilder();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            return null;
return null;
    }
}
