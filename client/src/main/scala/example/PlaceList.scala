package example

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.TagOf
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html
import shared.Place
import japgolly.scalajs.react.vdom.html_<^._

case class PlaceListState(places: Seq[Place])

class PlaceList($: BackendScope[Unit, PlaceListState]) {

    def start = Callback {
        Ajax.get("/places").foreach { xhr =>
            val p = upickle.default.read[Seq[Place]](xhr.responseText)
            //JSON.parse(xhr.responseText).asInstanceOf[Seq[Place]]
            $.setState(PlaceListState(p)).runNow()
        }
    }

    def render(state: PlaceListState): TagOf[html.Element] = {
        def createItem(place: Place) = <.li(place.name)
        <.ul(state.places.map(createItem): _*)
    }
}