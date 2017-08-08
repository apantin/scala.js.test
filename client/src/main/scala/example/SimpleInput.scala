package example

import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html

import scala.util.Random

trait SimpleInputType

object TextInputType extends SimpleInputType
object MemoInputType extends SimpleInputType
object NumberInputType extends SimpleInputType

case class SimpleInputState(value: String)

case class SimpleInputProps(id: String, inputType: SimpleInputType, defaultValue: Option[String] = None)

class SimpleInput($: BackendScope[SimpleInputProps, SimpleInputState]) {

    def getValue(): String = value()

    private var value: () => String = () => ""

    def render(props: SimpleInputProps, state: SimpleInputState): VdomElement = {
        props.inputType match {
            case TextInputType =>
                <.input(
                    ^.id := props.id,
                    ^.`type` := "text",
                    ^.defaultValue :=? props.defaultValue
                ).ref(input => value = () => input.value)
            case NumberInputType =>
                <.input(
                    ^.id := props.id,
                    ^.`type` := "number",
                    ^.defaultValue :=? props.defaultValue
                ).ref(input => value = () => input.value)
            case MemoInputType =>
                <.textarea(
                    ^.id := props.id,
                    ^.defaultValue :=? props.defaultValue
                ).ref(input => value = () => input.value)
        }
    }
}

object SimpleInput {

    val simpleInput = ScalaComponent.builder[SimpleInputProps]("SimpleInput")
        .initialState(SimpleInputState(""))
        .renderBackend[SimpleInput]
        .build

}