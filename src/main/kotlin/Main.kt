import datatypes.ImageRGB

fun main(args: Array<String>) {
    var image = ImageRGB.readPPM("src/main/image.ppm", 6, 6)
    image.print()
    println()
    val yCbCrImage = image.toYCbCr();
    yCbCrImage.print()
}