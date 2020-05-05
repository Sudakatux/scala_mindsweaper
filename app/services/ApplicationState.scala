package services

import javax.inject.Singleton

@Singleton
class ApplicationState {
  var games = Map[String,Game]()
//  def createGame(gameName:String,rowCount:Int,colCount:Int,bombs:Int):Game = {
//    val createdGame = Game(rowCount,colCount,bombs)
//    games += (gameName-> Game(rowCount,colCount,bombs))
//    createdGame
//  }

  def updateGame(gameName:String, game:Game):Game = {
    games += (gameName->game)
    games(gameName)
  }

}
