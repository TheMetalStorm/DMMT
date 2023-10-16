import datatypes.ImageRGB

fun main() {
    val image = ImageRGB.readPPM("src/main/image.ppm", 8, 8)
    val yCbCrImage = image.toYCbCr()
    val subsampleCbCr = yCbCrImage.subsample(4, 2, 0)
    println("orig")
    yCbCrImage.print()
    println("subsampled cb")
    subsampleCbCr.print()
}