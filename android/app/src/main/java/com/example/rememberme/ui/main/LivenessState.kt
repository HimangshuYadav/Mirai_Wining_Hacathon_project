package com.example.rememberme.ui.main

import kotlin.random.Random

enum class Challenge {
    TURN_LEFT,
    TURN_RIGHT,
    SMILE;

    companion object {
        fun random(): Challenge = values()[Random.nextInt(values().size)]
    }
}

enum class LivenessStatus {
    Waiting,
    Passed,
    Failed
}
