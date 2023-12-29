import datatypes.ImageRGB
import DCT.DCT
import JPGSegments.APP0
import JPGSegments.DHT
import JPGSegments.DQT
import JPGSegments.SOF0
import datatypes.BitStream
import org.ejml.simple.SimpleMatrix
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
//    val image = ImageRGB.readPPM("src/main/image.ppm", 8, 8)
//    val yCbCrImage = image.toYCbCr()
//    val subsampleCbCr = yCbCrImage.subsample(4, 4, 4)
//    println("orig")
//    yCbCrImage.print()
//    println("subsampled cb cr")
//    subsampleCbCr.print()

//    val bitstream : BitStream = BitStream()

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

//    //helpful: https://mykb.cipindanci.com/archive/SuperKB/1294/JPEG%20File%20Layout%20and%20Format.htm
//    val app0 = APP0(1u, 1u, 0u, 0u, 0x48u, 0u, 0x48u)
//    val sof0 = SOF0(8u, 0u, 16u, 0u, 16u, 1u,
//        arrayListOf(0x01u, 0x22u, 0u))
//
//    val huffman = Huffman()
//    val originalMessage = "ufog5JxQIQSqsyFnC09yEjMFCMuyVaqGszj3lGWKVFOQvkS6c4WJvH1rGgfyCC1MCdwx5dmybnv985Llu3sx7YcGkmbFtXNGtGrwn6dp6mzdDgZW6DzMDqqfe8zkEyHurLMApvtMsi5nBGuuUuZSsvZeZ6HXomW6lZLF76SjZqh2Fv3mwqidWvwkUaxdlX4Jl0KuLD97ZLGjnwlRF1PfAGjMbhw6XkJlDbZbLke31cl0seJZqClUBIefuWuhND7QXAFJKnS9x12ouBNTEWbErsq4PH40wOg3aoUJ4U6vi2WmZ9x5medUwKxNe7nNIC0pzqAckSbQrlTZvAFZ3zsOrH2Ij1HkBfgCYueLiL1MaojChgA6amwnAnKZL7lTPlPZBFSlAUcprFOQmneUZ74u2UHpZz3Ld24qtOJZ3VuAHg1KHR7pt87ULupxYClydSTOFJNyUWZA2vveHk4NCAUSazIUTK0Mdxyi5D6jpz68V8XbJbu0rFCC"
////    val originalMessage = "AAAAABBBBBBBBCCCDDDEEEEEEEFFFFFFFFFFFFFFGGG"
//    val (encodedMessage, symbolToCodeMap) = huffman.encode(originalMessage.toCharArray().map { it.code }.toIntArray())

//    for (symbol in symbolToCodeMap) {
//        val c = symbol.key
//        val bs = symbol.value
//        println("Symbol: $c")
//        bs.printBits()
//        println(bs)
//        println()
//    }

//    encodedMessage.printBits()
//    println()
//    val array = huffman.decode(HufEncode(encodedMessage, symbolToCodeMap))
//    print("Original: ")
//    println(originalMessage)
//    print("Decoded:  ")
//    for (i in array) {
//        print(Char(i))
//    }
//
//    val dht = DHT(symbolToCodeMap, 0, 0)
//    val dht2 = DHT(symbolToCodeMap, 1, 1)
//
////    //SOI
//    bitstream.addByteToStream(arrayListOf(0xffu, 0xd8u))
////
////    //APP0
//    bitstream.addBitStream(app0.getBitStream());
////
////    //SOF0
//    bitstream.addBitStream(sof0.getBitStream())
////
//    //DHT
//    bitstream.addBitStream(dht.getBitStream())
//    bitstream.addBitStream(dht2.getBitStream())
//
//
////    //EOI
//    bitstream.addByteToStream(arrayListOf(0xffu, 0xd9u))
////
////    bitstream.printBits()
////
//    bitstream.saveToFileAsBytes("test.jpeg")
//    println()


//
//    var dct2 = DCT.araiDct2D8x8(redChannel)
//    dct2.print()
//    println()
//





//
//    val image = ImageRGB.readPPM("src/main/kotlin/DCT/red.ppm", 8, 8)
//    val redChannel = image.getChannel(0)
//
//    redChannel.print()
//    val dct = DCT.araiDCT(redChannel)
//    redChannel.print()
//    dct.print()

