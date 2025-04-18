import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.model.BOARD_DIM
import kotlin.test.*

class TestRow {

    @Test
    fun `Build a Row by index and check properties`() {
        val index = BOARD_DIM - 2
        val sut: Row = Row(index)
        assertEquals('2', sut.digit)
        assertEquals(index, sut.index)
    }

    @Test
    fun `Build a Row with invalid index throws exception`() {
        val index = BOARD_DIM
        val ex = assertFailsWith<IllegalArgumentException> { Row(index) }
        assertEquals("Invalid row index: $index", ex.message)
    }

    @Test
    fun `Digit to Row conversion with check`() {
        val sut: Row? = '3'.toRowOrNull()
        assertNotNull(sut)
        assertEquals(BOARD_DIM - 3, sut.index)
        assertNull('0'.toRowOrNull())
    }

    @Test
    fun `All valid digits to rows`() {
        assertEquals(
            (BOARD_DIM - 1 downTo 0).toList(),
            ('1'..<'1' + BOARD_DIM).mapNotNull { it.toRowOrNull()?.index }
        )
    }

    @Test
    fun `Get all valid values of Row`() {
        assertEquals(BOARD_DIM, Row.values.size)
        assertEquals(List(BOARD_DIM) { '1' + BOARD_DIM - it - 1 }, Row.values.map { it.digit })
    }

    @Test
    fun `All invalid rows`() {
        val invalidNumbers = (0..255).map { it.toChar() } - ('1'..<'1' + BOARD_DIM)
        val invalidRows = invalidNumbers.mapNotNull { it.toRowOrNull() }
        assertEquals(0, invalidRows.size)
    }

    @Test
    fun `Equality between rows`(){
        val row = Row(BOARD_DIM - 1)
        val r = '1'.toRowOrNull()
        assertNotNull(r)
        assertEquals(row, r)
        assertEquals(row.hashCode(), r.hashCode())
    }

    @Test
    fun `Row must be a value class`(){
        val sut = Row(0)
        val ref: Any = sut
        assertNotSame(ref, sut)
    }
}