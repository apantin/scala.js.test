package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import shared.model.nsi.TreeItem

object ReactTreeViewDemo {

    object Style {
        def treeViewDemo = Seq(^.display := "flex").toTagMod

        def selectedContent = Seq(^.alignSelf := "center", ^.margin := "0 40px").toTagMod
    }

    // EXAMPLE:START

    case class SimpleTreeItem(name:String, children: Seq[SimpleTreeItem]) extends TreeItem

    val data = SimpleTreeItem("root", Seq (
        SimpleTreeItem("dude1", Seq(SimpleTreeItem("dude1c", Nil))),
        SimpleTreeItem("dude2", Nil),
        SimpleTreeItem("dude3", Nil),
        SimpleTreeItem("dude4", Seq(SimpleTreeItem("dude4c", Seq(SimpleTreeItem("dude4cc", Nil))))))
    )

    case class State(content: String = "")

    class Backend(t: BackendScope[_, _]) {

        def onItemSelect(item: String, parent: String, depth: Int): Callback = {
            val content =
                s"""Selected Item: $item <br>
                   |Its Parent : $parent <br>
                   |Its depth:  $depth <br>
          """.stripMargin
            Callback(dom.document.getElementById("treeviewcontent").innerHTML = content)
        }

        def render = {
            <.div(
                <.div(Style.treeViewDemo)(
                    ReactTreeView(
                        root = data,
                        openByDefault = true,
                        onItemSelectCb = onItemSelect _,
                        showSearchBox = true
                    ),
                    <.strong(^.id := "treeviewcontent", Style.selectedContent)
                )
            )
        }
    }

    val component = ScalaComponent
        .builder[Unit]("ReactTreeViewDemo")
        .initialState(State())
        .renderBackend[Backend]
        .build

    // EXAMPLE:END

    def apply() = component()

}