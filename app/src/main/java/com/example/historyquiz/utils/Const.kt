package com.example.historyquiz.utils

import com.google.gson.Gson

object Const {

    const val TAG = "TAG"
    const val TAG_LOG = "TAG_LOG"

    const val TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DOUBLE_FORM = "%.2f"


    const val MAX_LENGTH = 80
    const val MORE_TEXT = "..."

    const val BASE_URL = "https://ru.wikipedia.org/w/api.php/"

    //image path
    const val IMAGE_START_PATH = "images/users/"
    const val AVATAR = "avatar"
    const val STUB_PATH = "https://upload.wikimedia.org/wikipedia/commons/b/ba/Leonardo_self.jpg"

    //Firebase constants
    const val SEP = "/"
    const val QUERY_END = "\uf8ff"

    //SharedPreferences
    const val USER_DATA_PREFERENCES = "user_data"
    const val USER_USERNAME = "user_username"
    const val USER_PASSWORD = "user_password"

    //API
    //query
    const val FORMAT = "xml"
    const val ACTION_QUERY = "query"
    const val PROP = "extracts|pageimages|description"
    const val EXINTRO = "1"
    const val EXPLAINTEXT = "1"
    const val PIPROP = "original"
    const val PILICENSE = "any"
    const val TITLES = "Толстой, Лев Николаевич"
    //opensearch
    const val ACTION_SEARCH = "opensearch"
    const val UTF_8 = "1"
    const val NAMESPACE = "0"
    const val SEARCH = "Лев Толстой"

    //TestRelation
    const val WIN_GAME = "win_game"
    const val WIN_AFTER_WIN = "after_win_test"
    const val LOSE_AFTER_WIN = "ore_after_test"
    const val TEST_AFTER_WIN = ""

    const val AFTER_TEST = "after_test"
    const val WIN_AFTER_TEST = "after_win_test"
    const val TEST_AFTER_TEST = "ore_after_test"
    const val LOSE_AFTER_TEST = ""

    const val LOSE_GAME = "lose_game"
    const val WIN_AFTER_LOSE = "after_win_test"
    const val LOSE_AFTER_LOSE = "ore_after_test"
    const val TEST_AFTER_LOSE = ""

    const val BEFORE_TEST = "before_test"

    //comment types
    const val CARD_COMMENT_TYPE = "card_comments"

    //TestType
    const val TEST_ONE_TYPE = "test_one_type"
    const val TEST_MANY_TYPE = "test_many_type"

    const val TIME_TYPE = "TIME_TYPE"
    const val NEW_ONES = "NEW_ONES"
    const val OLD_ONES = "OLD_ONES"
    //items
    const val PHOTO_ITEM = "PHOTO_ITEM"
    const val USER_ITEM = "USER_ITEM"
    const val TEST_ITEM = "TEST_ITEM"
    const val CARD_ITEM = "CARD_ITEM"
    const val ABS_CARD = "ABS_CARD"
    const val ITEM_ITEM = "ITEM_ITEM"
    const val LINK_ITEM = "LINK_ITEM"

    //id's
    const val TEST_ID = "TEST_ID"
    const val USER_ID = "USER_ID"
    const val BOT_ID = "JmHmHZUIUORNR42aqXmzV5VujwN2"
    const val DEFAULT_EPOCH_ID = "-LceLTNbCEbXB00ismFk"

    //args
    const val QUESTION_NUMBER = "queston_number"
    const val PAGE_TITLE = "PAGE_TITLE"
    const val PAGE_URL = "PAGE_URL"

    //result nums
    const val ADD_CARD_CODE = 0
    const val ADD_EPOCH_CODE = 1
    const val ADD_LINK_CODE = 2


    const val ONLINE_GAME = "online_game"
    const val BOT_GAME = "bot_game"

    //game modes/stadies
    const val MODE_CHANGE_CARDS = "change_cards"
    const val MODE_PLAY_GAME = "play_game"
    const val MODE_CARD_VIEW = "card_view"
    const val MODE_END_GAME = "end_game"

    const val CARD_NUMBER = 3

    //Friend type
    const val ADD_FRIEND = "addF"
    const val REMOVE_FRIEND = "removeF"
    const val ADD_REQUEST = "addR"
    const val REMOVE_REQUEST = "removeR"

    //Friend's list types
    const val USERS_LIST_TYPE = "type"
    const val DEFAULT_USERS_TYPE = "default_users"

    const val READER_LIST = "readers"
    const val FRIEND_LIST = "friends"
    const val REQUEST_LIST = "requests"

    //GameType
    const val OFFICIAL_TYPE = "official_type"
    const val USER_TYPE = "user_type"

    //Crossing type
    const val WATCHER_TYPE = "watcher"
    const val OWNER_TYPE = "owner"
    const val RESTRICT_OWNER_TYPE = "restrict_owner"
    const val FOLLOWER_TYPE = "follower"

    //Gamer status
    const val ONLINE_STATUS = "online_status"
    const val OFFLINE_STATUS = "offline_status"
    const val STOP_STATUS = "stop_status"
    const val IN_GAME_STATUS = "in_game_status"
    const val NOT_ACCEPTED = "not_accepted"
    const val EDIT_STATUS = "edit_status"

    const val GAME_WIN_POINTS = 20
    const val GAME_LOSE_POINTS = 5
    const val TEST_POINTS = 10

    const val EPOCH_KEY = "EPOCH_KEY"

    val gson = Gson()

    val USER_FRIENDS = "user_friends"


}