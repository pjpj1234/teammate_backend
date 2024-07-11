package com.pujun.user_system_back.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入用户到数据库
 */
public class ImportUser {

    public static void main(String[] args) {
        //Excel数据文件放在自己电脑上，能够找到的路径
        String fileName = "D:\\selfProject\\user_control_system\\user_system_back-master\\src\\main\\resources\\TableInfo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserTableInfo> userTableInfoList =
                EasyExcel.read(fileName).head(UserTableInfo.class).sheet().doReadSync();
        System.out.println("用户总数 = " + userTableInfoList.size());

        Map<String, List<UserTableInfo>> listMap = //alt + enter 生成局部变量信息
        //根据用户名相同的分组、并过滤掉用户名为空的 String为相同用户名 List是多个拥有相同用户名的用户信息
                userTableInfoList.stream()
                        .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUserName()))
                        .collect(Collectors.groupingBy(UserTableInfo::getUserName));

        //查看每个用户名对应用户列表
        //这里用iter 来生成循环 entrySet是map values是value keySet是key
        for (Map.Entry<String, List<UserTableInfo>> stringListEntry : listMap.entrySet()) {
            System.out.println("用户名为 = " + stringListEntry.getKey());
            System.out.println("1"); //断点查看
        }
        System.out.println("不重复的昵称总数 = " + listMap.keySet().size());
    }
}
