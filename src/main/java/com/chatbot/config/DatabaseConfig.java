package com.chatbot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final HikariDataSource dataSource;

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String host = dotenv.get("DB_HOST", "localhost");
        String port = dotenv.get("DB_PORT", "3306");
        String dbName = dotenv.get("DB_NAME", "chatbot_ai_platform");
        String user = dotenv.get("DB_USER", "root");
        String password = dotenv.get("DB_PASSWORD", "");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mariadb://%s:%s/%s", host, port, dbName));
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(60000);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(10000);

        dataSource = new HikariDataSource(config);
        log.info("HikariCP pool configured for {}/{}", host, dbName);
    }

    private DatabaseConfig() {
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
