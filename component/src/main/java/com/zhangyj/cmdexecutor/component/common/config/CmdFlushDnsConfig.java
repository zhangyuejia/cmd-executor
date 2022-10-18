package com.zhangyj.cmdexecutor.component.common.config;

import com.zhangyj.cmdexecutor.component.entity.bo.HostsInfoBO;
import com.zhangyj.cmdexecutor.core.common.config.AbstractCmdConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhagnyj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class CmdFlushDnsConfig extends AbstractCmdConfig {

    /**
     * 网络hosts文件
     */
    private List<HostsInfoBO> hosts;

    /**
     * 本地hosts文件地址
     */
    private String hostsPath;

    @Override
    public String getDesc() {
        return "刷新dns配置功能";
    }
}
