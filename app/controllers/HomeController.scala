package controllers

import javax.inject._
import models.{GameBoard, GameCreation, VisibleCell}
import org.checkerframework.checker.units.qual.min
import play.api.data.{Form, Forms}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._
import repositories.{GameRepo, GameRepository}
import services.{ApplicationState, Bomb, BombAdjacent, EmptyCell, Game}

@Singleton
class HomeController @Inject()(cc: ControllerComponents, gameRepository: GameRepo) extends AbstractController(cc) {

  def createGame() = Action(parse.json) { implicit request: Request[JsValue] =>
    //val gameCreation = request.body.validate[GameCreation]
    val gameCreationForm = Form(Forms.mapping(
      "gameName"-> nonEmptyText,
      "rowCount"-> number(min=1),
      "colCount"-> number(min=1),
      "bombAmount"-> number(min=1)
    )(GameCreation.apply)(GameCreation.unapply))

    gameCreationForm.bindFromRequest.fold(
      errors => {
        BadRequest(Json.obj("status" -> "error"))
      },
      gameCreation => {
        // state.updateGame(gameCreation.gameName, Game(gameCreation.rowCount, gameCreation.colCount, gameCreation.bombAmount))
        gameRepository.findGameByName(gameCreation.gameName)
          .map(_=>
            BadRequest(Json.obj("status" -> "error game exists with that name. Pick a differnt name"))
          ).getOrElse({
            gameRepository.insert(
              Game(gameCreation.rowCount, gameCreation.colCount, gameCreation.bombAmount,gameCreation.gameName))
            Ok(Json.toJson(gameCreation)).as("application/json")
        })
      }
    )
  }

  def games() = Action(parse.json) {
    //val games = state.games.keys.toList
    Ok(Json.obj("games" -> List[String]()))
  }

  def gameByName(name: String) = Action(parse.json) {
    val maybeGame = gameRepository.findGameByName(name)

    maybeGame.map(game => GameBoard.gameToGameBoard(name,game))
      .map(game => Ok(Json.toJson(game)).as("application/json"))
      .getOrElse(NotFound(Json.obj("message" -> "Game was not found")).as("application/json"))
  }

  def openCel(name:String, row:Int, col:Int) = Action(parse.json) {
    val maybeGamePlay =  gameRepository.findGameByName(name).map(game=> game.openCell(row,col)).map(gameRepository.updateGame(_))
    // Needs refactor
    maybeGamePlay.map(game => GameBoard.gameToGameBoard(name,game))
      .map(game => Ok(Json.toJson(game)).as("application/json"))
      .getOrElse(NotFound(Json.obj("message" -> "Game was not found")).as("application/json"))
  }

  def flagCel(name:String, row:Int, col:Int) = Action(parse.json) {
    val maybeGamePlay = gameRepository.findGameByName(name).map(game=> game.flagCell(row,col)).map(gameRepository.updateGame(_))
    //Needs refactor
    maybeGamePlay.map(game => GameBoard.gameToGameBoard(name,game))
      .map(game => Ok(Json.toJson(game)).as("application/json"))
      .getOrElse(NotFound(Json.obj("message" -> "Game was not found")).as("application/json"))
  }

}
