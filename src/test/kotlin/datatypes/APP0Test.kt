package datatypes

import JPGSegments.APP0
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class APP0Test {
    @Test
    fun `densityUnits must be 0, 1, or 2`() {
        val invalidDensityUnits: List<UByte> = listOf(3u, 4u, 255u)

        invalidDensityUnits.forEach { unit ->
            assertFailsWith<Exception> {
                APP0(unit, 1u, unit, 1u, 1u, 1u, 1u)
            }.also { exception ->
                assertTrue("Density Units should be 0, 1 or 2" in exception.message!!)
            }
        }
    }

    @Test
    fun `xDensity must not be 0`() {
        assertFailsWith<Exception> {
            APP0(1u, 2u, 1u, 0u, 0u, 1u, 1u)
        }.also { exception ->
            assertTrue("xDensity is not allowed to be 0" in exception.message!!)
        }
    }

    @Test
    fun `yDensity must not be 0`() {
        assertFailsWith<Exception> {
            APP0(1u, 2u, 1u, 1u, 1u, 0u, 0u)
        }.also { exception ->
            assertTrue("yDensity is not allowed to be 0" in exception.message!!)
        }
    }

    @Test
    fun `valid input should not throw`() {
        APP0(1u, 2u, 1u, 1u, 1u, 1u, 1u) // This is a valid input
    }
}