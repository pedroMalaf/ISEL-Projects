import isel.leic.tds.checkers.model.*
import kotlin.test.*

class TestSquare {

    @Test
    fun `Dimension limits`() {
        assert(BOARD_DIM in 2..8 step 2) { "BOARD_DIM must be a pair in 2..8" }
    }

    @Test
    fun `Build a Square and check properties`() {
        assertTrue(BOARD_DIM > 2)
        val row = Row(BOARD_DIM - 2)
        val col = Column(2)
        val sut: Square = Square(row, col)
        assertEquals(row.index * BOARD_DIM + col.index % BOARD_DIM, sut.index)
    }

    @Test
    fun `Create a black square and convert to string`() {
        assertTrue(BOARD_DIM > 2)
        val square = Square(Row(1), Column(2))
        assertTrue(square.black)
        assertEquals("${BOARD_DIM - 1}${'a' + 2}", square.toString())
    }

    @Test
    fun `String to a white square and use index values`() {
        assertTrue(BOARD_DIM > 3)
        val square = "3d".toSquareOrNull()
        assertNotNull(square)
        assertEquals(3, square.column.index)
        assertEquals(BOARD_DIM - 3, square.row.index)
        assertFalse(square.black)
    }

    @Test
    fun `Invalid string to Square results null`() {
        assertNull("b3b".toSquareOrNull())
        assertNull("b3".toSquareOrNull())
        assertNull("3$".toSquareOrNull())
        assertNull("${BOARD_DIM + 1}a".toSquareOrNull())
        assertNull("1${'a' + BOARD_DIM}".toSquareOrNull())
    }

    @Test
    fun `Invalid string to Square throws exception`() {
        assertFailsWith<IllegalArgumentException> { "b3".toSquare() }
        assertFailsWith<IllegalArgumentException> { "3$".toSquare() }
    }

    @Test
    fun `All valid squares`(){
        val all = Square.values
        assertEquals(BOARD_DIM * BOARD_DIM, all.size)
        val upperLeft = all.first()
        assertEquals("${BOARD_DIM}a", upperLeft.toString())
        assertEquals(0, upperLeft.index)
        val lowerRight = all.last()
        assertEquals("1${'a'+ BOARD_DIM - 1}", lowerRight.toString())
        assertEquals(BOARD_DIM * BOARD_DIM - 1, lowerRight.index)
    }

    @Test
    fun `Equality between squares`(){
        val row = assertNotNull('3'.toRowOrNull())
        val col = assertNotNull('c'.toColumnOrNull())
        val square = assertNotNull("3c".toSquareOrNull())
        val s = Square(row, col)
        assertEquals(square, s)
        assertEquals(square.hashCode(), s.hashCode())
        assertSame(square, s)
    }
}