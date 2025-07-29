package com.alvarobf0.similarproducts.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstant {
    public static final String OK_CODE = "200";
    public static final String OK_REASON = "OK";

    public static final String NOT_FOUND_CODE = "404";
    public static final String NOT_FOUND_REASON = "Not Found";

    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String INTERNAL_SERVER_ERROR_REASON = "Internal Server Error";
}
