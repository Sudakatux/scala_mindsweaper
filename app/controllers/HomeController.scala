package controllers

import javax.inject._
import models.{GameBoard, GameCreation, VisibleCell}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{ApplicationState, Bomb, BombAdjacent, EmptyCell, Game}

@Singleton
class HomeController @Inject()(cc: ControllerComponents, state: ApplicationState) extends AbstractController(cc) {

  def createGame() = Action(parse.json) { request: Request[JsValue] =>
    val gameCreation = request.body.validate[GameCreation]
    println("it gets here")
    gameCreation.fold(
      errors => {
        BadRequest(Json.obj("status" -> "error"))
      },
      gameCreation => {
        state.updateGame(gameCreation.gameName, Game(gameCreation.rowCount, gameCreation.colCount, gameCreation.bombAmount))
        Ok(Json.toJson(gameCreation)).as("application/json")
      }
    )
  }

  def games() = Action(parse.json) {
    val games = state.games.keys.toList
    Ok(Json.obj("games"->games))
  }

  def gameByName(name: String) = Action(parse.json) {
    val maybeGame = state.games.get(name)

    maybeGame.map(game => GameBoard.gameToGameBoard(name,game))
      .map(game => Ok(Json.toJson(game)).as("application/json"))
      .getOrElse(NotFound(Json.obj("message" -> "Game was not found")).as("application/json"))
  }

  def openCel(name:String, row:Int, col:Int) = Action(parse.json) {
    val maybeGamePlay = state.games.get(name).map(game=> game.openCell(row,col))

    maybeGamePlay.map(game => GameBoard.gameToGameBoard(name,state.updateGame(name,game)))
      .map(game => Ok(Json.toJson(game)).as("application/json"))
      .getOrElse(NotFound(Json.obj("message" -> "Game was not found")).as("application/json"))
  }

  def flagCel(name:String, row:Int, col:Int) = Action(parse.json) {
    val maybeGamePlay = state.games.get(name).map(game=> game.flagCell(row,col))

    maybeGamePlay.map(game => GameBoard.gameToGameBoard(name,state.updateGame(name,game)))
      .map(game => Ok(Json.toJson(game)).as("application/json"))
      .getOrElse(NotFound(Json.obj("message" -> "Game was not found")).as("application/json"))
  }

}
