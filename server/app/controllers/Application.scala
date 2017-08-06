package controllers

import javax.inject._

import model.PlaceService
import shared.{Location, Place, SharedMessages}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

    def index = Action {
        Ok(views.html.index(SharedMessages.itWorks))
    }

    implicit val locationWrites: Writes[Location] = (
            (JsPath \ "lat").write[Double] and
            (JsPath \ "long").write[Double]
        ) (unlift(Location.unapply))

    implicit val placeWrites: Writes[Place] = (
            (JsPath \ "name").write[String] and
            (JsPath \ "location").write[Location]
        ) (unlift(Place.unapply))

    def listPlaces = Action {
        val json = Json.toJson(PlaceService.list)
        Ok(json)
    }
}
