package controllers

import models.{GameBoard, GameCreation}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.test._
import play.api.test.Helpers._
import play.mvc.Http
import services.{ApplicationState, Game}

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController" should {

    "Be able to create a game given a configuration. and get the configuration back" in {
      val appState = new ApplicationState()
      val controller = new HomeController(stubControllerComponents(), appState)
      val mockGameCreation = GameCreation("someName",4,12,3)
      val jsonRequest  = Json.toJson(mockGameCreation)
      val home = FakeRequest(POST, "/api/game")
        .withJsonBody(jsonRequest)
      val result = route(app,home).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      val resultJson = contentAsJson(result)
      resultJson mustBe jsonRequest
    }
    "Be able to return a game by name" in {
      val appState = new ApplicationState()
      val gameName = "name"
      appState.updateGame(gameName,Game(1,2,1))
      val controller = new HomeController(stubControllerComponents(), appState)
      val home = controller.gameByName(gameName).apply(FakeRequest(GET,gameName))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      val resultJson = contentAsJson(home)
      resultJson.toString() mustBe """{"name":"name","board":[{"cellType":"NotVisible","display":"NotVisible"},{"cellType":"NotVisible","display":"NotVisible"}],"rowCount":1,"gameState":"Finish Him"}"""
    }
    "Be able to open a cell in the game" in {
      val appState = new ApplicationState()
      val gameName = "name"
      appState.updateGame(gameName,Game(1,2,1))
      val controller = new HomeController(stubControllerComponents(), appState)
      val home = controller.openCel(gameName,0,0).apply(FakeRequest(GET,gameName))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      val resultJson = contentAsJson(home)
      val board = resultJson.as[GameBoard]
      board.board.count(cell=>cell.cellType!="NotVisible") >= 1 mustBe(true)
    }
    "Be able to flag a cell in the game" in {
      val appState = new ApplicationState()
      val gameName = "name"
      appState.updateGame(gameName,Game(1,2,1))
      val controller = new HomeController(stubControllerComponents(), appState)
      val home = controller.flagCel(gameName,0,0).apply(FakeRequest(GET,gameName))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      val resultJson = contentAsJson(home)
      val board = resultJson.as[GameBoard]
      board.board.count(cell=>cell.cellType!="Flagged") >= 1 mustBe(true)
    }
  }
}
