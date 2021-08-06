package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel
@Accessors(chain = true)
public class FindAllMinerVO {
    private Long userId;

    private String userName;

    private List<String> minerIds;

}