//    println("Reading Image Data (3840 x 2160)...")
//    println()
//
//    val imageSrc:String = if (args.isEmpty())  "./dct4c.ppm"
//                else args[0]
//    val image = ImageRGB.readPPM(imageSrc, 8, 8)
//    val redChannel = image.getChannel(0)
//
////    //directDCT Test
//    println("Running direct DCT...")
//    var directIterations = 0
//    var smallest = Long.MAX_VALUE
//    var time: Long = 0
//    for (i in 1..10000) {
//        val once = measureTimeMillis {
//            val a = redChannel
//            DCT.directDCT(a)
//        }
//        if(once < smallest) smallest = once
//        directIterations += 1
//        time += once
//        if (time > 10000) break
//    }
//
//    println("Time directDCT x ${directIterations}: $time ms")
//    println("Time directDCT fastest iteration: $smallest ms")
//    println()
//
//    println("Running seperate DCT...")
////
////    //seperateDCT Test
//    var seperateIterations = 0
//    smallest = Long.MAX_VALUE
//    time = 0
//    for (i in 1..10000) {
//        val once = measureTimeMillis {
//            val a = redChannel
//            DCT.seperateDCT(a)
//        }
//        if(once < smallest) smallest = once
//        seperateIterations += 1
//        time += once
//        if (time > 10000) break
//    }
//
//    println("Time seperateDCT x ${seperateIterations}: $time ms")
//    println("Time seperateDCT fastest iteration: $smallest ms")
//    println()
//
//    println("Running arai DCT...")
//    var araiIterations = 0
//    smallest = Long.MAX_VALUE
//    time = 0
//    for (i in 1..10000) {
//        val once = measureTimeMillis {
//            val a = redChannel
//            DCT.araiDCT(a)
//        }
//        if(once < smallest) smallest = once
//        araiIterations += 1
//        time += once
//        if (time > 10000) break
//    }
//    println("Time araiDCT x ${araiIterations}: $time ms")
//    println("Time araiDCT fastest iteration: $smallest ms")
//    println()
//
//    println("Done!")

//    val im = ImageRGB.empty(3840, 2160, 8, 8)
//    for (y in 0..im.h) {
//        for (x in 0..im.w) {
//            im.setPixel(x, y, RGB(((x+y*8) % 256), 0, 0))
//        }
//    }
//    im.writePPM("dct4c.ppm")

//    val testMatrix = SimpleMatrix( 8, 8, true,
//        581.0, -144.0, 56.0, 17.0, 15.0, -7.0, 25.0, -9.0,
//        -242.0, 133.0, -48.0, 42.0, -2.0, -7.0, 13.0, -4.0,
//        108.0, -18.0, -40.0, 71.0, -33.0, 12.0, 6.0, -10.0,
//        -56.0, -93.0, 48.0, 19.0, -8.0, 7.0, 6.0, -2.0,
//        -17.0, 9.0, 7.0, -23.0, -3.0, -10.0, 5.0, 3.0,
//        4.0, 9.0, -4.0, -5.0, 2.0, 2.0, -7.0, 3.0,
//        -9.0, 7.0, 8.0, -6.0, 5.0, 12.0, 2.0, -5.0,
//        -9.0, -4.0, -2.0, -3.0, 6.0, 1.0, -1.0, -1.0,
//    )
//
//    DCT.quantize(testMatrix, DCT.quantMatrix50()).print()


    makeTestJPEG()
}

fun makeTestJPEG(){

    val bitstream : BitStream = BitStream()

    val app0 = APP0(1u, 1u, 0u, 0u, 0x48u, 0u, 0x48u)
    val sof0 = SOF0(8u, 0u, 16u, 0u, 16u, 1u,
        arrayListOf(0x01u, 0x22u, 0u))

    val huffman = Huffman()

    val originalMessage = "ufog5JxQIQSqsyFnC09yEjMFCMuyVaqGszj3lGWKVFOQvkS6c4WJvH1rGgfyCC1MCdwx5dmybnv985Llu3sx7YcGkmbFtXNGtGrwn6dp6mzdDgZW6DzMDqqfe8zkEyHurLMApvtMsi5nBGuuUuZSsvZeZ6HXomW6lZLF76SjZqh2Fv3mwqidWvwkUaxdlX4Jl0KuLD97ZLGjnwlRF1PfAGjMbhw6XkJlDbZbLke31cl0seJZqClUBIefuWuhND7QXAFJKnS9x12ouBNTEWbErsq4PH40wOg3aoUJ4U6vi2WmZ9x5medUwKxNe7nNIC0pzqAckSbQrlTZvAFZ3zsOrH2Ij1HkBfgCYueLiL1MaojChgA6amwnAnKZL7lTPlPZBFSlAUcprFOQmneUZ74u2UHpZz3Ld24qtOJZ3VuAHg1KHR7pt87ULupxYClydSTOFJNyUWZA2vveHk4NCAUSazIUTK0Mdxyi5D6jpz68V8XbJbu0rFCC"
    val (encodedMessage, symbolToCodeMap) = huffman.encode(originalMessage.toCharArray().map { it.code }.toIntArray())
    val dht = DHT(symbolToCodeMap, 0, 0)
    val dht2 = DHT(symbolToCodeMap, 1, 1)
    val quant50 = DCT.quantMatrix50()
    val quant50List = quant50.toArray2().toList().flatMap { it.toList() }.map { it.toInt().toUByte() }
    val dqt = DQT(ArrayList(quant50List), 0, 0)
    val dqt2 = DQT(ArrayList(quant50List), 1, 0)

    //SOI
    bitstream.addByteToStream(arrayListOf(0xffu, 0xd8u))

    //APP0
    bitstream.addBitStream(app0.getBitStream());

    //DQT
    bitstream.addBitStream(dqt.getBitStream())
    bitstream.addBitStream(dqt2.getBitStream())

    //SOF0
    bitstream.addBitStream(sof0.getBitStream())

    //DHT
    bitstream.addBitStream(dht.getBitStream())
    bitstream.addBitStream(dht2.getBitStream())


    //EOI
    bitstream.addByteToStream(arrayListOf(0xffu, 0xd9u))

    bitstream.saveToFileAsBytes("test.jpeg")

}

