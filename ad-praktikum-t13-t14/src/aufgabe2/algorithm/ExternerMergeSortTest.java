package aufgabe2.algorithm;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.io.File;

import org.junit.*;


public class ExternerMergeSortTest {
	

	@Test
	public void testBlockSort() {
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		Arrays.sort(expectedArr); //Java-Standard-implementierung vom sortieren

		ExternerMergeSort.blockSort_insertion(testelems,0,testelems.length-1);
		assertEquals(expectedArr, testelems); 
	}
	
	@Test
	public void testBlockSortEmpty() {
		int[] testelems = {};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		
		ExternerMergeSort.blockSort_insertion(testelems,0,testelems.length-1);
		assertEquals(expectedArr, testelems); 
	}
	
	@Test
	public void testBlockSort_quick() {
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34,12};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		Arrays.sort(expectedArr); //Java-Standard-implementierung vom sortieren

		ExternerMergeSort.blockSort_quick(testelems,0,testelems.length-1);
		//System.out.println(Arrays.toString(test.getData()));
		assertEquals(expectedArr, testelems); 
	}
	
	@Test @Ignore
	public void testBlockSort_Time() {
		long duration=0;
		int durchlaeufe = 30;
		for (int i=0; i<durchlaeufe; i++){
			int[] testelems = initRandomArray(1000000, 10000000, -1000000);
			long start = System.currentTimeMillis();
			ExternerMergeSort.blockSort_quick(testelems,0,testelems.length-1);
			duration += System.currentTimeMillis() - start;
			assertTrue(isSorted(testelems));
		}
		System.out.println("Dauer der Sortierung (durchschnittlich): " + (duration / durchlaeufe) + "ms");
	}
	
	
	@Test 
	public void testMergeSortAlgorithm() {
		String InputFilePath = "DataManagerTest";
		String outputFilePath = null;
		//TestFileGenerator.createTestFile(InputFilePath,1000000,1000);
		
		 int anzahlZahlenProSchreibVorgang = 10000;
	      int anzahlSchreibVorgaenge = 1;
	        //TestFileGenerator.createTestFile("DataManagerTest",anzahlZahlenProSchreibVorgang,anzahlSchreibVorgaenge);
//	      aufgabe2.data.Reader.setInegerCountPerRead(2097152);   // 2097152 =>  ca 8mb lese buffer dadurch ist der schreibbuffer 24mb groß ( 3*8)
//	      aufgabe2.data.DataManagerImpl.setFolgenReaderInitValue(100000000); // hier bitte ein vielfaches von anzahlZahlenProSchreibVorgang * anzahlSchreibVorgaenge

		
		outputFilePath = ExternerMergeSort.sort(InputFilePath);
		
        System.out.println("Sortieren abgeschlossen. Prüfe sortierung...");
        //assertTrue(TestFileGenerator.isSorted(outputFilePath));
        
        //Aufräumen
        //deleteFile(InputFilePath);
        //deleteFile(outputFilePath);
	}
	
	private static void deleteFile(String path){
		File file = new File(path);
		if (file.exists())
			file.delete();
	}
	
	
    private static int[] initRandomArray(int arraySize, int upperBound, int lowerBound) {
        System.gc();
        int array[] = new int[arraySize];
        Random random = new Random();

        upperBound += (1 + Math.abs(lowerBound));

        for(int i = 0; i < array.length; i++){
            array[i] = random.nextInt(upperBound)+lowerBound;
        }

        return array;
    }
    private static boolean isSorted(int[] data){
    	if(data.length==0)
    		return true;
    	for (int i = 1; i < data.length; i++) {
			if(data[i-1]>data[i])
				return false;
		}
    	return true;
    }

}
