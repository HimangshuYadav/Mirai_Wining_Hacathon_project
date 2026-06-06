package com.example.rememberme.ui.main

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LivenessStateTest {
    @Test
    fun testChallengeRandom() {
        val challenge = Challenge.random()
        assertNotNull(challenge)
        assertTrue(challenge in Challenge.values())
    }
}
