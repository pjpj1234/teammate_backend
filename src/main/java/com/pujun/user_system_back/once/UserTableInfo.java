package com.pujun.user_system_back.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户表格信息对象
 */
@Data
public class UserTableInfo {

    /**
     * 强制读取第三个 这里不建议 index 和 name 同时用，要么一个对象只用index，要么一个对象只用name去匹配
     */
    @ExcelProperty("用户编号")
    private String validCode;

    /**
     * 用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
     */
    @ExcelProperty("用户名")
    private String userName;
}