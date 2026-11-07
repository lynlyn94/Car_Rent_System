package com.carrent.util;

import com.carrent.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 在拦截器中直接写出 JSON 响应的工具类
 */
public class ResponseUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeJson(HttpServletResponse response, int status, Result<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(MAPPER.writeValueAsString(result));
    }
}
