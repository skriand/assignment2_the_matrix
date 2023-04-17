@file:Suppress("UNUSED_PARAMETER")
package mmcs.assignment2

/**
 * Ячейка матрицы: row = ряд, column = колонка
 */
data class Cell(val row: Int, val column: Int)

/**
 * Интерфейс, описывающий возможности матрицы. E = тип элемента матрицы
 */
interface Matrix<E> {
    /** Высота */
    val height: Int

    /** Ширина */
    val width: Int

    /**
     * Доступ к ячейке.
     * Методы могут бросить исключение, если ячейка не существует или пуста
     */
    operator fun get(row: Int, column: Int): E

    operator fun get(cell: Cell): E

    /**
     * Запись в ячейку.
     * Методы могут бросить исключение, если ячейка не существует
     */
    operator fun set(row: Int, column: Int, value: E)

    operator fun set(cell: Cell, value: E)
}

/**
 * Метод для создания матрицы, должен вернуть РЕАЛИЗАЦИЮ Matrix<E>.
 * height = высота, width = ширина, e = чем заполнить элементы.
 * Бросить исключение IllegalArgumentException, если height или width <= 0.
 */
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> {
    if (height <= 0 || width <= 0 )
        throw IllegalArgumentException("Matrix can't be create")
    var res = MatrixImpl<E>(height, width, e)

    return res
}

/**
 * Реализация интерфейса "матрица"
 */

@Suppress("EqualsOrHashCode")
class MatrixImpl<E>(override val height: Int, override val width: Int, private val e: MutableList<E>) : Matrix<E> {

    constructor(height: Int, width: Int, e: E) : this(height, width, MutableList(height * width) { e })

    override fun get(row: Int, column: Int): E =
        e[row*height + column] ?: throw IllegalArgumentException("Out of bounds")

    override fun get(cell: Cell): E = get(cell.row, cell.column)

    override fun set(row: Int, column: Int, value: E) {
        if (column > width - 1 || row > height - 1)
            throw IllegalArgumentException("Out of bounds")
        e[row*height + column] = value
    }

    override fun set(cell: Cell, value: E) = set(cell.row, cell.column, value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is MatrixImpl<*>) return false
        if (height != other.height || width != other.width)
            return false
        for (row in 0 until height) {
            for (column in 0 until width) {
                if (get(row, column) != other[row, column])
                    return false
            }
        }

        return true
    }

    override fun toString(): String {
        var res = StringBuilder()
        for (row in 0 until height) {
            for (column in 0 until width) {
                res.append(get(row, column))
                res.append(" ")
            }
            if (row != height - 1)
                res.append("\n")
        }
        return res.toString()
    }
}