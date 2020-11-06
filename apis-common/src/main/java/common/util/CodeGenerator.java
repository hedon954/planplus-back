package common.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatisPlus 提供的代码生成器
 *
 * 参考官网：https://baomidou.com/guide/generator.html#使用教程
 *
 * @author Hedon Wang
 * @create 2020-10-15 22:31
 */
public class CodeGenerator {

    /**
     * 把要生成的数据库表明写在 TABLENAMES 里面
     */
    private static final String[] TABLENAMES = new String[]{"tbl_baidu_info"};


    /**
     * 执行方法
     */
    public static void main(String[] args){

        //代码生成器
        AutoGenerator mpg = new AutoGenerator();

        //全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/apis-common/src/main/java");
        gc.setAuthor("Jiahan Wang");  //作者名称
        gc.setOpen(false);
        gc.setSwagger2(true);         //实体属性加上 Swagger2 注解
        mpg.setGlobalConfig(gc);

        //数据库配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://182.61.131.18:3306/dida_manager?useUnicode=true&characterEncoding=utf8&useSSL=false");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("Hedon954!");
        mpg.setDataSource(dsc);

        //包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("");
        pc.setParent("common");   //父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        //自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        //自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ，如果 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/apis-common/src/main/resources/mapper/" +
                        tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        //模板配置
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        //策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(TABLENAMES);  //要生成哪些数据库表对应的文件
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("tbl_"); //表名前缀，加上的话在生成文件名时会去掉前缀
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

}
