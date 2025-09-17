package com.hadoken.framework.web.xss.core.filter;

import com.hadoken.framework.web.xss.config.XssProperties;
import com.hadoken.framework.web.xss.core.cleaner.XssCleaner;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Xss 过滤器
 *
 * @author yanggj
 */
@SuppressWarnings("NullableProblems")
public class XssFilter extends OncePerRequestFilter {

    /**
     * 属性
     */
    private final XssProperties properties;
    /**
     * 路径匹配器
     */
    private final PathMatcher pathMatcher;

    private final XssCleaner xssCleaner;

    public XssFilter(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        this.properties = properties;
        this.pathMatcher = pathMatcher;
        this.xssCleaner = xssCleaner;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 如果关闭，则不过滤
        if (!properties.isEnable()) {
            return true;
        }
        List<String> excludePatterns = properties.getExcludePatterns();
        if (excludePatterns.isEmpty()) {
            return false;
        }
        // 如果匹配到无需过滤，则不过滤
        String requestURI = request.getRequestURI();
        return excludePatterns.stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, requestURI));
    }

}
