import scala.annotation.tailrec
import scala.util.Random
abstract class Cell(col: Int, row: Int, isFlagged: Boolean, isOpen: Boolean, neighbors: Set[Int], bombsTouching: Int)

case class EmptyCell(col: Int, row: Int, isFlagged: Boolean, isOpen: Boolean, neighbors: Set[Int], bombsTouching: Int) extends Cell(col,row,isFlagged,isOpen,neighbors,bombsTouching);
case class Bomb(col: Int, row: Int, isFlagged: Boolean, isOpen: Boolean, neighbors: Set[Int], bombsTouching: Int) extends Cell(col,row,isFlagged,isOpen,neighbors,bombsTouching);
case class BombAdjacent(col: Int, row: Int, isFlagged: Boolean, isOpen: Boolean, neighbors: Set[Int], bombsTouching: Int) extends Cell(col,row,isFlagged,isOpen,neighbors,bombsTouching);

object Game {
  type Position = (Int, Int) // row, column

  def position(rowCount: Int, idx: Int): Position = {
    val row = (idx % rowCount)
    val column = (idx - row) / rowCount
    (row, column)
  }

  def index(rowCount: Int, position:Position): Int = {
    val (row,col) = position
    col * rowCount + row
  }

  def randomBombPositionsInGrid(bombCount:Int, boardSize: Int): Set[Int] = { // Bomb position
    @tailrec
    def placeBombs(placedBombs:Set[Int]): Set[Int] ={
      if (placedBombs.size == bombCount){
        return placedBombs
      }
      val bombIdx = Random.nextInt(boardSize-1)
      placeBombs(placedBombs + bombIdx)
    }
    placeBombs(Set())
  }

  def adjacentsForPosition(boardSize:Int, rowCount:Int, position: Position):Set[Int] = {
    val (row, col) = position
    val possibleCandidates = for (col <- Set(col,col+1,col-1);
      row <-Set(row,row+1,row-1))
      yield(row,col)

    def existsInBoard: (Int) => Boolean = (idx:Int)=> (idx < boardSize && idx >=0 )

    val candidatesWithoutSelf = possibleCandidates.filter(_!=position).filter((pos)=> (pos._1 >=0 && pos._2 >=0))
    candidatesWithoutSelf.map(index(rowCount,_)).filter(existsInBoard)
  }

  def bombAdjacentByBombsNotBombs(boardSize:Int, rowCount:Int, bombIdx:Set[Int]):Map[Int,Set[Int]] = {
    val bombAdjacentsByBomb:Map[Int,Set[Int]] = bombIdx
      .map(bIdx=>Tuple2(bIdx,adjacentsForPosition(boardSize,rowCount,position(rowCount,bIdx))))
      .toMap

    def notABomb = (adjacentIdx:Int) => !bombIdx.contains(adjacentIdx)

    bombAdjacentsByBomb.mapValues(_.filter(notABomb))

  }

  def initBoard(rowCount:Int, colCount:Int, bombCount:Int):List[Cell]={
    val boardSize = rowCount * colCount
    val bombIdxPositions = randomBombPositionsInGrid(bombCount,boardSize)
    val bombAdjacentsByBomb : Map[Int,Set[Int]] = bombAdjacentByBombsNotBombs(boardSize,rowCount,bombIdxPositions)
    val adjacentIndexes = bombAdjacentsByBomb.values.flatten

  }


}
