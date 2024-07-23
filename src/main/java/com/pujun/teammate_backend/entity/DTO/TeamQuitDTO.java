package com.pujun.teammate_backend.entity.DTO;


import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitDTO implements Serializable {
    private static final long serialVersionUID = 33132383579786026L;

    /**
     *  id
     */
    private Long teamId;
}
