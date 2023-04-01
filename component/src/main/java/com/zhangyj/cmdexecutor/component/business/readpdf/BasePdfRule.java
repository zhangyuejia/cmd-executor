package com.zhangyj.cmdexecutor.component.business.readpdf;

import com.google.common.collect.Lists;
import com.zhangyj.cmdexecutor.component.entity.bo.ExpenseBO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author zhanglj
 */
public interface BasePdfRule {

    /**
     * 是否匹配当前规则
     * @param contents 内容
     * @return 是否匹配
     */
    boolean match(List<String> contents);

    /**
     * 获取内容开始标志
     * @return 内容开始标志
     */
    String getStartSign();

    /**
     * 获取目标数据内容
     * @param contents 内容
     * @return 数据内容
     */
    default List<ExpenseBO> getTargetContents(List<String> contents){
        if(CollectionUtils.isEmpty(contents)){
            return null;
        }
        List<ExpenseBO> result = Lists.newArrayList();
        boolean start = false;
        for (String content : contents) {
            if(start) {
                if(Pattern.matches("[0-9].*", content)){
                    result.add(getTargetContent(content));
                }
            }else {
                if(content.startsWith(getStartSign())){
                    start = true;
                }
            }
        }
        return result;
    }

    /**
     * 获取目标数据内容
     * @param content 原始数据内容
     * @return 目标数据内容
     */
    ExpenseBO getTargetContent(String content);

    /**
     * 是否匹配当前规则
     * @param contents 内容
     * @param keyWord 关键字
     * @return 是否匹配
     */
    default boolean match(List<String> contents, String keyWord){
        for (String content : contents) {
            if (content.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }
}
