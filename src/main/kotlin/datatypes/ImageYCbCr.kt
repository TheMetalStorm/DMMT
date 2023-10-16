package datatypes

class ImageYCbCr(var pixels: Array<Array<YCbCr>>) {
    val h = pixels.size
    val w = pixels[0].size

    fun getPixel(x: Int, y: Int): YCbCr{
        return if (x in 0..<w && y in 0..<h)
            pixels[y][x]
        else YCbCr()
    }
    fun setPixel(x: Int, y: Int, pixel: YCbCr ){
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

    private fun subsampleCb(sampleLength: Int, firstLineNumSamples: Int, secondLineNumSamples: Int):ImageYCbCr {

        val result = empty(w, h)

        for (y in 0..<pixels.size-1 step 2) {
            // get upper and lower row
            val upperRow = pixels[y]
            val lowerRow = pixels[y+1]
            for (x in 0..<upperRow.size-sampleLength step sampleLength) {
                val upper: MutableList<YCbCr> = mutableListOf()
                for (z in 0..<sampleLength){
                    upper.add(upperRow[x+z])
                }

                val lower: MutableList<YCbCr> = mutableListOf()
                for (z in 0..<sampleLength){
                    lower.add(lowerRow[x+z])
                }

                for(z in 0..<upper.size step sampleLength/firstLineNumSamples){
                    val samplePixel = upper[z]
                    for (h in  0..<firstLineNumSamples) {
                        val toChange = upper[z+h]
                        result.setPixel(x+z+h, y, YCbCr(toChange.y, samplePixel.cb, toChange.cr))
                        if(secondLineNumSamples==0){
                            val toChangeLower = lower[z+h]
                            result.setPixel(x+z+h, y+1, YCbCr(toChangeLower.y, samplePixel.cb, toChangeLower.cr))
                        }
                    }
                }

                if(secondLineNumSamples != 0){
                    for(z in 0..<lower.size step sampleLength/secondLineNumSamples){
                        val samplePixel = lower[z]
                        for (h in  0..<secondLineNumSamples) {
                            val toChange = lower[z+h]
                            result.setPixel(x+z+h, y+1, YCbCr(toChange.y, samplePixel.cb, toChange.cr))
                        }

                    }
                }

            }
        }
        return result
    }

    private fun subsampleCr(sampleLength: Int, firstLineNumSamples: Int, secondLineNumSamples: Int):ImageYCbCr {

        val result = empty(w, h)

        for (y in 0..<pixels.size-1 step 2) {
            // get upper and lower row
            val upperRow = pixels[y]
            val lowerRow = pixels[y+1]
            for (x in 0..<upperRow.size-sampleLength step sampleLength) {
                val upper: MutableList<YCbCr> = mutableListOf()
                for (z in 0..<sampleLength){
                    upper.add(upperRow[x+z])
                }

                val lower: MutableList<YCbCr> = mutableListOf()
                for (z in 0..<sampleLength){
                    lower.add(lowerRow[x+z])
                }

                for(z in 0..<upper.size step sampleLength/firstLineNumSamples){
                    val samplePixel = upper[z]
                    for (h in  0..<firstLineNumSamples) {
                        val toChange = upper[z+h]
                        result.setPixel(x+z+h, y, YCbCr(toChange.y, toChange.cb, samplePixel.cr))
                        if(secondLineNumSamples==0){
                            val toChangeLower = lower[z+h]
                            result.setPixel(x+z+h, y+1, YCbCr(toChangeLower.y, toChangeLower.cb, samplePixel.cr))
                        }
                    }
                }

                if(secondLineNumSamples != 0){
                    for(z in 0..<lower.size step sampleLength/secondLineNumSamples){
                        val samplePixel = lower[z]
                        for (h in  0..<secondLineNumSamples) {
                            val toChange = lower[z+h]
                            result.setPixel(x+z+h, y+1, YCbCr(toChange.y, toChange.cb, samplePixel.cr))
                        }

                    }
                }

            }
        }
        return result
    }

    fun subsample(sampleLength: Int, firstLineNumSamples: Int, secondLineNumSamples: Int):ImageYCbCr {
        val subsampleCb = subsampleCb(sampleLength, firstLineNumSamples, secondLineNumSamples)
        return subsampleCb.subsampleCr(sampleLength, firstLineNumSamples, secondLineNumSamples)

    }

        companion object{
        fun empty(w: Int, h: Int): ImageYCbCr{
            val pixels: Array<Array<YCbCr>> = Array(h){ Array(w){ YCbCr() } }
            return ImageYCbCr(pixels)
        }
    }
}