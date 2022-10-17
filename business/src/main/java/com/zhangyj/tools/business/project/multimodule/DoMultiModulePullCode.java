package com.zhangyj.tools.business.project.multimodule;

import cn.hutool.core.util.CharsetUtil;
import com.zhangyj.tools.business.project.multimodule.config.MultiModulePullCodeConfig;
import com.zhangyj.tools.business.project.multimodule.pojo.ModuleProperties;
import com.zhangyj.tools.common.base.AbstractRunner;
import com.zhangyj.tools.common.utils.CommandUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyj
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(MultiModulePullCodeConfig.class)
public class DoMultiModulePullCode extends AbstractRunner<MultiModulePullCodeConfig> {

    private final static String CURRENT_BRANCH_FLAG = "* ";

    private final static String LOCAL_BRANCH_FLAG = "  ";

    private final static String BRANCH_SP = "/";

    private final static String REMOTE_BRANCH_FLAG = LOCAL_BRANCH_FLAG + "remotes" + BRANCH_SP;


    @Override
    protected void doRun() throws Exception {
        // 是否检查只有一个replaceId
        if(config.getEnableRefId().size() > 1){
            throw new RuntimeException("配置项[multi-module-pull.enableReplaceId]配置个数不能大于1个");
        }
        for (ModuleProperties moduleProperties : config.getModulesProperties()) {
            if(!config.getEnableRefId().contains(moduleProperties.getRefId())){
                continue;
            }
            log.info("启用配置ID:{}", moduleProperties.getRefId());
            for (ModuleProperties.ModulesParam modulesParam : moduleProperties.getModulesParams()) {
                String modulePath = getModulePath(moduleProperties, modulesParam);
                if(!new File(modulePath).exists()){
                    log.error("模块{}路径不存在，跳过:{}", modulesParam.getName(), modulePath);
                    continue;
                }
                fetchRepo(moduleProperties, modulesParam);
                // 切换到本地分支
                checkoutLocalBranch(moduleProperties, modulesParam);
                // 更新代码
                pullCode(moduleProperties, modulesParam);
            }
        }
    }

    private void fetchRepo(ModuleProperties moduleProperties, ModuleProperties.ModulesParam modulesParam) throws Exception {
        String command = "git fetch --all";
        logExecCmdOutput(moduleProperties, modulesParam, command);
    }

    private void pullCode(ModuleProperties moduleProperties, ModuleProperties.ModulesParam modulesParam) throws Exception {
        for (String remoteBranch : modulesParam.getRemoteBranch()) {
            String command = "git pull " + moduleProperties.getRemoteRepoName() + " " + remoteBranch;
            logExecCmdOutput(moduleProperties, modulesParam, command);
        }
    }

    private void checkoutLocalBranch(ModuleProperties moduleProperties, ModuleProperties.ModulesParam modulesParam) throws Exception {
        if (!moduleProperties.getEnableCheckoutLocalBranch()) {
            log.info("未开启本地分支切换开关，无需切换");
            return;
        }

        String modulePath = getModulePath(moduleProperties, modulesParam);
        List<String> commandOutput = CommandUtil.execCommand(CharsetUtil.CHARSET_GBK, "git branch -a", modulePath);
        String currentBranch = commandOutput.stream().filter(v -> v.startsWith(CURRENT_BRANCH_FLAG)).collect(Collectors.toList()).get(0).substring(CURRENT_BRANCH_FLAG.length());
        String localBranch = modulesParam.getLocalBranch().trim();
        if(currentBranch.equals(localBranch)){
            log.info("{}仓库处于本地分支{}，无需切换", modulesParam.getName(), modulesParam.getLocalBranch());
        } else if(commandOutput.contains(LOCAL_BRANCH_FLAG + localBranch)){
            log.info("{}仓库处于其他分支{}，需要切换为{}", modulesParam.getName(), currentBranch, modulesParam.getLocalBranch());
            String command = "git checkout " + localBranch;
            logExecCmdOutput(moduleProperties, modulesParam, command);
        }else if(commandOutput.contains(REMOTE_BRANCH_FLAG + moduleProperties.getRemoteRepoName() + BRANCH_SP + localBranch)){
            log.info("{}仓库不存在分支，从远程仓库{}检出分支{}", modulesParam.getName(), moduleProperties.getRemoteRepoName(), modulesParam.getLocalBranch());
            String command = "git checkout -b " + localBranch + " " + moduleProperties.getRemoteRepoName() + BRANCH_SP + modulesParam.getLocalBranch();
            logExecCmdOutput(moduleProperties, modulesParam, command);
        }else {
            throw new RuntimeException("远程仓库不存在分支" + modulesParam.getLocalBranch() + ",无法进行检出");
        }
    }

    private void logExecCmdOutput(ModuleProperties moduleProperties, ModuleProperties.ModulesParam modulesParam, String cmdArr) throws Exception {
        String modulePath = getModulePath(moduleProperties, modulesParam);
        CommandUtil.execCommand(CharsetUtil.CHARSET_GBK, cmdArr, modulePath, msg -> {
            log.info(msg);
            for (String errorWord : config.getErrorLogWords()) {
                if(msg.contains(errorWord)){
                    throw new RuntimeException("输出日志包含错误关键词" + errorWord + "，请检查是否正常");
                }
            }
        });
    }

    private String getModulePath(ModuleProperties moduleProperties, ModuleProperties.ModulesParam modulesParam){
        return moduleProperties.getProjectPath() + File.separator + modulesParam.getName();
    }
}