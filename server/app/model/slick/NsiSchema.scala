package model.slick

import model.slick.nsi.{InvMarketGroups, RamActivities}
import slick.jdbc.PostgresProfile.api._

object NsiSchema {
    val ramActivities = TableQuery[RamActivities]
    val invMarketGroups = TableQuery[InvMarketGroups]
}
