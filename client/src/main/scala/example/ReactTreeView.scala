package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.model.nsi.TreeItem

import scalacss.ScalaCssReact._
import scala.scalajs.js

object ReactTreeView {

    val cssSettings = scalacss.devOrProdDefaults

    import cssSettings._

    private implicit final class UCB[R](private val uc: js.UndefOr[CallbackTo[R]])
        extends AnyVal {
        @inline def asCbo: CallbackOption[R] =
            CallbackOption.liftOption(uc.toOption.map(_.runNow()))
    }

    trait Style extends StyleSheet.Inline {

        import dsl._

        def reactTreeView: StyleA = style(margin(0 px))

        def treeGroup: StyleA = style(margin(0 px), padding(0 px, 0 px, 0 px, 14 px))

        def treeItem: StyleA = style(listStyleType := "none")

        def selectedTreeItemContent: StyleA =
            style(backgroundColor.rgb(27, 142, 176),
                color.white,
                fontWeight._400,
                padding(0 px, 7 px))

        def treeItemBefore: StyleA = style(
            display.inlineBlock,
            fontSize(11 px),
            color.gray,
            margin(3 px, 7 px, 0 px, 0 px),
            textAlign.center,
            width(11 px)
        )

        def treeItemHasChildrenClosed: StyleA = style(content := "▶")

        def treeItemHasChildrenOpened: StyleA = style(content := "▼")

    }

    type NodeC = ScalaComponent.MountedPure[NodeProps, NodeState, NodeBackend]

    case class State(filterText: String, filterMode: Boolean, selectedNode: js.UndefOr[NodeC])

    class Backend($ : BackendScope[Props, State]) {

        def onNodeSelect(P: Props)(selected: NodeC): Callback = {
            val removeSelection: Callback =
                $.state.flatMap(
                    _.selectedNode
                        .filterNot(_ == selected)
                        .fold(Callback.empty)(_.modState(_.copy(selected = false)))
                )

            val updateThis: Callback =
                $.modState(_.copy(selectedNode = selected, filterMode = false))

            val setSelection: Callback = selected.modState(_.copy(selected = true))


            val tell =
                selected.props.map { p =>
                    P.onItemSelect(p.root.name, p.parent, p.depth)
                }.runNow()

            removeSelection >> updateThis >> setSelection  >> tell
        }

        def onTextChange(text: String): Callback =
            $.modState(_.copy(filterText = text, filterMode = true))

        def render(P: Props, S: State) =
            <.div(P.style.reactTreeView,
                ReactSearchBox(onTextChange = onTextChange).when(P.showSearchBox),
                TreeNode.withKey("root")(
                    NodeProps(
                        root = P.root,
                        open = if (S.filterText.nonEmpty) true else P.open,
                        onNodeSelect = onNodeSelect(P),
                        filterText = S.filterText,
                        style = P.style,
                        filterMode = S.filterMode
                    ))
            )
    }

    case class NodeBackend($ : BackendScope[NodeProps, NodeState]) {

        def onItemSelect(P: NodeProps)(e: ReactEventFromHtml): Callback = {
            P.onNodeSelect($.asInstanceOf[NodeC]) >> e.preventDefaultCB >> e.stopPropagationCB
        }

        def childrenFromProps(P: NodeProps): CallbackTo[Option[Unit]] =
            $.modState(S => S.copy(children = if (S.children.isEmpty) P.root.children else Nil))
                .when(P.root.children.nonEmpty)

        def onTreeMenuToggle(P: NodeProps)(e: ReactEventFromHtml): Callback =
            childrenFromProps(P) >> e.preventDefaultCB >> e.stopPropagationCB

        def isFilterTextExist(filterText: String, data: TreeItem): Boolean = {
            def matches(item: TreeItem): Boolean =
                item.name.toLowerCase.contains(filterText.toLowerCase)

            def loop(data: Seq[TreeItem]): Boolean =
                data.view.exists(
                    item => if (item.children.isEmpty) matches(item) else loop(item.children)
                )

            matches(data) || loop(data.children)
        }

        def render(P: NodeProps, S: NodeState): VdomTag = {
            val depth = P.depth + 1
            val parent =
                if (P.parent.isEmpty) P.root.name
                else s"${P.parent}<-${P.root.name}"

            val treeMenuToggle: TagMod =
                if (S.children.nonEmpty)
                    <.span(
                        ^.onClick ==> onTreeMenuToggle(P),
                        ^.key := "arrow",
                        P.style.treeItemBefore,
                        "▼"
                    )
                else if (P.root.children.nonEmpty && S.children.isEmpty)
                    <.span(
                        ^.onClick ==> onTreeMenuToggle(P),
                        ^.key := "arrow",
                        P.style.treeItemBefore,
                        "▶"
                    )
                else ""

            <.li(
                ^.listStyleType := "none",
                treeMenuToggle,
                ^.key := "toggle",
                ^.cursor := "pointer",
                <.span(
                    P.style.selectedTreeItemContent.when(S.selected),
                    ^.onClick ==> onItemSelect(P),
                    P.root.name
                ),
                <.ul(P.style.treeGroup)(
                    S.children
                        .map(
                            child =>
                                TreeNode
                                    .withKey(s"$parent$depth${child.name}")
                                    .apply(
                                        P.copy(
                                            root = child,
                                            open = !P.filterText.trim.isEmpty,
                                            depth = depth,
                                            parent = parent,
                                            filterText = P.filterText
                                        ))
                                    .when(isFilterTextExist(P.filterText, child))): _*
                        )
            )
        }
    }

    case class NodeState(children: Seq[TreeItem] = Nil, selected: Boolean = false)

    case class NodeProps(root: TreeItem,
                         open: Boolean,
                         depth: Int = 0,
                         parent: String = "",
                         onNodeSelect: (NodeC) => Callback,
                         filterText: String,
                         style: Style,
                         filterMode: Boolean)

    lazy val TreeNode = ScalaComponent
        .builder[NodeProps]("ReactTreeNode")
        .initialStateFromProps(P => if (P.open) NodeState(P.root.children) else NodeState())
        .renderBackend[NodeBackend]
        .componentWillReceiveProps { c =>
            c.modState(_.copy(children = if (c.nextProps.open) c.nextProps.root.children else Nil))
                .when(c.nextProps.filterMode)
                .void
        }
        .build

    val component = ScalaComponent
        .builder[Props]("ReactTreeView")
        .initialState(State("", filterMode = false, js.undefined))
        .renderBackend[Backend]
        .build

    case class Props(root: TreeItem,
                     open: Boolean,
                     onItemSelect: (String, String, Int) => Callback,
                     showSearchBox: Boolean,
                     style: Style)

    def apply(root: TreeItem,
              openByDefault: Boolean = false,
              onItemSelectCb: (String, String, Int) => Callback = (_, _, _) => Callback.empty,
              showSearchBox: Boolean = false,
              style: Style = new Style {}) =
        component(Props(root, openByDefault, onItemSelectCb, showSearchBox, style))

}