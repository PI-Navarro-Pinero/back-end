package com.pi.back.config.swagger;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class HttpStatusCodes {
    public static final String OK = "200";
    public static final String NO_CONTENT = "204";
    public static final String BAD_REQUEST = "400";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";
    public static final String PRECONDITION_FAILED = "412";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String SERVICE_UNAVAILABLE = "503";
}