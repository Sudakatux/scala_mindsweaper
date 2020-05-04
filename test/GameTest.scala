import org.scalatestplus.play.PlaySpec

class GameTest extends PlaySpec{
"Game " should {
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
  "returns bomb adjacents for bombs that are not bombs" in {
    val rowSize = 3
    val colSize = 4
    val boardSize = rowSize*colSize

    val fakeBomb = Set(4)
    val result = Game.bombAdjacentByBombsNotBombs(boardSize,rowSize,fakeBomb)
    println(s" Result is ${result}")
    result.values.flatten.size mustBe maxPossibleAdjacents

    val fakeBombs = Set(4,7)
    val result1 = Game.bombAdjacentByBombsNotBombs(boardSize,rowSize,fakeBombs)
    result1.get(4).get.size mustBe (maxPossibleAdjacents-1) // Has a bomb next to it should filter that bomb
  }
}
}
