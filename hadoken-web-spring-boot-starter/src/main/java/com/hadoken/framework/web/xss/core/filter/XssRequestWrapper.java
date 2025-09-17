package com.hadoken.framework.web.xss.core.filter;

import cn.hutool.core.io.IoUtil;
import com.hadoken.framework.web.xss.core.cleaner.XssCleaner;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Xss 请求 Wrapper
 *
 * @author yanggj
 */
@Slf4j
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final XssCleaner xssCleaner;
    // 缓存过滤后的JSON体
    private byte[] cachedJsonBody;
    // 缓存过滤后的参数
    private Map<String, String[]> cachedParameterMap;

    public XssRequestWrapper(HttpServletRequest request, XssCleaner xssCleaner) {
        super(request);
        this.xssCleaner = xssCleaner;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 非JSON请求，直接返回原始流
        if (!isJsonRequest()) {
            return super.getInputStream();
        }

        // 如果已缓存，直接返回缓存流
        if (cachedJsonBody != null) {
            return createServletInputStream(cachedJsonBody);
        }

        // 读取并过滤JSON体
        String originalJson = IoUtil.readUtf8(super.getInputStream());
        if (originalJson == null || originalJson.trim().isEmpty()) {
            cachedJsonBody = new byte[0];
            return createServletInputStream(cachedJsonBody);
        }

//        String cleanedJson = xssCleaner.clean(originalJson);
//        cachedJsonBody = cleanedJson.getBytes(StandardCharsets.UTF_8);

        cachedJsonBody = originalJson.getBytes(StandardCharsets.UTF_8);
        return createServletInputStream(cachedJsonBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (cachedParameterMap != null) {
            return cachedParameterMap;
        }
        Map<String, String[]> originalMap = super.getParameterMap();
        cachedParameterMap = filterParameterMap(originalMap);
        return cachedParameterMap;
    }

    private Map<String, String[]> filterParameterMap(Map<String, String[]> originalMap) {
        Map<String, String[]> filteredMap = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
            String key = safeClean(entry.getKey());
            String[] values = filterStringArray(entry.getValue());
            filteredMap.put(key, values);
        }
        return filteredMap;
    }

    private String[] filterStringArray(String[] originalArray) {
        if (originalArray == null || originalArray.length == 0) {
            return originalArray;
        }
        String[] filteredArray = new String[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            filteredArray[i] = safeClean(originalArray[i]);
        }
        return filteredArray;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        return Arrays.stream(values).map(this::safeClean).toArray(String[]::new);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return safeClean(value);
    }

    // ============================ 工具方法 ============================
    private boolean isJsonRequest() {
        String contentType = getHeader(HttpHeaders.CONTENT_TYPE);
        return contentType != null &&
                (contentType.contains(MediaType.APPLICATION_JSON_VALUE) ||
                        contentType.contains("application/json"));
    }

    private String safeClean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        try {
            return xssCleaner.clean(value);
        } catch (Exception e) {
            log.error("[XSS过滤异常] 内容：{}，异常：{}", value.substring(0, Math.min(100, value.length())), e.getMessage());
            return value;
        }
    }

    private ServletInputStream createServletInputStream(byte[] content) {
        return new CachedServletInputStream(content);
    }

    private static class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream sourceStream;

        public CachedServletInputStream(byte[] content) {
            this.sourceStream = new ByteArrayInputStream(content);
        }

        @Override
        public int read() throws IOException {
            return sourceStream.read();
        }

        @Override
        public boolean isFinished() {
            return sourceStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("CachedServletInputStream does not support asynchronous IO");
        }
    }

}
