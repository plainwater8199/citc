package com.citc.nce.common.facadeserver.annotations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationScanner {

    public static List<String> getMethodsWithAnnotation(Class<? extends java.lang.annotation.Annotation> annotation) {
        List<String> methods = new ArrayList<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory factory = new CachingMetadataReaderFactory();
        try {
            Resource[] resources = resolver.getResources("classpath*:com/citc/nce/**/*Controller.class");
            ClassLoader loader = ClassUtils.getDefaultClassLoader();
            if (Objects.isNull(loader)) {
                log.error("class_load not found");
                throw new RuntimeException("class_load not found");
            }
            for (org.springframework.core.io.Resource resource : resources) {
                MetadataReader reader = factory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                try {
                    Class<?> clazz = loader.loadClass(className);
                    if (Objects.nonNull(clazz.getAnnotation(annotation))) {
                        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                        if (Objects.nonNull(requestMapping)) {
                            methods.addAll(Arrays.stream(requestMapping.value()).map(s -> s + "/**").collect(Collectors.toList()));
                        }
                    }
                    Method[] declaredMethods = clazz.getDeclaredMethods();
                    RequestMapping clazzAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
                    String[] clazzPath = null;
                    if (clazzAnnotation != null) {
                        clazzPath = (clazzAnnotation.path());
                    }
                    for (Method method : declaredMethods) {
                        if (!method.isAnnotationPresent(annotation))
                            continue;
                        RequestMapping mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                        if (mergedAnnotation != null) {
                            String[] path = mergedAnnotation.path();
                            StringBuilder sb = new StringBuilder();
                            if (clazzPath != null) {
                                for (String cp : clazzPath) {
                                    if (StringUtils.hasLength(cp) && !cp.startsWith("/")) {
                                        cp = "/" + cp;
                                    }
                                    sb.append(cp);
                                }
                            }
                            for (String s : path) {
                                if (StringUtils.hasLength(s) && !s.startsWith("/")) {
                                    s = "/" + s;
                                }
                                sb.append(s);
                            }
                            methods.add(sb.toString());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("class load fail ：{}。。。。。。。。", className);
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        List<String> urls = new ArrayList<>();
        for (String method : methods) {
            StringTokenizer stringTokenizer = new StringTokenizer(method, "/");
            StringBuilder sb = new StringBuilder();
            while (stringTokenizer.hasMoreElements()) {
                sb.append("/");
                sb.append(stringTokenizer.nextElement());
            }
            urls.add(sb.toString());
        }
        return urls.stream().distinct().collect(Collectors.toList());
    }
}
