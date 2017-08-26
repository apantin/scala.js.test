package example

import example.eve.MarketGroups
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Resolution, Router, RouterConfigDsl, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.scalajs.js

object ScalaJSExample extends js.JSApp {

    sealed trait Page
    case object Excel extends Page
    case object PlaceList extends Page
    case object Test extends Page
    case object ComponentTest extends Page
    case object EveMarketGroup extends Page

    private val placeListComponent = ScalaComponent.builder[Unit]("PlaceList")
        .initialState(PlaceListState(Nil))
        .renderBackend[PlaceList]
        .componentDidMount(_.backend.start)
        .build

    private val excelDemo = ScalaComponent.builder[Unit]("ExcelDemo")
        .initialState(ExcelDemo.default.state)
        .renderBackend[ExcelDemo]
        .build

    private val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
        import dsl._

        (emptyRule
            | staticRoute(root,                 Excel)  ~> render(excelDemo())
            | staticRoute("#PlaceList",         PlaceList) ~> render(placeListComponent())
            | staticRoute("#Test",              Test) ~> render(<.div("Test"))
            | staticRoute("#ComponentTest",     ComponentTest) ~> render(ComponentTestPage.component())
            | staticRoute("#EveMarketGroup",    EveMarketGroup) ~> render(MarketGroups.component())
        )
        .notFound(redirectToPage(Excel)(Redirect.Replace))
        .renderWith(layout)
    }

    private val baseUrl =
        if (dom.window.location.hostname == "localhost")
            BaseUrl.fromWindowOrigin_/
        else
            BaseUrl.fromWindowOrigin / "scalajs-react/"

    def main(): Unit = {
        println(BaseUrl.fromWindowOrigin)
        println(baseUrl)

        val router = Router(baseUrl, routerConfig)
        router().renderIntoDOM(dom.document.getElementById("playground"))
    }

    private def layout(c: RouterCtl[Page], r: Resolution[Page]) =
        <.div(
            navMenu(c),
            <.div(^.cls := "container", r.render()))

    def data_toggle = VdomAttr("data-toggle")
    def data_target = VdomAttr("data-target")

    private val navMenu = ScalaComponent.builder[RouterCtl[Page]]("Menu")
        .render_P { ctl =>
            def nav(name: String, target: Page) =
                <.li(
                    ^.cls := "active",   // navbar-brand
                    ctl setOnClick target,
                    <.a(name)
                    )

            <.nav(
                ^.cls := "navbar navbar-default navbar-static-top",
                ^.role := "navigation",
                <.div( ^.cls := "navbar-header",
                    <.button(^.`type` := "button",
                        ^.cls := "navbar-toggle",
                        data_toggle := "collapse",
                        data_target := ".navbar-collapse",
                        <.span(
                            ^.cls := "sr-only",
                            "Toggle navigation"
                        ),
                        <.span(^.cls := "icon-bar"),
                        <.span(^.cls := "icon-bar"),
                        <.span(^.cls := "icon-bar")
                    ),
                    <.a(^.cls := "navbar-brand",
                        ^.href := "/",
                        "Eve Manager"
                    )
                ),
                <.ul(
                    ^.cls := "nav navbar-top-links navbar-left",
                    nav("Excel",                Excel),
                    nav("PlaceList",            PlaceList),
                    nav("Test",                 Test),
                    nav("ComponentTest",        ComponentTest),
                    nav("EveMarketGroup",        EveMarketGroup)
                ),
                <.ul(
                    ^.cls := "nav navbar-top-links navbar-right",
                    <.li(
                        ^.cls := "dropdown",
                        <.a(
                            ^.cls := "dropdown-toggle",
                            <.i(
                                ^.cls := "fa fa-user fa-fw"
                            ),
                            <.i(
                                ^.cls := "fa fa-caret-down"
                            )
                        )
                    )
                )

            )

            /*            <.nav(
                ^.cls := "navbar navbar-default navbar-static-top",
                ^.role := "navigation",
                ^.style := "margin-bottom: 0",
                <.div(
                    ^.cls := "container-fluid",
                    <.div(
                        ^.cls := "collapse navbar-collapse",
                        <.ul(
                            ^.cls := "nav navbar-nav",
                            nav("Excel",                Excel),
                            nav("PlaceList",            PlaceList),
                            nav("Test",                 Test),
                            nav("ComponentTest",        ComponentTest),
                            nav("EveMarketGroup",        EveMarketGroup)
                        )
                    )
                )
            )*/

        }
        .configure(Reusability.shouldComponentUpdate)
        .build
}
