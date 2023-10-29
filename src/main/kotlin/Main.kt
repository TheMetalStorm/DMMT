import datatypes.BitStream

fun main() {
//    val image = ImageRGB.readPPM("src/main/image.ppm", 8, 8)
//    val yCbCrImage = image.toYCbCr()
//    val subsampleCbCr = yCbCrImage.subsample(4, 4, 4)
//    println("orig")
//    yCbCrImage.print()
//    println("subsampled cb cr")
//    subsampleCbCr.print()

    val bitstream : BitStream = BitStream()
    val listToAdd : ArrayList<Int> = arrayListOf(0,1,0,1,1)
    bitstream.addToList(listToAdd)
    bitstream.addToList(arrayListOf(0,1,1,1,0,0))
    bitstream.addToList(arrayListOf(0,1,1,1,0,0))
    bitstream.print()
    bitstream.getByte()
    bitstream.saveToFile("Aufgabe2b")
}