package model.slick.nsi

import slick.jdbc.PostgresProfile.api._

class RamActivities(tag: Tag) extends Table[(Int, String, Option[String], String, Boolean)](tag, "ramActivities") {
    def activityID: Rep[Int] = column[Int]("activityID", O.PrimaryKey) // This is the primary key column
    def activityName: Rep[String] = column[String]("activityName")
    def iconNo: Rep[Option[String]] = column[Option[String]]("iconNo")
    def description: Rep[String] = column[String]("description")
    def published: Rep[Boolean] = column[Boolean]("published")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (activityID, activityName, iconNo, description, published)
}
