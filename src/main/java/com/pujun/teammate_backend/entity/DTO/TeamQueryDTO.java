package com.pujun.teammate_backend.entity.DTO;

import com.pujun.teammate_backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true) //传值时，比较赋值父类的页码、大小
@Data
public class TeamQueryDTO extends PageRequest { //继承PageRequest得到 页面字段
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     *  id列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 创始人id
     */
    private Long userId;

    /**
     * 状态 0 -公开、1 - 私有、2 - 加密
     */
    private Integer status;
}
