package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html

import scala.util.Random

case class SuggestState(value: String)

case class SuggestProps(values: Seq[String], defaultValue: Option[String] = None, id: Option[String] = None)

class Suggest($: BackendScope[SuggestProps, SuggestState]) {

    def getValue(): String = edit.value

    private var edit: html.Input = _

    def render(props: SuggestProps, state: SuggestState): VdomElement = {
        val randomId = Random.nextInt().toString
        <.div(
            <.input(
                ^.list := randomId,
                ^.defaultValue :=? props.defaultValue,
                ^.id :=? props.id
            ).ref(edit = _),
            <.datalist(^.id := randomId)(
                props.values.zipWithIndex.map { case (value, index) =>
                    <.option(
                        ^.value := value,
                        ^.key := index
                    )
                }: _*
            )
        )
    }
}

object Suggest {

    val suggest = ScalaComponent.builder[SuggestProps]("Suggest")
        .initialState(SuggestState(""))
        .renderBackend[Suggest]
        .build

}
