import JPGSegments.APP0
import JPGSegments.SOF0
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

//    val time = measureTimeMillis  {
//        val iterations = 10000000;
//        for (i in 0..<iterations){
//            bitstream.addToList(1);
//        }
//
//        for (i in 0..<iterations){
//            var read = bitstream.getBit(i)
//
//        }
//    }
//    print("Time (ms): " + time)

    //APP0
   // bitstream.addByteToStream(UByte.)

//    bitstream.saveToFile("Aufgabe2b")
    //LINK TO SPEC-> https://elearning.thws.de/pluginfile.php/2033910/mod_resource/content/0/JPG-Spezifikation.pdf
    //VORSCHLAG KONTSTRUCTOREN für Segmente und mithilfe von Segmenten das JPG objekt zusammen bauen JPG(SEGMENT APP0, SEGMENT SOFO, etc.)

    //helpful: https://mykb.cipindanci.com/archive/SuperKB/1294/JPEG%20File%20Layout%20and%20Format.htm
    val app0 = APP0(1.toUByte(), 1.toUByte(), 0.toUByte(), 0.toUByte(), 0x48.toUByte(), 0.toUByte(), 0x48.toUByte())
    val sof0 = SOF0(8.toUByte(), 0.toUByte(), 16.toUByte(), 0.toUByte(), 16.toUByte(), 1.toUByte(), arrayListOf(0x01.toUByte(), 0x22.toUByte(),
   0.toUByte()))

    //SOI
    bitstream.addByteToStream(arrayListOf(0xff.toUByte(), 0xd8.toUByte()))

//    //APP0
    bitstream.addBitStream(app0.getBitStream());

//    //SOF0
    bitstream.addBitStream(sof0.getBitStream())

//    //EOI
    bitstream.addByteToStream(arrayListOf(0xff.toUByte(), 0xd9.toUByte()))

    bitstream.printBits()

    bitstream.saveToFileAsBytes("test.jpeg")
}