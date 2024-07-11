package com.pujun.user_system_back.once;

import com.alibaba.excel.EasyExcel;
import java.util.List;

/**
 * 导入Excel表格类
 */
public class ImportExcel {
    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link UserTableInfo}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link TableListener}
     * <p>
     * 3. 直接读即可
     */

    public static void main(String[] args) {
        String fileName = "D:\\selfProject\\user_control_system\\user_system_back-master\\src\\main\\resources\\TableInfo.xlsx";
//        simpleRead(fileName);
        synchronousRead(fileName);
    }

    // 写法1：JDK8+ ,不用额外写一个UserTableInfoListener
    // since: 3.0.0-beta1
    public static void simpleRead(String fileName) {
        EasyExcel.read(fileName, UserTableInfo.class, new TableListener()).sheet().doRead();
    }

    // 写法2：
    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     * 不需要绑定监听器 直接获取全部数据
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserTableInfo> userList = EasyExcel.read(fileName).head(UserTableInfo.class).sheet().doReadSync();
        for (UserTableInfo data : userList) {
            System.out.println(data);
        }
        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
//        List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
//        for (Map<Integer, String> data : listMap) {
//            // 返回每条数据的键值对 表示所在的列 和所在列的值
//            LOGGER.info("读取到数据:{}", JSON.toJSONString(data));
//        }
    }
}
