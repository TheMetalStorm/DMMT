package datatypes

import JPGSegments.SOF0
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class SOF0Test {

    @Test
    fun `componentCount must be 1 or 3`() {
        val invalidComponentCounts: List<UByte> = listOf(0u, 2u, 4u)

        invalidComponentCounts.forEach { count ->
                val exception = assertFailsWith<Exception> {
                SOF0(8u, 1u, 1u, 1u, 1u, count, generateSequence { 1.toUByte() }.take(count.toInt()*3).toCollection(ArrayList()))
            }
            assertTrue("componentCount has to be 1 or 3" in exception.message!!)
        }
    }

    @Test
    fun `components size must equal componentCount times 3`() {
        assertFailsWith<Exception> {
            SOF0(8u, 1u, 1u, 1u, 1u, 3u, arrayListOf(1u, 2u)) // Only 2 components instead of 3*3=9
        }
    }

    @Test
    fun `pictureSizeX and pictureSizeY must be greater than 0`() {
        assertFailsWith<Exception> {
            SOF0(8u, 0u, 0u, 0u, 0u, 1u, arrayListOf(1u, 2u, 3u))
        }
    }

    @Test
    fun `dataAccuracy must be 8, 12, or 16`() {
        val invalidDataAccuracies: List<UByte> = listOf(0u, 7u, 9u, 11u, 13u, 15u, 17u)

        invalidDataAccuracies.forEach { accuracy ->
                assertFailsWith<Exception> {
                        SOF0(accuracy, 1u, 1u, 1u, 1u, 1u, arrayListOf(1u, 2u, 3u))
                }
        }
    }

    @Test
    fun `valid input should not throw`() {
        SOF0(8u, 1u, 1u, 1u, 1u, 3u, arrayListOf(1u, 2u, 3u, 4u, 5u, 6u, 7u, 8u, 9u)) // This is a valid input
    }
}