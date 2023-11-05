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
    //LINK TO SPEC-> https://elearning.thws.de/pluginfile.php/2033910/mod_resource/content/0/JPG-Spezifikation.pdf
    //TODO:Segmente APP0 und SOF0 implementieren: APP0 -> länge des Sements(high byte, low byte) >= 16
    //TODO: + 1 Byte(1) + 1 Byte(1.1) +1 Byte(0) + high byte, low byte (0x0048) + high byte, low byte (0x0048) +1 Byte(0)
    //TODO: +1 Byte(0)
    //TODO: SOFO -> länge des Sements(high byte, low byte) 8 + Anzahl Komponenten*3 + 1 Byte(8) + (2 Byte, Hi-Lo) >0 +
    //TODO: (2 Byte, Hi-Lo) >0 + 1 Byte (Anzahl der Komponenten) + jede Komponente 3 Byte
    //VORSCHLAG KONTSTRUCTOREN für Segmente und mithilfe von Segmenten das JPG objekt zusammen bauen JPG(SEGMENT APP0, SEGMENT SOFO, etc.)

}