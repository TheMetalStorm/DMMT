package datatypes

import java.io.File
import java.util.stream.Collectors
import java.util.stream.Collectors.toList

class ImageRGB (var pixels: Array<Array<RGB>>) {
    val h = pixels.size
    val w = pixels[0].size

    fun getPixel(x: Int, y: Int): RGB{
        return if (x in 0..<w && y in 0..<h)
            pixels[y][x]
        else RGB(0,0,0)
    }
    fun setPixel(x: Int, y: Int, pixel: RGB ){
        if (x in 0..<w && y in 0..<h)
            pixels[y][x] = pixel
    }

    fun print(){
        for(row in pixels){
            for (pixel in row){
                print(pixel.toString())
            }
            println()
        }
    }

    fun toYCbCr(): ImageYCbCr {
        val result = ImageYCbCr.empty(w, h)
        for ((y, row) in pixels.withIndex()) {
            for((x, pixel) in row.withIndex()){
                val toYCbCr = pixel.toYCbCr()
                result.setPixel(x, y, toYCbCr)
            }
        }
        return result
    }


    companion object {
        fun empty(w: Int, h: Int, strideW: Int, strideH: Int ): ImageRGB{
            var realW = if (w<=strideW) strideW else w
            var realH = if (h<=strideH) strideH else h

            if (realW % strideW != 0) realW = realW - (w % strideW) + strideW
            if (realH % strideH != 0) realH = realH - (h % strideH) + strideH

            val pixels: Array<Array<RGB>> = Array(realH){ Array(realW){ RGB() } }
            return ImageRGB(pixels)
        }

        fun readFileAsLinesUsingBufferedReader(fileName: String): List<String>
                = File(fileName).bufferedReader().readLines()

        fun readPPM(path: String, strideW: Int, strideH: Int ) : ImageRGB{
            val lines: MutableList<String> = readFileAsLinesUsingBufferedReader(path).filterNot { it.startsWith("#") }
                .stream()
                .skip(1)
                .collect(toList());

            val widthHeight = lines.removeFirst().split(" ")
            val picWidth =  widthHeight[0].toInt()
            val picHeight = widthHeight[1].toInt()

            val result = empty(picWidth, picHeight, strideW, strideH)
            val maxColorVal = lines.removeFirst().toInt()

            for (y in 0..<result.h) {
                val numbers: MutableList<Int> = if(y >= picHeight){
                    Regex("[0-9]+").findAll(lines[lines.size-1])
                        .map(MatchResult::value)
                        .map(String::toInt)
                        .toMutableList()
                } else{
                    Regex("[0-9]+").findAll(lines[y])
                        .map(MatchResult::value)
                        .map(String::toInt)
                        .toMutableList()
                }


                for (x in 0..<result.w) {
                    var r: Int
                    var g: Int
                    var b: Int
                    if(x >= picWidth) {
                        val lastIndex = picWidth - 1
                        r = (numbers[lastIndex * 3] * 255) / maxColorVal
                        g = (numbers[(lastIndex * 3) + 1] * 255) / maxColorVal
                        b = (numbers[(lastIndex * 3) + 2] * 255) / maxColorVal
                    }
                    else{
                        r = (numbers[x * 3] * 255) / maxColorVal
                        g = (numbers[(x * 3) + 1] * 255) / maxColorVal
                        b = (numbers[(x * 3) + 2] * 255) / maxColorVal
                    }

                    val curPixel = RGB(r,g,b)
                    result.setPixel(x, y, curPixel)
                }
            }

            return result
        }
    }

}