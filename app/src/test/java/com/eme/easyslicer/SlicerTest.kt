package com.eme.easyslicer

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test


class SlicerTest {

    lateinit var center: Point
    var radius = 0F

    @Before
    fun setUp() {
        center = Point(300F, 300F)
        radius = 10F
    }

    @Test
    fun getCoords_2slices_happyCase() {
        // Given
        val slices = 2
        val expected = listOf(Point(300F, 290F), Point(300F, 310F))

        // When
        val coords = getCoords(center, slices, radius)

        // Then
        assertThat(coords).isNotNull()
        assertThat(coords).hasSize(slices)
        assertThat(coords).containsExactlyElementsIn(expected)
    }

    @Test
    fun getCoords_4slices_happyCase() {
        // Given
        val slices = 4
        val expected =
            listOf(Point(310F, 300F), Point(300F, 290F), Point(290F, 300F), Point(300F, 310F))

        // When
        val coords = getCoords(center, slices, radius)

        // Then
        assertThat(coords).isNotNull()
        assertThat(coords).hasSize(slices)
        assertThat(coords).containsExactlyElementsIn(expected)
    }
}