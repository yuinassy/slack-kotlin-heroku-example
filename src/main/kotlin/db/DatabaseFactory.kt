package db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private var hikariDataSource: HikariDataSource? = null

    fun init(
//        dbName: String,
//        username: String,
//        password: String,
//        cloudSqlInstance: String,
        jdbcUrl: String,
//        maximumPoolSize: Int,
//        minimumIdle: Int
    ) {
        val ds = hikariDataSource(jdbcUrl)
        hikariDataSource = ds
        Database.connect(ds)
    }

    private fun hikariDataSource(
//        dbName: String,
//        username: String,
//        password: String,
//        cloudSqlInstance: String,
        jdbcUrl: String,
//        maximumPoolSize: Int,
//        minimumIdle: Int
    ): HikariDataSource {
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
//            jdbcUrl = "jdbc:postgresql:///$dbName"
//            this.username = username
//            this.password = password // IAMで接続可能性が制御できているので、パスワードは特にシークレット扱いはしない。

            // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
            // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
//            addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory")
//            addDataSourceProperty("cloudSqlInstance", cloudSqlInstance)

//            this.maximumPoolSize = maximumPoolSize
//            this.minimumIdle = minimumIdle
            connectionTimeout = 10000 // 10 seconds
            idleTimeout = 600000 // 10 minutes
            maxLifetime = 1800000 // 30 minutes

            validate()
        }

        return HikariDataSource(config)
    }
}
