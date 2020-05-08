package services

import org.scalatestplus.play.PlaySpec

class GameTest extends PlaySpec{

"services.Game " should {
    /*
    0 3 6 9
    1 4 7 10
    2 5 8 11
    */
   val maxPossibleAdjacents = 8
  "compute the correct position given an index in the column stack" in {
    val result = Game.position(3,3)
    result mustBe (0,1)

    val result1 = Game.position(3,7)
    result1 mustBe (1,2)
  }

  "compute the correct index given a position" in {
    val resultIdx = Game.index(3,(0,1))
    resultIdx mustBe 3

    val resultIdx1 = Game.index(3,(1,2))
    resultIdx1 mustBe 7

    val resultidx2 = Game.index(3,(0,0))
    resultidx2 mustBe 0
  }

  "add random bombs within grid" in {
    val bombCount = 3
    val boardSize = 12
    val result = Game.randomBombPositionsInGrid(bombCount,boardSize)
    result.size > 1 mustBe true // Works
    result.size mustBe bombCount // Returns the appropiate size
    result.filter(p=> p < boardSize && p >= 0 ).size mustBe bombCount //  makes sense
  }

  "returns adjacent positions" in {
    val rowSize = 3
    val colSize = 4
    val boardSize = rowSize*colSize


    val positionOfAMiddleNumber = (1,1)
    val indexOfMiddleNumber = 4
    val result = Game.adjacentsForPosition(boardSize,rowSize,positionOfAMiddleNumber) // Testing the 4 in the middle
    result.contains(indexOfMiddleNumber) mustBe false // Should not show itself as a position
    result.filter(rIdx=>rIdx >=0 && rIdx<boardSize).size mustBe maxPossibleAdjacents

    // Gets rid of points out of the grid
    val positionOfVertex = (0,0)
    val indexOfVertex = 0
    val vertexMaxAdjacents = 3
    val result1 = Game.adjacentsForPosition(boardSize,rowSize,positionOfVertex) // Testing the 4 in the middle
    result1.contains(indexOfVertex) mustBe false // Should not show itself as a position
    result1.filter(rIdx=>rIdx >=0 && rIdx<boardSize).size mustBe vertexMaxAdjacents
    result1.equals(Set(3,4,1)) mustBe true
  }
  "returns correct adjacent for fields in margin" in {
    val rowSize = 4
    val colSize = 4
    val boardSize = rowSize*colSize
    val result2 = Game.adjacentsForPosition(boardSize,rowSize,(3,0))

    result2 mustBe Set(6, 2, 7)
  }

  "returns bomb adjacents for bombs that are not bombs" in {
    val rowSize = 3
    val colSize = 4
    val boardSize = rowSize*colSize

    val fakeBomb = Set(4)
    val result = Game.bombAdjacentByBombsNotBombs(boardSize,rowSize,fakeBomb)
    result.values.flatten.size mustBe maxPossibleAdjacents

    val fakeBombs = Set(4,7)
    val result1 = Game.bombAdjacentByBombsNotBombs(boardSize,rowSize,fakeBombs)
    result1.get(4).get.size mustBe (maxPossibleAdjacents-1) // Has a bomb next to it should filter that bomb


    //result1.get(4).get.size mustBe (maxPossibleAdjacents-1) // Has a bomb next to it should filter that bomb
  }
  "generates a board with bombs adjacents and empty" in {
    val rowSize = 3
    val colSize = 4
    val bombCount = 1
    val result = Game.initBoard(3,4,bombCount)
    result.size mustBe rowSize*colSize
    result.count(_ match {
      case Bomb(_,_,_) => true
      case _ => false
    }) mustBe bombCount
  }
  "flags a cell in the services.Game" in {
    val rowSize = 3
    val colSize = 4
    val bombCount = 1

    Game(rowSize,colSize,bombCount,"SomeName").flagCell(0,0).currentBoard.count(cell=>cell.isFlagged) mustBe 1
  }
  "open an cell in the game" in {
    val rowSize = 3
    val colSize = 4
    val bombCount = 1

    new Game(rowSize,colSize,bombCount,List.tabulate(4*3)(_=>BombAdjacent(false,false,Set(),2))).openCell(0,0).currentBoard.filter(_.isOpen).size mustBe(1)
  }
  "open an empty cell in the game" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,false,Set(1,3,4)), //0,0
      BombAdjacent(false,false,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,false,Set(1,5)),//2,0
      BombAdjacent(false,false,Set(0,1,4),1),
      BombAdjacent(false,false,Set(0,1,2,3,5),1),
      EmptyCell(false,false,Set(1,2,4))
    )

    new Game(rowSize,colSize,1,board)
      .openCell(2,0).currentBoard.filter(_.isOpen).size mustBe(4)
  }
  "open an adjacent cell" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,false,Set(1,3,4)), //0,0
      BombAdjacent(false,false,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,false,Set(1,5)),//2,0
      BombAdjacent(false,false,Set(0,1,4),1),
      BombAdjacent(false,false,Set(0,1,2,3,5),1),
      EmptyCell(false,false,Set(1,2,4))
    )

    new Game(rowSize,colSize,1,board)
      .openCell(1,0).currentBoard.filter(_.isOpen).size mustBe(1)
  }

  "open a bomb" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,false,Set(1,3,4)), //0,0
      BombAdjacent(false,false,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,false,Set(1,5)),//2,0
      BombAdjacent(false,false,Set(0,1,4),1),
      BombAdjacent(false,false,Set(0,1,2,3,5),1),
      EmptyCell(false,false,Set(1,2,4))
    )

    new Game(rowSize,colSize,1, board)
      .openCell(0,0).currentBoard.filter(_.isOpen).size mustBe(board.size)
  }
  "open an empty cell in the game when flagged is prsent" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,false,Set(1,3,4)), //0,0
      BombAdjacent(true,false,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,false,Set(1,5)),//2,0
      BombAdjacent(false,false,Set(0,1,4),1),
      BombAdjacent(false,false,Set(0,1,2,3,5),1),
      EmptyCell(false,false,Set(1,2,4))
    )

    new Game(rowSize,colSize,1,board)
      .openCell(2,0).currentBoard.filter(_.isOpen).size mustBe(4-1) // should not open the flagged cell
  }
  "open a flagged cell should do nothing" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,false,Set(1,3,4)), //0,0
      BombAdjacent(true,false,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,false,Set(1,5)),//2,0
      BombAdjacent(false,false,Set(0,1,4),1),
      BombAdjacent(false,false,Set(0,1,2,3,5),1),
      EmptyCell(false,false,Set(1,2,4))
    )

    new Game(rowSize,colSize,1,board)
      .openCell(1,0).currentBoard mustBe board// should not open the flagged cell
  }

  "Tell if the game has been won" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,false,Set(1,3,4)), //0,0
      BombAdjacent(false,true,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,true,Set(1,5)),//2,0
      BombAdjacent(false,true,Set(0,1,4),1),
      BombAdjacent(false,true,Set(0,1,2,3,5),1),
      EmptyCell(false,true,Set(1,2,4))
    )

    new Game(rowSize,colSize,1,board).isWin mustBe(true)
    new Game(rowSize,colSize,1,board).isLoose mustBe(false)

  }
  "Tell if the game has been lost" in {
    val rowSize = 3
    val colSize = 2
    val board = List(
      Bomb(false,true,Set(1,3,4)), //0,0
      BombAdjacent(false,true,Set(0,2,3,4,5),1),//1,0
      EmptyCell(false,true,Set(1,5)),//2,0
      BombAdjacent(false,true,Set(0,1,4),1),
      BombAdjacent(false,true,Set(0,1,2,3,5),1),
      EmptyCell(false,true,Set(1,2,4))
    )

    new Game(rowSize,colSize,1,board).isWin mustBe(false)
    new Game(rowSize,colSize,1,board).isLoose mustBe(true)

  }
}
}
