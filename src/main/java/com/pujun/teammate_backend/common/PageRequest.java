package com.pujun.teammate_backend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -2898508050930823196L;
    /**
     * 页面大小
     */
    private int pageSize;

    /**
     * 第几页
     */
    private int pageNum;
}
