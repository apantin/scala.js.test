package controllers.nsi

import javax.inject._

import model.slick.{DbSession, NsiSchema}
import play.api.Logger
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Writes}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import shared.model.nsi.{MarketGroup, MarketGroupTree}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class NsiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

    implicit val marketGroupTreeWrites: Writes[MarketGroupTree] = (group: MarketGroupTree) => Json.obj(
        "marketGroupID" -> group.marketGroupID,
        "title" -> group.title,
        "description" -> group.description,
        "childrenList" -> group.childrenList
    )

    implicit val marketGroupWrites: Writes[MarketGroup] = (
            (JsPath \ "marketGroupID").write[Int] and
            (JsPath \ "parentGroupID").write[Option[Int]] and
            (JsPath \ "marketGroupName").write[Option[String]] and
            (JsPath \ "description").write[Option[String]] and
            (JsPath \ "iconID").write[Option[Int]] and
            (JsPath \ "hasTypes").write[Boolean]
        ) (unlift(MarketGroup.unapply))

    def test: Action[AnyContent] = Action.async {
        val t1 = NsiSchema.ramActivities.result
        DbSession.db.run(t1).map { result =>
            val resString = result.map { case (activityID, activityName, iconNo, description, published) =>
                s"$activityID $activityName $iconNo $description $published"
            }.mkString(" <br/>")
            Ok(resString)
        }
    }

    def marketGroups(parent: Option[Int]): Action[AnyContent] = Action.async {
        val query =
            if(parent.contains(-1)) {
                NsiSchema.invMarketGroups
            } else {
                for {
                    mg <- NsiSchema.invMarketGroups if mg.parentGroupID === parent
                } yield (mg.marketGroupID, mg.parentGroupID, mg.marketGroupName, mg.description, mg.iconID, mg.hasTypes)
            }

        DbSession.db.run(query.result).map { result =>
            val groups = result.map(MarketGroup.tupled)
            //Ok(Json.toJson(groups))
            //val upickle. upickle.default.read[MarketGroup2](testStr)
            //val jsResult = upickle.default.writeJs(groups)
            val jsResult = upickle.default.write(groups)
            Ok(jsResult)
        }
    }

    def marketTree: Action[AnyContent] = Action.async {
        DbSession.db.run(NsiSchema.invMarketGroups.result)
            .map(_.map(MarketGroup.tupled))
            .map { groups =>
                def makeGroupTree(group: MarketGroup): MarketGroupTree = {
                    val children = groups.filter(_.parentGroupID.contains(group.marketGroupID)).map(makeGroupTree)
                    MarketGroupTree(group.marketGroupID, group.marketGroupName, group.description, children)
                }
                val result = groups.filter(_.parentGroupID.isEmpty).map(makeGroupTree)
                //Logger.warn(result.toString)
                Ok(Json.toJson(result))
            }
    }
}