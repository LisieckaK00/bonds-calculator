package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import model.Params

/**
 * ParamsController handles the storage and retrieval of parameter data.
 *
 * @param cc ControllerComponents for Play Framework.
 */
@Singleton
class ParamsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private var storedParams: Option[Params] = None

  /**
   * Saves the provided parameters to the controller.
   *
   * @return A JSON response indicating success or error.
   */
  def saveParams(): Action[JsValue] = Action(parse.json) { request =>
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

  /**
   * Retrieves the stored parameters.
   *
   * @return A JSON response with the stored parameters or an error if no parameters are found.
   */
  def getParams = Action {
    storedParams match {
      case Some(params) => Ok(Json.toJson(params))
      case None => NotFound(Json.obj("status" -> "error", "message" -> "Parameters not found"))
    }
  }

  /**
   * Provides access to the stored parameters.
   *
   * @return An Option containing the stored parameters or None if no parameters are stored.
   */
  def getStoredParams: Option[Params] = storedParams
}
