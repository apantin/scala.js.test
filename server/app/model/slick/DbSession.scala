package model.slick

import org.postgresql.ds.PGSimpleDataSource
import slick.jdbc.PostgresProfile.api._

object DbSession {

    lazy val db = {
        val dataSource = new PGSimpleDataSource()

        dataSource.setServerName("localhost")
        dataSource.setPortNumber(5433)
        dataSource.setDatabaseName("eve1")
        dataSource.setUser("postgres")
        dataSource.setPassword("123")

        Database.forDataSource(dataSource, Some(5))
    }

}