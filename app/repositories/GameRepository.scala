package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.DBApi
import anorm._
import anorm.SqlParser.{get, scalar, str}
import com.google.inject.ImplementedBy
import models.GameBoard.maybeVisible
import models.VisibleCell
import services.{Bomb, BombAdjacent, Cell, EmptyCell, Game}

@ImplementedBy(classOf[GameRepository])
trait GameRepo {
  def insert(game:Game): Game
  def findGameByName(name:String): Option[Game]
  def updateGame(game:Game): Game
}

@Singleton
class GameRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) extends GameRepo {
  private val db = dbapi.database("default")

  def insert(game: Game): Game = {
    db.withConnection { implicit connection =>
        val gameId = SQL(
          """
          insert into GAME(name,row_count,col_count,bomb_amount)
          values (
            {name}, {rowCount}, {columnCount}, {bombAmount}
          )
        """).on(
          'name -> game.name,
          'rowCount -> game.rows,
          'columnCount -> game.columns,
          'bombAmount -> game.bombs)
          .executeInsert(scalar[Long].single)
         insertBoard(game.currentBoard,game.name,gameId)
         game
    }
  }

 def updateGame(game: Game): Game = {
   insertBoard(game.currentBoard, game.name)
   game
 }

  case class RunningGameCel(cellType: String, isFlagged: Boolean, isOpen: Boolean, bombsTouching: Int, cellsArround: Set[Int])

  def insertBoard(listCel: List[Cell], gameName:String, gameId:Long = 0): List[Cell] = {
    db.withConnection { implicit connection =>
        SQL("""delete from RUNNING_GAME where game_name = {gameName}""").on('gameName -> gameName ).executeUpdate()
        def insertListCel(cell:Cell) = { // TODO use batch update instead
          val runningGameCell = cell match {
            case Bomb(_isFlagged, _isOpen, _) => RunningGameCel("BOMB",_isFlagged,_isOpen,0,Set())
            case EmptyCell(_isFlagged, _isOpen, _cellsArround) => RunningGameCel("EMPTY",_isFlagged,_isOpen,0,_cellsArround)
            case BombAdjacent(_isFlagged, _isOpen, _cellsArround, bombsTouching) => RunningGameCel("BOMB_ADJACENT",_isFlagged,_isOpen,bombsTouching, _cellsArround)
          }
          SQL(
            """
            insert into RUNNING_GAME(game_name,game_id,cell_type,is_flagged,is_open,bombs_touching,cells_arround)
            values (
              {gameName}, {gameId}, {cellType}, {isFlagged},{isOpen},{bombsTouching},{cellsArround}
            )
          """).on(
            'gameName -> gameName,
            'gameId -> gameId,
            'cellType -> runningGameCell.cellType,
            'isFlagged -> runningGameCell.isFlagged,
            'isOpen -> runningGameCell.isOpen,
            'bombsTouching -> runningGameCell.bombsTouching,
            'cellsArround -> runningGameCell.cellsArround.mkString(",")
          )
            .executeInsert(scalar[Long].single)
        }
        listCel.foreach(insertListCel)
       listCel
    }
  }

  val gameParser: RowParser[Game] = {
    get[Long]("GAME.id") ~
      get[String]("GAME.name") ~
      get[Int]("GAME.row_count") ~
      get[Int]("GAME.col_count") ~
      get[Int]("GAME.bomb_amount") map {
      case id ~ name ~ rowCount ~ columnCount ~ bombAmount =>
        new Game(
          rowCount,
          columnCount,
          bombAmount,
          getBoardByGameName(name),
          name)
    }
  }

  def getGameById(gameId: Long): Game = {
    db.withConnection { implicit connection =>
      val game = SQL(
        """
        select * from GAME
        where id = {gameId}
      """).on(
        'gameId -> gameId).as(gameParser.single)
      game
    }
  }



  val runningCelParser: RowParser[RunningGameCel] = {
    get[String]("RUNNING_GAME.cell_type") ~
      get[Boolean]("RUNNING_GAME.is_flagged") ~
      get[Boolean]("RUNNING_GAME.is_open") ~
      get[Int]("RUNNING_GAME.bombs_touching") ~
      get[String]("RUNNING_GAME.cells_arround") map {
      case cellType ~ isFlagged ~ isOpen ~ bombsTouching ~ cellsArround =>
        RunningGameCel(
          cellType,
          isFlagged,
          isOpen,
          bombsTouching,
          cellsArround.split(",")
            .toSet
            .filter(!_.isBlank)
            .map((celIdx:String) => celIdx.toInt),
        )
    }
  }

  def getBoardByGameName(gameName: String): List[Cell] = {
    case class RunningGameCel()
    val runningGameCels = db.withConnection { implicit connection =>
      val game = SQL(
        """
        select * from RUNNING_GAME
        where game_name = {gameName} order by id
      """).on(
        'gameName -> gameName).as(runningCelParser.*)
      game
    }
    runningGameCels.map(runningCel => {
      val runningCelType = runningCel.cellType
      runningCelType match {
        case "BOMB" => Bomb(runningCel.isFlagged, runningCel.isOpen, runningCel.cellsArround)
        case "EMPTY" => EmptyCell(runningCel.isFlagged, runningCel.isOpen, runningCel.cellsArround)
        case "BOMB_ADJACENT" => BombAdjacent(runningCel.isFlagged, runningCel.isOpen, runningCel.cellsArround, runningCel.bombsTouching)
      }
    })
  }


  def findGameByName(name: String): Option[Game] = {
    db.withConnection { implicit connection => SQL(
        """
        select * from GAME
        where name = {name}
      """).on(
        'name -> name).as(gameParser.singleOpt)

    }
  }
}