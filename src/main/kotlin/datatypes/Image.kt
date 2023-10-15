package datatypes

import java.io.File
import java.util.stream.Collectors

class Image (var pixels: Array<Array<Pixel>>) {
    val h = pixels.size;
    val w = pixels[0].size;

    fun getPixel(x: Int, y: Int): Pixel{
        if (x in 0..<w && y in 0..<h)
            return pixels[y][x];
        else return Pixel(0,0,0);
    }
    fun setPixel(x: Int, y: Int, pixel: Pixel ){
        if (x in 0..<w && y in 0..<h)
            pixels[y][x] = pixel;
    }

    fun print(){
        for(row in pixels){
            for (pixel in row){
                print(pixel.toString());
            }
            println();
        }
    }



    companion object {
        fun empty(w: Int, h: Int, strideW: Int, strideH: Int ): Image{
            var realW = if (w<=strideW) strideW else w;
            var realH = if (h<=strideH) strideH else h;

            if (realW % strideW != 0) realW = realW - (w % strideW) + strideW
            if (realH % strideH != 0) realH = realH - (h % strideH) + strideH

            var pixels: Array<Array<Pixel>> = Array(realH){ Array(realW){ Pixel() } };
            return Image(pixels);
        }

        fun readFileAsLinesUsingBufferedReader(fileName: String): List<String>
                = File(fileName).bufferedReader().readLines()

        fun readPPM(path: String, strideW: Int, strideH: Int ) : Image{
            var fileContentWithoutComments = readFileAsLinesUsingBufferedReader(path).filterNot { it.startsWith("#") };
            var lines: MutableList<String> = fileContentWithoutComments.stream().skip(1).collect(Collectors.toList());
            val widthHeight = lines.removeFirst().split(" ");
            var picWidth =  widthHeight.get(0).toInt();
            var picHeight = widthHeight.get(1).toInt();

            var result = empty(picWidth, picHeight,6, 6)
            val maxColorVal = lines.removeFirst().toInt();

            for (y in 0..<result.h) {
                var numbers: MutableList<Int>;
                if(y >= picHeight){
                     numbers = Regex("[0-9]+").findAll(lines[lines.size-1])
                        .map(MatchResult::value)
                        .map(String::toInt)
                        .toMutableList()
                }
                else{
                    numbers = Regex("[0-9]+").findAll(lines[y])
                        .map(MatchResult::value)
                        .map(String::toInt)
                        .toMutableList()
                }


                for (x in 0..<result.w) {
                    var r: Int;
                    var g: Int;
                    var b: Int;
                    if(x >= picWidth) {
                        val lastIndex = picWidth - 1
                        r = (numbers[lastIndex * 3] * 255) / maxColorVal ;
                        g = (numbers[(lastIndex * 3) + 1] * 255) / maxColorVal ;
                        b = (numbers[(lastIndex * 3) + 2] * 255) / maxColorVal ;
                    }
                    else{
                        r = (numbers[x * 3] * 255) / maxColorVal ;
                        g = (numbers[(x * 3) + 1] * 255) / maxColorVal ;
                        b = (numbers[(x * 3) + 2] * 255) / maxColorVal ;
                    }

                    var curPixel = Pixel(r,g,b);
                    result.setPixel(x, y, curPixel)
                }
            }

            return result
        }
    }

}