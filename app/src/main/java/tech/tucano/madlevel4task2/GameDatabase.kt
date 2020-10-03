package tech.tucano.madlevel4task2

import androidx.room.Database

@Database (entities = [Game::class], version = 1, exportSchema = false)
abstract class GameDatabase {
    abstract fun gameDao() : GameDao

    companion object {

    }
}