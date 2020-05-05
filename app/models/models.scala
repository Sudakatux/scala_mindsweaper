package models

import play.api.libs.json.Json
import services.{Bomb, BombAdjacent, EmptyCell, Game}


case class GameCreation(gameName: String, rowCount: Int, colCount: Int, bombAmount: Int)

object GameCreation {
  implicit val gameCreationImplicitReads = Json.reads[GameCreation]
  implicit val gameCreationImplicitWrites = Json.writes[GameCreation]
}

case class VisibleCell(cellType:String, display:String)
object VisibleCell {
  implicit val visibleCellImplicitReads = Json.reads[VisibleCell]
  implicit val visibleCellImplicitWrites = Json.writes[VisibleCell]
}

case class GameBoard(board:List[VisibleCell],gameConfiguration:GameCreation)
object GameBoard {
  implicit val gameBoardImplicitReads = Json.reads[GameBoard]
  implicit val gameBoardImplicitWrites = Json.writes[GameBoard]

  def maybeVisible(isOpen: Boolean, cellType: String): String = {
    if (isOpen) cellType else "NotVisible"
  }

  def gameToGameBoard(name:String,game:Game):GameBoard =
    GameBoard(game.currentBoard.map(_ match {
      case Bomb(_isFlagged, _isOpen, _) => VisibleCell(cellType = maybeVisible(_isOpen, "Bomb"), display = "NotVisible")
      case EmptyCell(_isFlagged, _isOpen, _) => VisibleCell(cellType = maybeVisible(_isOpen, "Empty"), display = "NotVisible")
      case BombAdjacent(_isFlagged, _isOpen, _, bombsTouching) => VisibleCell(cellType = maybeVisible(_isOpen, "Adjacent"), display = {
        maybeVisible(_isOpen, s"${bombsTouching}")
      })
    }), GameCreation(name, game.rows, game.columns, 0))
}

