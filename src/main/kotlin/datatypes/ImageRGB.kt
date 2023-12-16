package datatypes

import java.io.File
import java.lang.Exception
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

    fun writePPM(name: String){
        val stream: BitStream = BitStream()


        val header = "P3\r\n${w} ${h}\r\n255\r\n"
        //header to array of UByte

        //
        for (uByte in header.toByteArray().toUByteArray()) {
            stream.addByteToStream(uByte)
        }

        for (y in 0..<h) {
            for (x in 0..<w) {


                getPixel(x,y).r.toString().toByteArray().toUByteArray().forEach {
                    stream.addByteToStream(it)
                }
                stream.addByteToStream(0x20u)
                getPixel(x,y).g.toString().toByteArray().toUByteArray().forEach {
                    stream.addByteToStream(it)
                }
                stream.addByteToStream(0x20u)
                getPixel(x,y).b.toString().toByteArray().toUByteArray().forEach {
                    stream.addByteToStream(it)
                }
                stream.addByteToStream( 0x20u)


            }
            val nl = "\r\n"

            for (uByte in nl.toByteArray().toUByteArray()) {
                stream.addByteToStream(uByte)
            }


        }
        stream.saveToFileAsBytes(name)

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

    fun getChannel(channel: Int): Channel{
        var result = Channel(w, h)
        for (x in 0..<w) {
            for (y in 0..<h) {
                val color = getPixel(x, y)
                val channelValue =
                    if (channel == 0) color.r
                    else if(channel == 1) color.g
                    else if(channel == 2) color.b
                    else throw Exception("getChannel only supports channels 0,1,2")
                result.setValue(x, y, channelValue.toDouble())
            }
        }
        return result
    }
}