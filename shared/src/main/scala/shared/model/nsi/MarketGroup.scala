package shared.model.nsi

case class MarketGroup(marketGroupID: Int,
                       parentGroupID: Option[Int] = None,
                       marketGroupName: Option[String] = None,
                       description: Option[String] = None,
                       iconID: Option[Int] = None,
                       hasTypes: Boolean)

trait TreeItem {
    def name: String
    def children: Seq[TreeItem]
}

case class MarketGropuItem(marketGroupID: Int, title: Option[String], description: Option[String])extends TreeItem {
    def name: String = title.getOrElse("---")
    def children: Seq[MarketGroupTree] = Nil
}

case class MarketGroupTree(marketGroupID: Int, title: Option[String], description: Option[String], childrenList: Seq[MarketGroupTree]) extends TreeItem {
    def name: String = title.getOrElse("---")
    def children: Seq[MarketGroupTree] = childrenList
}