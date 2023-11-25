import JPGSegments.APP0
import JPGSegments.DHT
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
    val app0 = APP0(1u, 1u, 0u, 0u, 0x48u, 0u, 0x48u)
    val sof0 = SOF0(8u, 0u, 16u, 0u, 16u, 1u,
        arrayListOf(0x01u, 0x22u, 0u))

    val huffman = Huffman()
    val originalMessage = "CLiwrJ77mxYbbjJuFK44D4UL6qKtXrT1HWEHLgdHh107NGduCaJMLMkk4mfUQrA468SJYYBPBkfQdMRTuJvcZxmdjnV4XFyHPKBQdeDm12MKJePfCXhht6kePLFcU0r7A45Z6Kht4JwvXaUUw11afdvaXyaAFaC4wHZMpC4uhu4WrnitSyT0gPvQ5xc80f9kTz7HUPaq0K2bM8pX85HxjjcA7L8QUDGmPJmi4iRQcFTukpmEumhDNXKye6kMhEKBzV5DRHMLxihCiAEC1MyJHCpgb16hQt9gxWE19vAeT37mgLw0pBCxHgFyYzVMgzQ3i4Lh2XfacngxjnChZQiRqTvLk1bN3QydyhKcn0976wbQEdkrBR5mhW71vD7z769xvrRctHmYMxXqnUR194570eBGCZKvWfDB4QHGx5UYezSaTN1XwJxbGJeqhmdnhGMeyLhU5SmDTJWZHFMAn9zwbR303z4KS4KVS6hmT9CWp0PmYdaEQHMa"
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
}