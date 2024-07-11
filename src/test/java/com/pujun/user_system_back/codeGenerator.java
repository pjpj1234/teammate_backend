package com.pujun.user_system_back;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.ibatis.type.JdbcType;

import java.nio.file.Paths;

public class codeGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/pujun", "root", "pj18384831533")
                .globalConfig(builder -> builder
                        .author("pujun")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd")
                )
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            // 兼容旧版本转换成Integer
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder -> builder
                        .parent("com.pujun.generator")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
