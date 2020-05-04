object Game {

  def position(rowCount: Int, idx: Int): (Int, Int) = {
    val row = (idx % rowCount)
    val column = (idx - row) / rowCount
    (row, column)
  }

  def index(rowCount: Int, position:(Int,Int)): Int = {
    val (row,col) = position
    col * rowCount + row
  }
}
