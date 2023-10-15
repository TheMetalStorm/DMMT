import datatypes.Image
import datatypes.Pixel

fun main(args: Array<String>) {
    var image: Image = Image.empty(7, 17, 16, 16)
    var im = Image.readPPM("src/main/image.ppm", 2, 2)
    im.print()
}