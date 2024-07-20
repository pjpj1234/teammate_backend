package com.pujun.teammate_backend.once;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

// 有个很重要的点 TableListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去

/**
 * 表格监听器
 */
public class TableListener implements ReadListener<UserTableInfo> {

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     * @param context
     */
    @Override
    public void invoke(UserTableInfo data, AnalysisContext context) {
        System.out.println(data);
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据已解析完成");
    }

}
