package com.hadoken.framework.web.core.filter;

import com.hadoken.framework.web.config.XssProperties;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Xss 过滤器
 *
 * 对 Xss 不了解的胖友，可以看看 http://www.iocoder.cn/Fight/The-new-girl-asked-me-why-AJAX-requests-are-not-secure-I-did-not-answer/
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

    public XssFilter(XssProperties properties, PathMatcher pathMatcher) {
        this.properties = properties;
        this.pathMatcher = pathMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new XssRequestWrapper(request), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 如果关闭，则不过滤
        if (!properties.isEnable()) {
            return true;
        }

        // 如果匹配到无需过滤，则不过滤
        String uri = request.getRequestURI();
        return properties.getExcludeUrls().stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
    }

}
