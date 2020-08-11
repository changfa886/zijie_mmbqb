package com.xagent.dyin;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Scanner;

public class CodeGenerator
{
    public static String tb_scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");  // user.dir：用户的当前工作目录
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("jonty");// 作者
        gc.setOpen(false);
        gc.setFileOverride(true);
        gc.setActiveRecord(false);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setEntityName("%sEntity");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.POSTGRE_SQL);
        dsc.setDriverName("org.postgresql.Driver");
        dsc.setUrl("jdbc:postgresql://39.100.229.71:5432/zijiedb");
        dsc.setUsername("zijieadm");
        dsc.setPassword("PFwealTh0@0");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.xagent.dyin.db");
        pc.setController("controller");
        pc.setService("service");
        pc.setServiceImpl("service");
        pc.setMapper("mapper");
        pc.setEntity("entity");
        pc.setXml("xml");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        // strategy.setColumnNaming(NamingStrategy.underline_to_camel);

        //strategy.setInclude(new String[] { "wxvt_smscode" }); // 需要生成的表
        strategy.setInclude(tb_scanner("表名").split(","));

        strategy.setControllerMappingHyphenStyle(true);
        strategy.setRestControllerStyle(true);
        // strategy.setTablePrefix(new String[] { "sys_" });// 此处可以修改为您的表前缀
        // strategy.setTablePrefix(pc.getModuleName() + "_");
        strategy.setSuperServiceClass(null);
        strategy.setSuperServiceImplClass(null);
        strategy.setSuperMapperClass(null);
        strategy.setEntityLombokModel(true);

        // mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.setStrategy(strategy);

        // 执行生成
        mpg.execute();
    }
}
