package aufgabe2_old.data;

import aufgabe2_old.interfaces.DataWrapper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 04.11.12
 * Time: 13:37
 */
public class FolgenWriter {
    private Writer fileWriter;
    private int folgenLength;

    private boolean isLastFolgeReached;

    // der byteBuffer hinter dem intBuffer,
    private ByteBuffer byteBuffer;
    // intBuffer ist nur die view auf den bytebuffer
    private IntBuffer intBuffer;

    public static FolgenWriter create(String fileName){
        return new FolgenWriter(fileName);
    }
    private  FolgenWriter(String fileName){
        fileWriter = Writer.create(fileName);
        isLastFolgeReached = false;
        initBuffer();
    }
    private void initBuffer(){
        // buffer der maximalen groeße erstellen
        byteBuffer = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * (int) Writer.INTEGER_COUNT_PER_WRITE);
        // bytebuffer als intbuffer interpretieren
        intBuffer = byteBuffer.asIntBuffer();
    }
    public void close(){
        // hier muss noch geguckt werden welches besser funzt...
        if(intBuffer.position() != 0 || intBuffer.capacity() != intBuffer.remaining()){
            writeBuffer();
        }
        fileWriter.close();
    }
    private void writeBuffer(){
        // wenn der intbuffer komplett voll ist schreib ihn so weg,
        // sollte er nicht voll sein, dann MUSS der leere teil entfernt werden
        // damit keine nullen in die datei geschrieben werden Oder sogar müll
        // der sich noch im buffer befindet
        if(intBuffer.remaining()==0){
//            intBuffer.flip();

            // warum byteBuffer? weil der intbuffer nur ne view auf den bytebuffer ist
            fileWriter.writeByteBufferToFile(byteBuffer);
            intBuffer.clear();
        }else{
            // daten von dem zugroßen buffer (intBuffer) umschaufeln in den tmp_intbuffe um sie
            // korekt wegschreiben zukönnen.
            int positions = intBuffer.position();
            ByteBuffer tmp_byteBuff = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * positions);
            IntBuffer tmp_intBuff = tmp_byteBuff.asIntBuffer();
            intBuffer.flip();
            for(int i= 0;i < positions;i++){
                tmp_intBuff.put(intBuffer.get());
            }
            fileWriter.writeByteBufferToFile(tmp_byteBuff);
            intBuffer.clear();

        }
    }


    public void writeFolge(DataWrapper wrap){
        // wenn die folgenLaenge gleich dem restlichem platz im buffer entspricht,
        // dann du die folge rein und schreib sie raus
        System.out.println(fileWriter.getFileName()+": Bufferauslastung: "+((double)intBuffer.position())/intBuffer.capacity());

        int[] folge = wrap.getData();
        int folgenLaenge = wrap.getSize();
        if(folgenLaenge> Writer.INTEGER_COUNT_PER_WRITE){
            System.out.println(fileWriter.getFileName()+": folgenLaenge zu lang, dass tut weeehh  meeehh");
            System.exit(1);
        }

        if(folgenLaenge == intBuffer.remaining()){
            intBuffer.put(wrap.getData(),0,folgenLaenge);
            writeBuffer();
            intBuffer.clear();
            // wenn die folgenLaenge kleiner ist dann einfach in den buffer rein
        }else if(folgenLaenge < intBuffer.remaining()){
            intBuffer.put(wrap.getData(),0,folgenLaenge);
        }else {
            // und wenn sie größer ist den rest auffüllen und dann den rest der folge
            // in den geleerten buffer schreiben
            // berechne den rest der folge der nicht in den buffer passt
            int derRestDerNichtPasst = folgenLaenge - intBuffer.remaining();
            // schreib den teil der "passt" in den buffer
            intBuffer.put(folge,0,intBuffer.remaining());
            writeBuffer();
            intBuffer.clear();
            // füll den rest der nicht passte nun in den intbuffer
            for (int i = folgenLaenge-derRestDerNichtPasst; i < folgenLaenge ; i++) {
                intBuffer.put(folge[i]);
            }
        }
    }
    @Deprecated
    private void multipleWritesPerFolge(int[] folge) {
        // da bei den oversizedfolgen nicht gilt das sie immer gleich lang sind
        // bis zum ende (bezieht sich auf die int arrays, die folgen sind schon immer gleich lang aber
        // das ende einer folge verschiebt sich bei einer teilung und somit kann es reste geben die eben
        // nicht die normale länge vom buffer haben)

        if(folge.length == Writer.INTEGER_COUNT_PER_WRITE){
            intBuffer.put(folge);
            writeBuffer();
            intBuffer.clear();

        }else if(folge.length < Writer.INTEGER_COUNT_PER_WRITE){
            // neue bytebuffer größe berechnen für den schreib vorgang
            byteBuffer = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * folge.length);
            intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(folge);
            writeBuffer();
            intBuffer.clear();
            // Maximale größe des Buffers wiederherstellen
            initBuffer();
        }else {
            System.out.println(fileWriter.getFileName()+": FolgenWriter::multipleWritesPerFolge() : the given folge is greater than our buffersize ! folge.length = "+folge.length+", bufferSize = "+ Writer.INTEGER_COUNT_PER_WRITE);
            // wenn die länge der folge ein vielfaches von Writer.INTEGER_COUNT_PER_WRITE ist,
            // dann schreibe solange bis die folge abgearbeitet ist
            if(folge.length % Writer.INTEGER_COUNT_PER_WRITE == 0){
                for (int aFolge : folge) {

                    intBuffer.put(folge, 0, intBuffer.remaining());
                    writeBuffer();
                }

            }else{

            }

        }
    }
    @Deprecated
    private void singleWritesPerFolge(int[] folge) {
        // wenn die folge.length von der folgenLength abweicht => folge ist die Letzte folge die geschrieben wird
        // da nur die letzte folge eine andere groeße haben kann.
        isLastFolgeReached = !(folge.length == folgenLength);
        if( !isLastFolgeReached ){
            // wenn die gegebene folge kleiner ist als unser
            // restlicher platz im intbuffer, dann schreib die folge
            // in den intbuffer. Andernfalls berechne den rest der folge,
            // der erst nach dem schreiben des intbuffers in den buffer geschrieben wird
            if(folgenLength < intBuffer.remaining()){
                intBuffer.put(folge,0,folgenLength);

            }else{
                // berechne den rest der folge der nicht in den buffer passt
                int derRestDerNichtPasst = folgenLength - intBuffer.remaining();
                // schreib den teil der "passt" in den buffer
                intBuffer.put(folge,0,intBuffer.remaining());
                writeBuffer();
                intBuffer.clear();
               // füll den rest der nicht passte nun in den intbuffer
                for (int i = folgenLength-derRestDerNichtPasst; i < folgenLength ; i++) {
                      intBuffer.put(folge[i]);
                }


            }
        }else{
            // für den fall das die letzte folge nicht die folgen länge hat muss die bytebuffer größe
            // angepasst werden
            if(folge.length < folgenLength){
                // wenn der intbuffer keinen rest in sich drin hat dann neuen
                // buffer der größe der folge allokieren
                if(intBuffer.position() == 0){
                    byteBuffer = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * folge.length);
                    intBuffer = byteBuffer.asIntBuffer();
                    intBuffer.put(folge);
                    writeBuffer();
                }else{
                    // ist die remaining intbuffer size groß genug um den rest aufzunehmmen?
                    // wenn ja dann einfügen und schreiben, andernfalls aktuellen buffer schreiben
                    // und neuen buffer der größe der folge allokieren und den dann auch schreiben.
                    if(intBuffer.remaining() == folge.length ){
                        intBuffer.put(folge);
                        writeBuffer();
                    }else if(intBuffer.remaining() > folge.length){
                        // wenn es zuviel platz im intbuffer gibt, dann erstelle
                        // einen temporären buffer und fülle die elemente aus dem intbuffer
                        // in ihn ein und danach füge die elemente der folge ein
                        // und schreibe ihn nieder
                        ByteBuffer tmp_byteBuff = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * (folge.length+intBuffer.position()));
                        IntBuffer tmp_intBuff = tmp_byteBuff.asIntBuffer();
                        for (int i = 0; i < intBuffer.position(); i++) {
                            tmp_intBuff.put(intBuffer.get(i));
                        }
                        tmp_intBuff.put(folge,0,folge.length);
                        fileWriter.writeByteBufferToFile(tmp_byteBuff);
                        intBuffer.clear();
                    }else{
                       // für den fall das nicht genug platz im buffer ist fülle den buffer auf
                       // und schreibe ihn weg, danach erstelle einen neuen buffer und fülle die
                       // letzten elemente in ihn und schreibe ihn weg
                        // berechne den rest der folge der nicht in den buffer passt
                        int derRestDerNichtPasst = folgenLength - intBuffer.remaining();
                        // schreib den teil der "passt" in den buffer
                        intBuffer.put(folge,0,intBuffer.remaining());
                        writeBuffer();

                        // buffer der maximalen groeße erstellen
                        byteBuffer = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * derRestDerNichtPasst);
                        // bytebuffer als intbuffer interpretieren
                        intBuffer = byteBuffer.asIntBuffer();
                        // füll den rest der nicht passte nun in den intbuffer
                        for (int i = folgenLength-derRestDerNichtPasst; i < folgenLength ; i++) {
                            intBuffer.put(folge[i]);
                        }
                        writeBuffer();
                    }
                }
            }else {
                System.out.println(fileWriter.getFileName()+": error! docht nicht die letzte folge? folge.length ("+folge.length+") > folgenLength ("+folgenLength+")");
            }


        }

    }

    public void setRunLevel(int runLevel){

        // runLevel+1, weil ich jetz mal davon ausgehe das die Folgenlangge im aktuellen run
        // durch das verschmelzen ja doppelt so lang wird, bsp: wenn aus beiden streams
        // immer 8 gelesen werden und runlevel 0 ist, dann ist die zu schreibene folge 16 lang

        folgenLength = (int)(Math.pow(2,runLevel)* FolgenReader.INITAL_FOLGEN_LENGTH);
    }
    public void setFolgenLength(int x){
        folgenLength = x;
    }

    @Override
    public String toString() {
        return fileWriter.getFileName();
    }
    public boolean isOpen(){
        return fileWriter.isOpen();
    }
}
