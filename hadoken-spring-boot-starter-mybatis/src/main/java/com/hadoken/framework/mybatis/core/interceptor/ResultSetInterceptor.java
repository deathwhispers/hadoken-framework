package com.hadoken.framework.mybatis.core.interceptor;

import com.hadoken.framework.mybatis.core.annotation.SensitiveData;
import com.hadoken.framework.mybatis.core.codec.Decrypt;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * 对 {@link ResultSetHandler#handleResultSets(Statement)} 方法进行拦截
 * 针对返回的数据进行解密
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:27
 */
@Slf4j
@Component
@Intercepts(
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
)
public class ResultSetInterceptor implements Interceptor {

    @Resource
    private Decrypt decrypt;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // 取出查询的结果
        Object resultObject = invocation.proceed();
        if (Objects.isNull(resultObject)) {
            return null;
        }

        // 基于selectList
        if (resultObject instanceof ArrayList) {
            @SuppressWarnings("unchecked")
            ArrayList<Objects> resultList = (ArrayList<Objects>) resultObject;
            if (!CollectionUtils.isEmpty(resultList) && needToDecrypt(resultList.get(0))) {
                for (Object result : resultList) {

                    // 逐一解密
                    decrypt.decrypt(result);
                }
            }
            // 基于selectOne
        } else {
            if (needToDecrypt(resultObject)) {
                decrypt.decrypt(resultObject);
            }
        }
        return resultObject;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private boolean needToDecrypt(Object object) {
        Class<?> objectClass = object.getClass();
        SensitiveData sensitiveData = AnnotationUtils.findAnnotation(objectClass, SensitiveData.class);
        return Objects.nonNull(sensitiveData);
    }
}
