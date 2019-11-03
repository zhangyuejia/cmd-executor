package com.zhangyj.replactor.impl;

import com.zhangyj.config.Config;
import com.zhangyj.config.CopyListConfig;
import com.zhangyj.config.SvnConfig;
import com.zhangyj.replactor.BaseCopyListConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * 资源文件路径替换器
 * @author ZHANG
 */
@Component
public class ResourceCopyListConverter extends BaseCopyListConverter {

    public ResourceCopyListConverter(Config config) {
        super(config);
    }

    @Override
    public Set<String> toCopyListLines(String relativePath) {
        String copyListLine = config.getCopyList().getPrefix() + "\\WEB-INF\\classes" + relativePath.substring(relativePath.indexOf("/"));
        return Collections.singleton(copyListLine);
    }
}