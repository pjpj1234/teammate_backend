package com.pujun.teammate_backend.common;

/**
 * 队伍状态枚举
 */
public enum TeamStatusEnum {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私密"),
    SECRET(2, "加密");

    private int value;

    private String description;

    /**
     * 根据值value 来查找是否有该状态
     * @param value
     * @return
     */
    public static TeamStatusEnum getTeamStatusByValue(Integer value){
        if(value == null){
            return null;
        }
        TeamStatusEnum[] statusEnums = TeamStatusEnum.values();
        for (TeamStatusEnum statusEnum : statusEnums) {
            if(statusEnum.getValue() == value){
                return statusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
