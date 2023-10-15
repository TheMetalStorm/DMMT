package datatypes

class ImageYCbCr(var pixels: Array<Array<YCbCr>>) {
    val h = pixels.size;
    val w = pixels[0].size;

    fun getPixel(x: Int, y: Int): YCbCr{
        if (x in 0..<w && y in 0..<h)
            return pixels[y][x];
        else return YCbCr();
    }
    fun setPixel(x: Int, y: Int, pixel: YCbCr ){
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

    companion object{
        fun empty(w: Int, h: Int): ImageYCbCr{
            var pixels: Array<Array<YCbCr>> = Array(h){ Array(w){ YCbCr() } };
            return ImageYCbCr(pixels);
        }
    }
}