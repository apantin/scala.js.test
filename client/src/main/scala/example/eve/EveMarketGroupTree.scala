package example.eve

import example.ReactTreeView
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.TagOf
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html
import shared.model.nsi.{MarketGroup, MarketGroupTree, TreeItem}

import scala.concurrent.ExecutionContext.Implicits.global

object EveMarketGroupTree {

    object Style {
        def treeViewDemo = Seq(^.display := "flex").toTagMod

        def selectedContent = Seq(^.alignSelf := "center", ^.margin := "0 40px").toTagMod
    }

    // EXAMPLE:START
/*
    val data = TreeItem("root",
        TreeItem("dude1", TreeItem("dude1c")),
        TreeItem("dude2"),
        TreeItem("dude3"),
        TreeItem("dude4", TreeItem("dude4c", TreeItem("dude4cc"))))
    */

    class EmptyTreeItem extends TreeItem {
        def name: String = "---"
        def children: Seq[TreeItem] = Nil
    }

    case class RootTreeItem(name: String, children: Seq[TreeItem]) extends TreeItem

    case class State(content: String = "", root: TreeItem = new EmptyTreeItem)

    case class MarketGroup2(marketGroupID: Int,
                            parentGroupID: Int,
                            marketGroupName: String,
                            description: String,
                            iconID: Int,
                            hasTypes: Boolean)

    class Backend(t: BackendScope[Unit, State]) {

        def start = Callback {
            Ajax.get("/nsi/marketGroups?parent=-1").foreach { xhr =>
/*
                val testStr = "{\"marketGroupID\":2,\"parentGroupID\":null,\"marketGroupName\":\"Blueprints\",\"description\":\"Blueprints are data items used in industry for manufacturing, research and invention jobs\",\"iconID\":2703,\"hasTypes\":false}"

                val test = upickle.default.read[MarketGroup2](testStr)

                println("TEST " + test.toString)
*/

                val groups = upickle.default.read[Seq[MarketGroup]](xhr.responseText)
                //JSON.parse(xhr.responseText).asInstanceOf[Seq[Place]]

                def makeGroupTree(group: MarketGroup): MarketGroupTree = {
                    val children = groups.filter(_.parentGroupID.contains(group.marketGroupID)).map(makeGroupTree)
                    MarketGroupTree(group.marketGroupID, group.marketGroupName, group.description, children)
                }
                val result = groups.filter(_.parentGroupID.isEmpty).map(makeGroupTree)

                t.setState(State(root = RootTreeItem("root", result))).runNow()
            }
            /*            Ajax.get("/nsi/marketTree").foreach { xhr =>
                val groups = upickle.default.read[Seq[MarketGroupTree]](xhr.responseText)
                //JSON.parse(xhr.responseText).asInstanceOf[Seq[Place]]
                t.setState(State(root = RootTreeItem("root", groups))).runNow()
            }*/
        }

        def onItemSelect(item: String, parent: String, depth: Int): Callback = {
            val content =
                s"""Selected Item: $item <br>
                   |Its Parent : $parent <br>
                   |Its depth:  $depth <br>
          """.stripMargin
            Callback(dom.document.getElementById("treeviewcontent").innerHTML = content)
        }

        def render(state: State): TagOf[html.Element] = {
            <.div(
                <.div(Style.treeViewDemo)(
                    ReactTreeView(
                        root = state.root,
                        openByDefault = true,
                        onItemSelectCb = onItemSelect _,
                        showSearchBox = true
                    ),
                    <.strong(^.id := "treeviewcontent", Style.selectedContent)
                )
            )
        }
    }

    /*
        private val placeListComponent = ScalaComponent.builder[Unit]("PlaceList")
        .initialState(PlaceListState(Nil))
        .renderBackend[PlaceList]
        .componentDidMount(_.backend.start)
        .build
     */

    val component = ScalaComponent
        .builder[Unit]("EveMarketGroupTree")
        .initialState(State())
        .renderBackend[Backend]
        .componentDidMount(_.backend.start)
        .build

    // EXAMPLE:END

    def apply() = component()


}
