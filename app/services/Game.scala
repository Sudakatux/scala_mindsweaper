package services

import scala.annotation.tailrec
import scala.util.Random

abstract class Cell(_isFlagged: Boolean, _isOpen: Boolean, cellsArround: Set[Int]) {
  val isFlagged = _isFlagged
  val isOpen = _isOpen

  def open(): Cell

  def toggleFlag(): Cell

}

case class EmptyCell(_isFlagged: Boolean, _isOpen: Boolean, cellsArround: Set[Int]) extends Cell(_isFlagged, _isOpen, cellsArround) {
  override def open(): Cell = EmptyCell(_isFlagged, true, cellsArround)

  override def toggleFlag(): Cell = EmptyCell(!_isFlagged, _isOpen, cellsArround)
}

case class Bomb(_isFlagged: Boolean, _isOpen: Boolean, cellsArround: Set[Int]) extends Cell(_isFlagged, _isOpen, cellsArround) {
  override def open(): Cell = Bomb(_isFlagged, true, cellsArround)

  override def toggleFlag(): Cell = Bomb(!_isFlagged, _isOpen, cellsArround)
}

case class BombAdjacent(_isFlagged: Boolean, _isOpen: Boolean, cellsArround: Set[Int], bombsTouching: Int) extends Cell(_isFlagged, _isOpen, cellsArround) {
  override def open(): Cell = BombAdjacent(_isFlagged, true, cellsArround, bombsTouching)

  override def toggleFlag(): Cell = BombAdjacent(!_isFlagged, _isOpen, cellsArround, bombsTouching)
}

class Game(rowCount: Int, colCount: Int, bombCount:Int, board: List[Cell]) {
  val currentBoard = board
  val rows: Int = rowCount
  val columns: Int = colCount
  val bombs: Int = bombCount

  def flagCell(row: Int, col: Int): Game = {
    val idx = Game.index(rowCount, (row, col))
    val current = board(idx)
    val updatedBoard: List[Cell] = board.patch(idx, Seq(current.toggleFlag()), 1)
    new Game(rowCount, colCount, bombCount, updatedBoard)
  }

  def openCell(row: Int, col: Int): Game = {
    @tailrec
    def openEmptyCell(cellsToOpen: Set[Int], openedCells: Set[Int], board: List[Cell]): List[Cell] = {
      val unverifiedRest = cellsToOpen -- openedCells
      if (unverifiedRest.isEmpty) return board
      val currentIdx = unverifiedRest.head
      val current = board(currentIdx)

      def openIfNotFlagged(isFlagged:Boolean):List[Cell] = if(isFlagged) board else board.patch(currentIdx, Seq(current.open()), 1)

      current match {
        case EmptyCell(_isFlagged, _, cellsArround) => openEmptyCell(cellsArround ++ unverifiedRest.tail, openedCells + currentIdx, openIfNotFlagged(_isFlagged))
        case BombAdjacent(_isFlagged, _, _, _) => openEmptyCell(unverifiedRest.tail, openedCells + currentIdx, openIfNotFlagged(_isFlagged))
        case _ => openEmptyCell(unverifiedRest.tail, openedCells, board)
      }
    }

    val idx = Game.index(rowCount, (row, col))
    val current = board(idx)
    if(current.isFlagged) return new Game(rowCount, colCount, bombCount, board)
    val boardAfterOpen: List[Cell] = current match {
      case Bomb(_, _, _) => board.map(cell => cell.open())
      case EmptyCell(_, _, cellsArround) => openEmptyCell(cellsArround, Set(), board.patch(idx, Seq(current.open()), 1))
      case _ => board.patch(idx, Seq(current.open()), 1)
    }
    new Game(rowCount, colCount, bombCount, boardAfterOpen)
  }

  def isLoose: Boolean = {
    board.filter(cell => cell.isOpen).size == board.size
  }

  def isWin: Boolean = {
    val closedBombs = board.filter(cell => cell match {
      case Bomb(_, _isOpen, _) => !_isOpen
      case _ => false
    })
    val openedCells = board.filter(_.isOpen)
    closedBombs.size > 0 && (closedBombs.size + openedCells.size == board.size)
  }


}

