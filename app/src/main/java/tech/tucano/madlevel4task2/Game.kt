package tech.tucano.madlevel4task2

import androidx.room.*

@Entity(tableName = "game_table")
data class Game (
    @ColumnInfo(name="date")
    var date: String,

    @ColumnInfo(name="player_set")
    var playerSet: String,

    @ColumnInfo(name="cpu_set")
    var cpuSet: String,

    @ColumnInfo(name="result")
    var result: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
)