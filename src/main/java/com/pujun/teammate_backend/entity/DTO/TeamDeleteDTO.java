package com.pujun.teammate_backend.entity.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除队伍DTO
 */
@Data
public class TeamDeleteDTO implements Serializable {
    private static final long serialVersionUID = 6794709211816259327L;
    /**
     * 队伍 id
     */
    private long id;
}
