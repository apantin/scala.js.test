package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.concurrent.ExecutionContext.Implicits.global

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
            <.div( ^.cls := "row",
                Suggest.suggest(SuggestProps(stringList))

            )
    )

}
