package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import model.Params

@Singleton
class ParamsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private var storedParams: Option[Params] = None

  def saveParams: Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Params].fold(
      errors => {
        BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors)))
      },
      params => {
        storedParams = Some(params)
        Ok(Json.obj("status" -> "success"))
      }
    )
  }

  def getParams = Action {
    storedParams match {
      case Some(params) => Ok(Json.toJson(params))
      case None => NotFound(Json.obj("status" -> "error", "message" -> "Parameters not found"))
    }
  }

  def getStoredParams: Option[Params] = storedParams
}
