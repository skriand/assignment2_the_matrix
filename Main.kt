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

/**
 * Пример
 *
 * Транспонировать заданную матрицу matrix.
 */
fun <E> transpose(matrix: Matrix<E>): Matrix<E> {
    if (matrix.width < 1 || matrix.height < 1) return matrix
    val result = createMatrix(height = matrix.width, width = matrix.height, e = matrix[0, 0])
    for (i in 0 until matrix.width) {
        for (j in 0 until matrix.height) {
            result[i, j] = matrix[j, i]
        }
    }
    return result
}

fun <E> rotate(matrix: Matrix<E>): Matrix<E> {
    if (matrix.width < 1 || matrix.height < 1) return matrix
    val result = createMatrix(height = matrix.width, width = matrix.height, e = matrix[0, 0])
    for (i in 0 until matrix.width) {
        for (j in 0 until matrix.height) {
            result[i, j] = matrix[matrix.width - j - 1, i]
        }
    }
    return result
}

/**
 * Сложить две заданные матрицы друг с другом.
 * Складывать можно только матрицы совпадающего размера -- в противном случае бросить IllegalArgumentException.
 * При сложении попарно складываются соответствующие элементы матриц
 */
operator fun Matrix<Int>.plus(other: Matrix<Int>): Matrix<Int> {
    if (width != other.width || height != other.height) {
        throw IllegalArgumentException("Out of bounds")
    }
    val result = createMatrix(height, width, 0)
    for (i in 0 until height) {
        for (j in 0 until width) {
            result[i, j] = this[i, j] + other[i, j]
        }
    }
    return result
}

/**
 * Инвертировать заданную матрицу.
 * При инвертировании знак каждого элемента матрицы следует заменить на обратный
 */
operator fun Matrix<Int>.unaryMinus(): Matrix<Int> {
    if (this.width < 1 || this.height < 1) return this
    val result = createMatrix(this.width, this.height, this[0, 0])
    for (i in 0 until this.height) {
        for (j in 0 until this.width) {
            result[i, j] = -this[i, j]
        }
    }
    return result
}

/**
 * Перемножить две заданные матрицы друг с другом.
 * Матрицы можно умножать, только если ширина первой матрицы совпадает с высотой второй матрицы.
 * В противном случае бросить IllegalArgumentException.
 * Подробно про порядок умножения см. статью Википедии "Умножение матриц".
 */
operator fun Matrix<Int>.times(other: Matrix<Int>): Matrix<Int> {
    if (this.width != other.height)
        throw IllegalArgumentException("Matrix can't be multiply")
    val result = createMatrix(this.height, other.width, this[0, 0])
    for (i in 0 until this.height) {
        for (j in 0 until other.width) {
            result[i, j] = 0
            for (k in 0 until this.width)
                result[i, j] += this[i, k]*other[k, j]
        }
    }
    return result
}


/**
 * Целочисленная матрица matrix состоит из "дырок" (на их месте стоит 0) и "кирпичей" (на их месте стоит 1).
 * Найти в этой матрице все ряды и колонки, целиком состоящие из "дырок".
 * Результат вернуть в виде Holes(rows = список дырчатых рядов, columns = список дырчатых колонок).
 * Ряды и колонки нумеруются с нуля. Любой из спискоов rows / columns может оказаться пустым.
 *
 * Пример для матрицы 5 х 4:
 * 1 0 1 0
 * 0 0 1 0
 * 1 0 0 0 ==> результат: Holes(rows = listOf(4), columns = listOf(1, 3)): 4-й ряд, 1-я и 3-я колонки
 * 0 0 1 0
 * 0 0 0 0
 */
fun findHoles(matrix: Matrix<Int>): Holes {
    val rows = mutableListOf<Int>()
    val columns = mutableListOf<Int>()
    var areHoles: Boolean
    for (i in 0 until matrix.height) {
        areHoles = true
        for (j in 0 until matrix.width) {
            if (matrix[i, j] != 0) {
                areHoles = false
                break
            }
        }
        if (areHoles) {
            rows.add(i)
        }
    }
    for (j in 0 until matrix.width) {
        areHoles = true
        for (i in 0 until matrix.height)  {
            if (matrix[i, j] != 0) {
                areHoles = false
                break
            }
        }
        if (areHoles) {
            columns.add(j)
        }
    }
    return Holes(rows, columns)
}

/**
 * Класс для описания местонахождения "дырок" в матрице
 */
data class Holes(val rows: List<Int>, val columns: List<Int>)

/**
 * Даны мозаичные изображения замочной скважины и ключа. Пройдет ли ключ в скважину?
 * То есть даны две матрицы key и lock, key.height <= lock.height, key.width <= lock.width, состоящие из нулей и единиц.
 *
 * Проверить, можно ли наложить матрицу key на матрицу lock (без поворота, разрешается только сдвиг) так,
 * чтобы каждой единице в матрице lock (штырь) соответствовал ноль в матрице key (прорезь),
 * а каждому нулю в матрице lock (дырка) соответствовала, наоборот, единица в матрице key (штырь).
 * Ключ при сдвиге не может выходить за пределы замка.
 *
 * Пример: ключ подойдёт, если его сдвинуть на 1 по ширине
 * lock    key
 * 1 0 1   1 0
 * 0 1 0   0 1
 * 1 1 1
 *
 * Вернуть тройку (Triple) -- (да/нет, требуемый сдвиг по высоте, требуемый сдвиг по ширине).
 * Если наложение невозможно, то первый элемент тройки "нет" и сдвиги могут быть любыми.
 */
fun canOpenLock(key: Matrix<Int>, lock: Matrix<Int>): Triple<Boolean, Int, Int> = TODO()

fun main() {

    println("Создание матрицы")
    val mat = createMatrix(3,3,1)
    mat.set(Cell(0,1),2)
    mat.set(2,2,3)
    println(mat)
     
    println("Транспонирование")
    println(transpose(mat))
        
    println("Поворот")
    println(rotate(mat))
   
    println("Сложение")
    println(mat.plus(rotate(mat)))
   
    println("Инверсия")
    println(mat.unaryMinus())

	println("Умножение")
    println(mat.times(createMatrix(3,3,1)))
   
    println("Дырки")
    val other_mat = createMatrix(3,3,0)
    other_mat.set(0,2,1)
    println(other_mat)
    println(findHoles(other_mat))
}