package com.mei.hui.user.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetTokenBO {

    private String bodyEncry;

    private String aesKeyEncry;
}
