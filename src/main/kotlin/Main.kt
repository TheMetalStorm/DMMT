import JPGSegments.APP0
import JPGSegments.DHT
import JPGSegments.SOF0
import datatypes.BitStream
import datatypes.ImageRGB
import DCT.DCT
import datatypes.Channel

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
    val app0 = APP0(1u, 1u, 0u, 0u, 0x48u, 0u, 0x48u)
    val sof0 = SOF0(8u, 0u, 16u, 0u, 16u, 1u,
        arrayListOf(0x01u, 0x22u, 0u))

    val huffman = Huffman()
    val originalMessage = "ufog5JxQIQSqsyFnC09yEjMFCMuyVaqGszj3lGWKVFOQvkS6c4WJvH1rGgfyCC1MCdwx5dmybnv985Llu3sx7YcGkmbFtXNGtGrwn6dp6mzdDgZW6DzMDqqfe8zkEyHurLMApvtMsi5nBGuuUuZSsvZeZ6HXomW6lZLF76SjZqh2Fv3mwqidWvwkUaxdlX4Jl0KuLD97ZLGjnwlRF1PfAGjMbhw6XkJlDbZbLke31cl0seJZqClUBIefuWuhND7QXAFJKnS9x12ouBNTEWbErsq4PH40wOg3aoUJ4U6vi2WmZ9x5medUwKxNe7nNIC0pzqAckSbQrlTZvAFZ3zsOrH2Ij1HkBfgCYueLiL1MaojChgA6amwnAnKZL7lTPlPZBFSlAUcprFOQmneUZ74u2UHpZz3Ld24qtOJZ3VuAHg1KHR7pt87ULupxYClydSTOFJNyUWZA2vveHk4NCAUSazIUTK0Mdxyi5D6jpz68V8XbJbu0rFCC"
//    val originalMessage = "AAAAABBBBBBBBCCCDDDEEEEEEEFFFFFFFFFFFFFFGGG"
    val (encodedMessage, symbolToCodeMap) = huffman.encode(originalMessage.toCharArray().map { it.code }.toIntArray())

//    for (symbol in symbolToCodeMap) {
//        val c = symbol.key
//        val bs = symbol.value
//        println("Symbol: $c")
//        bs.printBits()
//        println(bs)
//        println()
//    }

    encodedMessage.printBits()
    println()
    val array = huffman.decode(HufEncode(encodedMessage, symbolToCodeMap))
    print("Original: ")
    println(originalMessage)
    print("Decoded:  ")
    for (i in array) {
        print(Char(i))
    }

    val dht = DHT(symbolToCodeMap, 0, 0)
    val dht2 = DHT(symbolToCodeMap, 1, 1)

//    //SOI
    bitstream.addByteToStream(arrayListOf(0xffu, 0xd8u))
//
//    //APP0
    bitstream.addBitStream(app0.getBitStream());
//
//    //SOF0
    bitstream.addBitStream(sof0.getBitStream())
//
    //DHT
    bitstream.addBitStream(dht.getBitStream())
    bitstream.addBitStream(dht2.getBitStream())


//    //EOI
    bitstream.addByteToStream(arrayListOf(0xffu, 0xd9u))
//
//    bitstream.printBits()
//
    bitstream.saveToFileAsBytes("test.jpeg")
    println()

    println()
    val image = ImageRGB.readPPM("src/main/kotlin/DCT/red.ppm", 16, 16)
    val redChannel = image.getChannel(0)
    println()
    redChannel.print()
    val dct = DCT.directDCT(redChannel)
//    val toArray2 = DCT.seperateDCT(redChannel).toArray2()
    //toArray2 is a Array<DoubleArray>, convert it to Array<Array<Double>>
//    val dct = Channel(16,16, toArray2.map { it.toTypedArray() }.toTypedArray())

    val idct = DCT.inverseDirectDCT(dct)
    println("orig")
    redChannel.print()

    println("idct")
    idct.print()

    val channel = Channel(8,8, Array(8){ Array(8){ kotlin.random.Random.nextDouble()*255 } })
    println("random channel")
    channel.print()
    val dctarai = DCT.araiDct2D(channel)
    println("arai orig")
    dctarai.print()
    println("arai idct")
    val inverseDirectDCT = DCT.inverseDirectDCT(dctarai)
    inverseDirectDCT.print()
}

