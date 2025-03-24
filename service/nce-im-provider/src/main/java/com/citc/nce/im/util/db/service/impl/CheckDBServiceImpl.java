package com.citc.nce.im.util.db.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.im.util.db.mapper.TableInfoMapper;
import com.citc.nce.im.util.db.service.CheckDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;


@Service
@Slf4j
public class CheckDBServiceImpl implements CheckDBService {

    @Resource
    @Lazy
    private TableInfoMapper tableInfoMapper;


    @Override
    public void checkBDForService(String local) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory factory = new CachingMetadataReaderFactory();
        org.springframework.core.io.Resource[] resources = resolver.getResources(local);
        //log.info("find resources {}", Arrays.toString(resources));
        ClassLoader loader = ClassUtils.getDefaultClassLoader();
        if (Objects.isNull(loader)) {
            log.error("class_load not found");
            System.exit(0);
        }

        List<String> tableList = new ArrayList<>();
        List<String> filedNameList = new ArrayList<>();
        for (org.springframework.core.io.Resource resource : resources) {
            MetadataReader reader = factory.getMetadataReader(resource);
            String className = reader.getClassMetadata().getClassName();
            try {
                Class<?> clazz = loader.loadClass(className);
                TableName dbTable = clazz.getAnnotation(TableName.class);
                if (Objects.nonNull(dbTable)) {
                    //获取表名
                    String tableName = dbTable.value();
                    if (StringUtils.hasLength(tableName)) {
                        //检查表 map<数据库字段，类型>
                        Map<String, String> DBTableInfo = getFiledForDBMap(tableName);
                        if (Objects.isNull(DBTableInfo)) {
                            //表不存在
                            tableList.add(tableName);
                            continue;
                        }

                        //java 类中的字段
                        Map<String, String> fieldMap = getClassFieldMap(clazz);
                        //对比数据
                        for (String classFiledName : fieldMap.keySet()) {
                            String filedName = classFiledName.contains("_") ? classFiledName : StrUtil.toUnderlineCase(classFiledName);
                            if (!DBTableInfo.containsKey(filedName)) {
                                filedNameList.add(clazz.getName() + "--" + tableName + "--缺少字段：【" + classFiledName + "---->" + filedName + "】");
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                log.warn("class load fail ：{}。。。。。。。。", className);
            }
        }

        if (!tableList.isEmpty()) {
            log.error("=============== table not exist {}", tableList.size());
            for (String item : tableList) {
                System.out.println(item);
            }
        }

        if (!filedNameList.isEmpty()) {
            log.error("=============== filed not exist {}", filedNameList.size());
            for (String item : filedNameList) {
                System.out.println(item);
            }
        }

        //通过springboot上下文关系统
        if (!tableList.isEmpty() || !filedNameList.isEmpty()) {
            System.exit(0);
        }

    }

    private String getClassType(String type) {
        if (type.startsWith("bigint")) {
            return "Long";
        } else if (type.startsWith("varchar")) {
            return "String";
        } else if (type.startsWith("tinyint") || type.startsWith("int")) {
            return "Integer";
        } else if (type.startsWith("datetime")) {
            return "Date";
        } else {
            return "String";
        }
    }

    private static Map<String, String> getClassFieldMap(Class<?> clazz) {
        Map<String, String> fieldMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        // 输出每个字段的信息
        for (Field field : fields) {
            TableField annotation = field.getAnnotation(TableField.class);
            if (Objects.isNull(annotation) && !"serialVersionUID".equals(field.getName())) {
                fieldMap.put(field.getName(), field.getType().getName());
            }
            if (annotation != null && annotation.exist()) {
                String annotationValue = StringUtils.hasLength(annotation.value()) ? annotation.value() : field.getName();
                if (annotationValue.contains("`")) annotationValue = annotationValue.replaceAll("`", "");
                fieldMap.put(annotationValue, field.getType().getName());
            }
        }
        return fieldMap;
    }


    private Map<String, String> getFiledForDBMap(String tableForDBName) {
        //获取表字段和类型
        try {
            Map<String, String> map = new HashMap<>();
            List<Map<String, Object>> columns = getTableColumns(tableForDBName);
            if (!columns.isEmpty()) {
                for (Map<String, Object> itemMap : columns) {
                    String columnName = itemMap.get("Field") + "";
                    String type = itemMap.get("Type") + "";
                    map.put(columnName, type);
                }
            }
            return map;
        } catch (Exception e) {
            return null;
        }
    }


    private List<Map<String, Object>> getTableColumns(String tableName) {
        return tableInfoMapper.getTableColumns(tableName);
    }
}
