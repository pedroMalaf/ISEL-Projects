import isel.leic.tds.checkers.model.*
import kotlin.test.*

class TestColumn {
    @Test
    fun `Build a Column by index and check the properties`() {
        val index = 2
        val sut: Column = Column(index)
        assertEquals('c', sut.symbol)
        assertEquals(index, sut.index)
    }

    @Test
    fun `Build a Column with invalid index throws exception`() {
        val index = BOARD_DIM
        val ex = assertFailsWith<IllegalArgumentException> { Column(index) }
        assertEquals("Invalid column index: $index", ex.message)
    }

    @Test
    fun `Symbol to Column conversion with check`() {
        val sut: Column? = 'c'.toColumnOrNull()
        assertNotNull(sut)
        assertEquals(2, sut.index)
        assertNull('@'.toColumnOrNull())
    }

    @Test
    fun `All valid symbols to columns checked conversion`() {
        assertEquals(
            List(BOARD_DIM) { it },
            ('A'..'z').mapNotNull { it.toColumnOrNull()?.index }
        )
    }

    @Test
    fun `Get all valid values of Column`() {
        assertEquals(BOARD_DIM, Column.values.size)
        assertEquals(List(BOARD_DIM) { 'a' + it }, Column.values.map { it.symbol })
    }

    @Test
    fun `All invalid columns`() {
        val invalidChars = (0..255).map { it.toChar() } - ('a'..<('a' + BOARD_DIM))
        val invalidColumns = invalidChars.mapNotNull { it.toColumnOrNull() }
        assertEquals(0, invalidColumns.size)
    }

    @Test
    fun `Equality between columns`() {
        val column = Column(0)
        val c = 'a'.toColumnOrNull()
        assertNotNull(c)
        assertEquals(column, c)
        assertEquals(column.hashCode(), c.hashCode())
    }

    @Test
    fun `Column must be a value class`() {
        val sut = Column(0)
        val ref: Any = sut
        assertNotSame(ref, sut)
    }

}
