package com.mei.hui.config.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TokenBO {

    private Long userId;

    private Long currencyId;

    private String  platform;

    private boolean isVisitor;

    private List<Long> roleIds;

}
