package datatypes

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

            if (realW % strideW != 0) realW = realW - (w % strideW) +strideW;
            if (realH % strideH != 0) realH = realH - (h % strideH) + strideH;

            var pixels: Array<Array<Pixel>> = Array(realH){ Array(realW){ Pixel() } };
            return Image(pixels);
        }

//        fun readPPM(path: String, strideW: Int, strideH: Int ): Image{
//
//        }
    }

}