package model.slick.nsi

import slick.jdbc.PostgresProfile.api._

class InvMarketGroups(tag: Tag) extends Table[(Int, Option[Int], Option[String], Option[String], Option[Int], Boolean)](tag, "invMarketGroups") {
    def marketGroupID: Rep[Int] = column[Int]("marketGroupID", O.PrimaryKey)
    def parentGroupID: Rep[Option[Int]] = column[Option[Int]]("parentGroupID")
    def marketGroupName: Rep[Option[String]] = column[Option[String]]("marketGroupName")
    def description: Rep[Option[String]] = column[Option[String]]("description")
    def iconID: Rep[Option[Int]] = column[Option[Int]]("iconID")
    def hasTypes: Rep[Boolean] = column[Boolean]("hasTypes")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (marketGroupID, parentGroupID, marketGroupName, description, iconID, hasTypes)
}