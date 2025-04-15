package Core.Utils;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.LoggerContext;


public class LoggerUtil
{

    private static final Logger logger = LogManager.getLogger(LoggerUtil.class);

    public static void configInit()
    {
        // 获取LoggerContext
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("BuilderTest");
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
                addAttribute("level", Level.DEBUG));

        // 配置Console Appender
        AppenderComponentBuilder consoleBuilder = builder.
                newAppender("Console", "Console").
                addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        consoleBuilder.add(builder.newLayout("PatternLayout").
                addAttribute("pattern", "[%d{yyyy-MM-dd HH:mm:ss} %-5level] %msg%n"));
        builder.add(consoleBuilder);

        // 配置File Appender，
        builder.add(builder.newAppender("File", "File").
                addAttribute("fileName", "logs/${date:yyyy-MM-dd HH:mm:ss}.log").
                add(builder.newLayout("PatternLayout").
                        addAttribute("pattern", "[%d{yyyy-MM-dd HH:mm:ss} %-5level] %msg%n")).
                add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
                        addAttribute("level", Level.ERROR)));

        // 配置Root Logger引用上面定义的Appender
        builder.add(builder.newRootLogger(Level.DEBUG).add(builder.newAppenderRef("File")).add(builder.newAppenderRef("Console")));

        // 构建并设置Configuration到LoggerContext中，这将覆盖默认的配置（如果有的话）
        ctx.start(builder.build());
    }

    public static void info(String logContent)
    {
        logger.info(logContent);
    }
}

