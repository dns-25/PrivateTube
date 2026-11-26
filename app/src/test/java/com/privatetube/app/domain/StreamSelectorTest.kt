package com.privatetube.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StreamSelectorTest {
    private val streams = listOf(240, 720, 1080, 480).map { StreamOption(it, "u$it") }

    @Test fun zeroMeansBest() = assertEquals(1080, chooseStream(streams, 0)!!.height)
    @Test fun picksBestNotAbovePreference() = assertEquals(720, chooseStream(streams, 720)!!.height)
    @Test fun preferenceBetweenQualitiesRoundsDown() = assertEquals(480, chooseStream(streams, 600)!!.height)
    @Test fun allTooHighFallsBackToLowest() = assertEquals(240, chooseStream(streams, 144)!!.height)
    @Test fun emptyGivesNull() = assertNull(chooseStream(emptyList(), 720))
}
