package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Reader {

	
	public static Map<String, List<String>> readFile(String filePath) throws FileNotFoundException {
		Map<String, List<String>> fileContent=new HashMap<>();
		Scanner scanner=null;
		scanner = new Scanner(new File(filePath)); 
		scanner=scanner.useDelimiter("\n");
		//String string=scanner.next();
		while(scanner.hasNext()){
		   	String line=scanner.next();
		   	if(!line.isEmpty()) {
		   		if(line.getBytes()[0]!=37) {
			   		Scanner lineScanner=new Scanner(line);
				   	lineScanner=lineScanner.useDelimiter(";");
				   	String id=lineScanner.next();
				   	List<String> list=new LinkedList<>();
				   	fileContent.put(id, list);
				   	while(lineScanner.hasNext()) {
				   		String content=lineScanner.next();
				   		list.add(content);
				   	}
				   	lineScanner.close();
			   	}
		   	}
		}
		scanner.close();
		return fileContent;
	}

}
