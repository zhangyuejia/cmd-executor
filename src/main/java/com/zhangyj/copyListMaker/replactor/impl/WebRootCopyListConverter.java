package com.zhangyj.copyListMaker.replactor.impl;

import com.zhangyj.copyListMaker.config.Config;
import com.zhangyj.constant.Const;
import com.zhangyj.copyListMaker.replactor.BaseCopyListConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * WebRoot路径替换器
 * @author ZHANG
 */
@Component
@ConditionalOnBean(Config.class)
public class WebRootCopyListConverter extends BaseCopyListConverter {

    public WebRootCopyListConverter(Config config) {
        super(config);
    }

    @Override
    protected Set<String> toCopyListRelativePath(String relativePath) {
        return Collections.singleton(relativePath.substring(Const.WEB_ROOT.length()));
    }
}