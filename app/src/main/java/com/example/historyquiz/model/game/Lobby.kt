package com.example.historyquiz.model.game

import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.Const.CARD_NUMBER
import com.example.historyquiz.utils.Const.OFFICIAL_TYPE
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.google.firebase.database.Exclude

class Lobby {

    lateinit var id: String
    var title: String? = null
    var lowerTitle: String? = null
    lateinit var epochId: String
    lateinit var epoch: Epoch
    var photoUrl: String? = null
    var cardNumber: Int = CARD_NUMBER
    var status: String = ONLINE_STATUS
    var type: String = OFFICIAL_TYPE
    var isFastGame: Boolean = false

    @Exclude
    var isMyCreation: Boolean = false

    var creator: LobbyPlayerData? = null
    var invited: LobbyPlayerData? = null

    @Exclude
    var gameData: GameData? = null

    @Exclude
    var lobbyData: LobbyData? = null

    var usersIds: MutableList<String> = ArrayList()

    @Exclude
    var userList: MutableList<User> = ArrayList()

    companion object {
        val PARAM_creator = "creator"
        val PARAM_invited = "invited"

        const val ONLINE_GAME = "online_game"
    }
}
