package datatypes

data class Channel (val width: Int, val height: Int) {
    val data: Array<Array<Double>> = Array(height){ Array(width){ 0.0}}

    fun getValue(x: Int, y: Int): Double{
        return if (x in 0..<width && y in 0..<height)
            data[y][x]
        else Double.NaN
    }
    fun setValue(x: Int, y: Int, value: Double){
        if (x in 0..<width && y in 0..<height)
            data[y][x] = value
    }

    fun print(){
        for(row in data){
            for (pixel in row){
                print("${pixel} ")
            }
            println()
        }
    }

}