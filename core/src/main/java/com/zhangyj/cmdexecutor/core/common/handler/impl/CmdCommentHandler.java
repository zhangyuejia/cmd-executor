package com.zhangyj.cmdexecutor.core.common.handler.impl;

import com.zhangyj.cmdexecutor.core.common.config.CmdExecConfig;
import com.zhangyj.cmdexecutor.core.common.enums.CmdTypeEnum;
import com.zhangyj.cmdexecutor.core.common.handler.CmdHandler;
import org.springframework.stereotype.Component;

/**
 * 注释命令handler
 * @author zhangyj
 */
@Component
public class CmdCommentHandler implements CmdHandler {

    @Override
    public void handle(CmdExecConfig config, String cmdLine) {

    }

    @Override
    public CmdTypeEnum getCmdType() {
        return CmdTypeEnum.COMMENT;
    }
}
