import org.scalatestplus.play.PlaySpec

class GameTest extends PlaySpec{
"Game " should {
    /*
    0 3 6 9
    1 4 7 10
    2 5 8 11
    */
  "compute the correct position given an index in the column stack" in {
    val result = Game.position(3,3)
    result mustBe (0,1)

    val result1 = Game.position(3,7)
    result1 mustBe (1,2)
  }

  "Compute the correct index given a position" in {
    val resultIdx = Game.index(3,(0,1))
    resultIdx mustBe 3

    val resultIdx1 = Game.index(3,(1,2))
    resultIdx1 mustBe 7

  }
}
}
