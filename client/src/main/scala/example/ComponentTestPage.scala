package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object ComponentTestPage {

    private val stringList = Seq (
        "123",
        "132",
        "6456",
        "64544",
        "11122",
        "123123"
    )

    val component = ScalaComponent.static("Home")(
        <.div(
            <.div( ^.cls := "row",
                Suggest.suggest(SuggestProps(stringList))
            ),
            <.div( ^.cls := "row",
                SimpleInput.simpleInput(SimpleInputProps(id = "id_1", inputType = TextInputType))
            ),
            <.div( ^.cls := "row",
                SimpleInput.simpleInput(SimpleInputProps(id = "id_2", inputType = MemoInputType))
            ),
            <.div( ^.cls := "row",
                SimpleInput.simpleInput(SimpleInputProps(id = "id_3", inputType = NumberInputType))
            )
        )
    )




}
