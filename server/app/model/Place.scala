package model

import shared.{Location, Place}

object PlaceService {

    var list: List[Place] = {
        List(
            Place(
                "Sandleford",
                Location(51.377797, -1.318965)
            ),
            Place(
                "Watership Down",
                Location(51.235685, -1.309197)
            )
        )
    }

    def save(place: Place): Unit = {
        list = list ::: List(place)
    }
}