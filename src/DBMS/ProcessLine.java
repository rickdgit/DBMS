package DBMS;

import java.util.Scanner;

class ProcessLine {
	Scanner in;
	String readLine;
	
	ProcessLine(){
		in = new Scanner(System.in);
		readCommand();
	}
	
	private void readCommand(){
		String[] words;
		
		System.out.println("Enter your command: ");
		readLine = in.nextLine();
		words = readLine.split(" ");
		
		switch(words[0]){
		case "SEARCH":
			search(words);
		case "INSERT":
			insert(words);
		case "CREATE":
			createTable(words);
		case "DROP":
			dropTable(words);
		case "UPDATE":
			update(words);
		case "DELETE":
			delete(words);
		default :
			System.out.println("Invaild command.");
		}
	}

	private void delete(String[] words) {
		// TODO Auto-generated method stub
		
	}

	private void update(String[] words) {
		// TODO Auto-generated method stub
		
	}

	private void dropTable(String[] words) {
		// TODO Auto-generated method stub
		
	}

	private void createTable(String[] words) {
		// TODO Auto-generated method stub
		
	}

	private void insert(String[] words) {
		// TODO Auto-generated method stub
		
	}

	private void search(String[] words) {
		// TODO Auto-generated method stub
		
	}
}
