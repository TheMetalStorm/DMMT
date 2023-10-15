import datatypes.ImageRGB

fun main(args: Array<String>) {
    var image = ImageRGB.readPPM("src/main/image.ppm", 6, 6)
    image.print()
    println()
    val yCbCrImage = image.toYCbCr();
    yCbCrImage.subsampleCb(4,2,2);
//    yCbCrImage.subsampleCr(4,2,2)
}