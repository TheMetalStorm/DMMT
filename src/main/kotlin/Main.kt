import datatypes.Image
import datatypes.Pixel

fun main(args: Array<String>) {
    var image: Image = Image.empty(7, 17, 16, 16)
    image.setPixel(0,1, Pixel(1,2,3))
    image.print()

}