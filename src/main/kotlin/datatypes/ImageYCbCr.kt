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

        if (sampleLength != firstLineNumSamples || sampleLength != secondLineNumSamples) {
            for (y in 0..<pixels.size - 1 step 2) {
                // get upper and lower row
                val upperRow = pixels[y]
                val lowerRow = pixels[y + 1]
                for (x in 0..<upperRow.size - sampleLength step sampleLength) {
                    val upper: MutableList<YCbCr> = mutableListOf()
                    for (sampleIndex in 0..<sampleLength) {
                        upper.add(upperRow[x + sampleIndex])
                    }

                    val lower: MutableList<YCbCr> = mutableListOf()
                    for (sampleIndex in 0..<sampleLength) {
                        lower.add(lowerRow[x + sampleIndex])
                    }

                    for (sampleIndex in 0..<upper.size step sampleLength / firstLineNumSamples) {
                        val samplePixel = upper[sampleIndex]
                        for (nonSampleIndex in 0..<firstLineNumSamples) {

                            if (sampleLength != firstLineNumSamples) {
                                val toChange = upper[sampleIndex + nonSampleIndex]
                                result.setPixel(x + sampleIndex + nonSampleIndex, y, YCbCr(toChange.y, samplePixel.cb, toChange.cr))
                                if (secondLineNumSamples == 0) {
                                    val toChangeLower = lower[sampleIndex + nonSampleIndex]
                                    result.setPixel(
                                        x + sampleIndex + nonSampleIndex,
                                        y + 1,
                                        YCbCr(toChangeLower.y, samplePixel.cb, toChangeLower.cr)
                                    )
                                }
                            } else {
                                result.setPixel(
                                    x + sampleIndex + nonSampleIndex,
                                    y,
                                    YCbCr(upperRow[x + sampleIndex + nonSampleIndex].y, upperRow[x + sampleIndex + nonSampleIndex].cb, upperRow[x + sampleIndex + nonSampleIndex].cr)
                                )
                                if (secondLineNumSamples == 0) {
                                    result.setPixel(
                                        x + sampleIndex + nonSampleIndex,
                                        y + 1,
                                        YCbCr(upperRow[x + sampleIndex + nonSampleIndex].y, upperRow[x + sampleIndex + nonSampleIndex].cb, upperRow[x + sampleIndex + nonSampleIndex].cr)
                                    )
                                }
                            }
                        }

                    }
                    if (secondLineNumSamples != 0) {
                        for (sampleIndex in 0..<lower.size step sampleLength / secondLineNumSamples) {
                            val samplePixel = lower[sampleIndex]
                            for (nonSampleIndex in 0..<secondLineNumSamples) {
                                val toChange = lower[sampleIndex + nonSampleIndex]
                                result.setPixel(x + sampleIndex + nonSampleIndex, y + 1, YCbCr(toChange.y, samplePixel.cb, toChange.cr))
                            }

                        }
                    }

                }
            }
        }
        else{
            return this;
        }
        return result
    }

    private fun subsampleCr(sampleLength: Int, firstLineNumSamples: Int, secondLineNumSamples: Int):ImageYCbCr {

        val result = empty(w, h)

        if (sampleLength != firstLineNumSamples || sampleLength != secondLineNumSamples) {

            for (y in 0..<pixels.size - 1 step 2) {
                // get upper and lower row
                val upperRow = pixels[y]
                val lowerRow = pixels[y + 1]
                for (x in 0..<upperRow.size - sampleLength step sampleLength) {
                    val upper: MutableList<YCbCr> = mutableListOf()
                    for (sampleIndex in 0..<sampleLength) {
                        upper.add(upperRow[x + sampleIndex])
                    }

                    val lower: MutableList<YCbCr> = mutableListOf()
                    for (sampleIndex in 0..<sampleLength) {
                        lower.add(lowerRow[x + sampleIndex])
                    }

                    for (sampleIndex in 0..<upper.size step sampleLength / firstLineNumSamples) {
                        val samplePixel = upper[sampleIndex]
                        for (nonSampleIndex in 0..<firstLineNumSamples) {
                            if (sampleLength != firstLineNumSamples) {
                                val toChange = upper[sampleIndex + nonSampleIndex]
                                result.setPixel(x + sampleIndex + nonSampleIndex, y, YCbCr(toChange.y, toChange.cb, samplePixel.cr))
                                if (secondLineNumSamples == 0) {
                                    val toChangeLower = lower[sampleIndex + nonSampleIndex]
                                    result.setPixel(
                                        x + sampleIndex + nonSampleIndex,
                                        y + 1,
                                        YCbCr(toChangeLower.y, toChangeLower.cb, samplePixel.cr)
                                    )
                                }
                            } else {
                                result.setPixel(
                                    x + sampleIndex + nonSampleIndex,
                                    y,
                                    YCbCr(upperRow[x + sampleIndex + nonSampleIndex].y, upperRow[x + sampleIndex + nonSampleIndex].cb, upperRow[x + sampleIndex + nonSampleIndex].cr)
                                )
                                if (secondLineNumSamples == 0) {
                                    result.setPixel(
                                        x + sampleIndex + nonSampleIndex,
                                        y + 1,
                                        YCbCr(upperRow[x + sampleIndex + nonSampleIndex].y, upperRow[x + sampleIndex + nonSampleIndex].cb, upperRow[x + sampleIndex + nonSampleIndex].cr)
                                    )
                                }
                            }
                        }
                    }

                    if (secondLineNumSamples != 0) {
                        for (sampleIndex in 0..<lower.size step sampleLength / secondLineNumSamples) {
                            val samplePixel = lower[sampleIndex]
                            for (nonSampleIndex in 0..<secondLineNumSamples) {
                                val toChange = lower[sampleIndex + nonSampleIndex]
                                result.setPixel(x + sampleIndex + nonSampleIndex, y + 1, YCbCr(toChange.y, toChange.cb, samplePixel.cr))
                            }

                        }
                    }

                }
            }
        }
        else {
            return this;
        }
        return result
    }

    fun subsample(sampleLength: Int, firstLineNumSamples: Int, secondLineNumSamples: Int):ImageYCbCr {
        return if (sampleLength != firstLineNumSamples || sampleLength != secondLineNumSamples) {
            val subsampleCb = subsampleCb(sampleLength, firstLineNumSamples, secondLineNumSamples)
            subsampleCb.subsampleCr(sampleLength, firstLineNumSamples, secondLineNumSamples)
        } else {
            this;
        }
    }

    fun getChannel(channel: Int): Channel{
        var result = Channel(w, h)
        for (x in 0..<w) {
            for (y in 0..<h) {
                val color = getPixel(x, y)
                val channelValue =
                    if (channel == 0) color.y
                    else if(channel == 1) color.cb
                    else if(channel == 2) color.cr
                    else throw Exception("getChannel only supports channels 0,1,2")
                result.setValue(x, y, channelValue.toDouble())
            }
        }
        return result

    }

        companion object{
        fun empty(w: Int, h: Int): ImageYCbCr{
            val pixels: Array<Array<YCbCr>> = Array(h){ Array(w){ YCbCr() } }
            return ImageYCbCr(pixels)
        }
    }
}