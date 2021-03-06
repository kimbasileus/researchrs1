package hyu.kskim.recomsys.prepare.utils;
import java.io.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;


/*
 * ********************************File Processor************************************************************
 * 2011501682 Kyung Soo Kim
 *
 * boolean writer(String directory, String text): Write the given text on the directory file(including folder directory)
 * boolean reader(String directory): Read all line strings in the file
 * boolean Searcher(String directory, String key): Find the element which has the key in the file.
 *
 * **********************************************************************************************************
 */

public class FileIO {

	public boolean writer(String directory, String text){
		String dir;

		dir = directory;

		try{
			File file = new File(dir);  // . is a denotation representing current directory.

			// If the file isn't exist in the directory, make the file.....
			if(!file.exists()){
				System.out.println("Create new file: "+dir);
				file.createNewFile();
			}

			// Write the text on the text file using RandomAccessFile class.
			RandomAccessFile raf = new RandomAccessFile(dir, "rw");
			raf.seek(raf.length()); // Setting writing pointer on the end point.

			if(raf.length()==0) // If the file is empty, write the text at first line.
				raf.writeBytes(new String(text.getBytes("KSC5601"), "8859_1")); // For processing Korean language
			else
				raf.writeBytes("\r\n" + new String(text.getBytes("KSC5601"), "8859_1"));


			raf.close();

			return true;
		}catch(Exception e){
			System.err.println("Writer Error: "+e.getMessage());
			return false;
		}
	}


	
	public StringBuffer reader(String directory){
		String dir;
		if(directory == null) dir = ".\\train_5500.txt";
		else dir = directory;

		try{
			 StringBuffer buffer = new StringBuffer();
			 BufferedReader reader = new BufferedReader(new FileReader(dir));
			 String inputLine = null;
			 while ((inputLine = reader.readLine()) != null){
				 System.out.println(inputLine);
			     buffer.append(inputLine).append("\n");
			 }
			 reader.close();
			 System.out.println(buffer.toString());

			return buffer;

		}catch(Exception e){
			System.err.println("Reader error: "+e.getMessage());
			return null;
		}
	}



	public String Searcher(String directory, String key){
		String dir;
		if(directory == null)
			dir = ".\\textFiles\\test.index";
		else
			dir = directory;

		String rv = null; // Return value

		try{
			
			

			return rv;

		}catch(Exception e){
			System.err.println("Searcher error: "+e.getMessage());
			return rv;
		}
	}
}
