package com.server.engine.game.entity.character.components

import com.server.engine.game.components.Component

class RankComponent : Component {

    var rank: Int = 0
    var nextRankProgress: Int = 0
    var experienceForNextRank: Int = 1000

}