object Game {
  def apply(rowCount: Int, colCount: Int, bombAmount: Int): Game = new Game(rowCount, colCount, bombAmount, initBoard(rowCount, colCount, bombAmount))

  type Position = (Int, Int) // row, column

  def position(rowCount: Int, idx: Int): Position = {
    val row = (idx % rowCount)
    val column = (idx - row) / rowCount
    (row, column)
  }

  def index(rowCount: Int, position: Position): Int = {
    val (row, col) = position
    col * rowCount + row
  }

  /**
   * Given a board size and a bomb count will return a set of random indexes to deploy bombs
   * @param bombCount
   * @param boardSize
   * @return
   */
  def randomBombPositionsInGrid(bombCount: Int, boardSize: Int): Set[Int] = { // services.Bomb position
    @tailrec
    def placeBombs(placedBombs: Set[Int]): Set[Int] = {
      if (placedBombs.size == bombCount) {
        return placedBombs
      }
      val bombIdx = Random.nextInt(boardSize - 1)
      placeBombs(placedBombs + bombIdx)
    }

    placeBombs(Set())
  }

  /**
   * Takes a board size a row count and a position. will return a set containing the indexes
   * of all adjacents
   * @param boardSize
   * @param rowCount
   * @param position
   * @return
   */
  def adjacentsForPosition(boardSize: Int, rowCount: Int, position: Position): Set[Int] = {
    val (row, col) = position
    val possibleCandidates = for (col <- Set(col, col + 1, col - 1);
                                  row <- Set(row, row + 1, row - 1))
      yield (row, col)

    val colCount = boardSize/rowCount
    def existsInBoard: (Int) => Boolean = (idx: Int) => (idx < boardSize && idx >= 0)
    def positionIsWithinMargins: Position => Boolean = (pos)=>   (pos._1 >= 0 && pos._2 >= 0) && (pos._1<(rowCount) && pos._2 <= (colCount))

    val candidatesWithoutSelf = possibleCandidates.toSeq
      .filter(_ != position)
      .filter(positionIsWithinMargins)
      .toSet

    candidatesWithoutSelf.map(index(rowCount, _)).filter(existsInBoard)
  }

  /**
   * takes a board size a count. and a list of bombIndex
   * returns a Set of all cells that are bomb adjacents but are not bombs
   * @param boardSize board size
   * @param rowCount amount of rows
   * @param bombIdx list of located bombs
   * @return
   */
  def bombAdjacentByBombsNotBombs(boardSize: Int, rowCount: Int, bombIdx: Set[Int]): Map[Int, Set[Int]] = {
    val bombAdjacentsByBomb: Map[Int, Set[Int]] = bombIdx
      .map(bIdx => Tuple2(bIdx, adjacentsForPosition(boardSize, rowCount, position(rowCount, bIdx))))
      .toMap
    def notABomb = (adjacentIdx: Int) => !bombIdx.contains(adjacentIdx)

    bombAdjacentsByBomb.mapValues(_.filter(notABomb))

  }

  /**
   * Initializes a board with no plays
   * @param rowCount
   * @param colCount
   * @param bombCount
   * @return
   */
  def initBoard(rowCount: Int, colCount: Int, bombCount: Int): List[Cell] = {
    val boardSize = rowCount * colCount
    val bombIdxPositions = randomBombPositionsInGrid(bombCount, boardSize)
    val bombAdjacentsByBomb: Map[Int, Set[Int]] = bombAdjacentByBombsNotBombs(boardSize, rowCount, bombIdxPositions)
    val adjacentIndexes = bombAdjacentsByBomb.values.toList.flatten
    (0 until boardSize).map(boardIdx => {
      val idxPosition = position(rowCount, boardIdx)
      val cellsArround = adjacentsForPosition(boardSize, rowCount, idxPosition)
      val (row, col) = idxPosition

      if (bombIdxPositions.contains(boardIdx)) { // If index is due to be a bomb
        Bomb(
          false,
          false,
          cellsArround)
      } else if (adjacentIndexes.contains(boardIdx)) { // if its next to a services.Bomb
        BombAdjacent(false, false, cellsArround, adjacentIndexes.count(_ == boardIdx))
      } else {
        EmptyCell(false, false, cellsArround)
      }
    }).toList
  }
}
