import datatypes.ImageRGB

fun main() {
    val image = ImageRGB.readPPM("src/main/image.ppm", 8, 8)
    val yCbCrImage = image.toYCbCr()
    val subsampleCbCr = yCbCrImage.subsample(4, 4, 4)
    println("orig")
    yCbCrImage.print()
    println("subsampled cb cr")
    subsampleCbCr.print()
}