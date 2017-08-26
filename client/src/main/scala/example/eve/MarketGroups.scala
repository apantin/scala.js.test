package example.eve

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object MarketGroups {

    val component = ScalaComponent.static("Eve market")(
        <.div(
            EveMarketGroupTree.apply()
        )
    )

}
