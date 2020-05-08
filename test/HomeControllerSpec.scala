package controllers

import models.{GameBoard, GameCreation}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Headers
import play.api.test._
import play.api.test.Helpers._
import play.mvc.Http
import repositories.{GameRepo, GameRepository}
import services.{ApplicationState, Cell, Game}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
//object InMemoryDb {
//  val inMemoryDatabaseConfiguration: Map[String, Any] = Map(
//    "db.default.driver" -> "org.h2.Driver",
//    "db.default.url" -> "jdbc:h2:mem:play;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
//    "db.default.db.user" -> "sa",
//    "db.default.db.password" -> "",
//    "play.modules.disable" -> "play.api.db.evolutions.EvolutionsModule"
//  )
//}

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

//  import InMemoryDb._
//
//  override def fakeApplication(): Application = {
//    val builder = overrideDependencies(
//      new GuiceApplicationBuilder()
//        .configure(inMemoryDatabaseConfiguration)
//    )
//    builder.build()
//  }
//
//  def overrideDependencies(application: GuiceApplicationBuilder): GuiceApplicationBuilder = {
//    application
//  }
  def gameRepo:GameRepo = app.injector.instanceOf(classOf[GameRepository])
  "HomeController" should {
    "Be able to create a game given a configuration. and get the configuration back" in {
      val mockGameCreation = GameCreation("firstTestGame", 4, 12, 3)
      val jsonRequest = Json.toJson(mockGameCreation)
      val home = FakeRequest(POST, "/api/game")
        .withJsonBody(jsonRequest)
      val result = route(app, home).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      val resultJson = contentAsJson(result)
      resultJson mustBe jsonRequest
    }
    "not be able to create a game with wrong values" in {
      val mockGameCreation = GameCreation("someName", 0, 0, 0)
      val jsonRequest = Json.toJson(mockGameCreation)
      val home = FakeRequest(POST, "/api/game")
        .withJsonBody(jsonRequest)
      val result = route(app, home).get

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some("application/json")
    }
    "be able to return a game by name" in {
      val gameName = "myName"
      //rowCount: Int, colCount: Int, bombCount:Int, board: List[Cell],_name:String = "SomeGame"
      gameRepo.insert(Game(2,2,2,gameName))

      val controller = new HomeController(stubControllerComponents(), gameRepo)
      val home = controller.gameByName(gameName).apply(FakeRequest(GET, gameName))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      val resultJson = contentAsJson(home)
      println(resultJson.toString())
      resultJson.toString() mustBe """{"name":"myName","board":[{"cellType":"NotVisible","display":"NotVisible"},{"cellType":"NotVisible","display":"NotVisible"},{"cellType":"NotVisible","display":"NotVisible"},{"cellType":"NotVisible","display":"NotVisible"}],"rowCount":2,"bombCount":2,"gameState":"Finish Him"}"""
    }

    "Be able to open a cell in the game" in {
      val gameName = "openCell"
      gameRepo.insert(Game(1,2,1,gameName))

      val controller = new HomeController(stubControllerComponents(), gameRepo)
      val home = controller.openCel(gameName, 0, 0).apply(FakeRequest(GET, gameName))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      val resultJson = contentAsJson(home)
      val board = resultJson.as[GameBoard]
      board.board.count(cell => cell.cellType != "NotVisible") >= 1 mustBe (true)
    }
    "be able to flag a cell in the game" in {
      val gameName = "flagCell"
      gameRepo.insert(Game(1,2,1,gameName))
      val controller = new HomeController(stubControllerComponents(), gameRepo)
      val home = controller.flagCel(gameName, 0, 0).apply(FakeRequest(GET, gameName))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      val resultJson = contentAsJson(home)
      val board = resultJson.as[GameBoard]
      board.board.count(cell => cell.cellType != "Flagged") >= 1 mustBe (true)
    }
    "not be able to create a game if game with name exists" in {
      val gameName = "existingName"
      gameRepo.insert(Game(1,2,1,gameName))

      val mockGameCreation = GameCreation(gameName, 2, 2, 1)
      val jsonRequest = Json.toJson(mockGameCreation)
      val home = FakeRequest(POST, "/api/game")
        .withJsonBody(jsonRequest)
      val result = route(app, home).get

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some("application/json")

    }

  }
}
