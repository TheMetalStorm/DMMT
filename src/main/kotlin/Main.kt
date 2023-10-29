import datatypes.BitStream
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
//    val image = ImageRGB.readPPM("src/main/image.ppm", 8, 8)
//    val yCbCrImage = image.toYCbCr()
//    val subsampleCbCr = yCbCrImage.subsample(4, 4, 4)
//    println("orig")
//    yCbCrImage.print()
//    println("subsampled cb cr")
//    subsampleCbCr.print()

    val bitstream : BitStream = BitStream()

    val time = measureTimeMillis  {
        val iterations = 10000000;
        for (i in 0..<iterations){
            bitstream.addToList(1);
        }

        for (i in 0..<iterations){
            var read = bitstream.getBit(i)

        }
    }
    print("Time (ms): " + time)

//    bitstream.saveToFile("Aufgabe2b")
}