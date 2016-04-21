package DBMS;

import java.util.ArrayList;
import java.util.Scanner;

import BPlusTreeNode.BPlusTreeNode;

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
			printInfo(search(words));
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
	
	//===================operation methods=========================

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

	private ArrayList<BPlusTreeNode> search(String[] words) {
		// TODO Auto-generated method stub
		BPlusTreeNode[] bpts = getTrees(words);
		int[] key = getKey(words);
		ArrayList<BPlusTreeNode> result = new ArrayList<BPlusTreeNode>();
		
		for(BPlusTreeNode bpt:bpts){
			if(key.length > 1){
				//if # of keys > 1, start range search. 
				//For one boundary search, one of the element in the key array can be Integer.MIN or .MAX.  
				for(BPlusTreeNode node : bpt.search(key[0],key[1]))
					result.add(node);
			}else{
				//search for elements' key equal to key
				for(BPlusTreeNode node : bpt.search(key[0]))
					result.add(node);
			}
		}
		return result;
	}
	
	//Print information depend on the input nodes
		//print error msg when input list.size() = 0
	private void printInfo(ArrayList<BPlusTreeNode> nodes){
		
	}
	
	//=====================helping methods=======================
	//find out trees that assigned by the command
	private BPlusTreeNode[] getTrees(String[] words){
		
		
	}
	
	//return the range of key assigned by the command
	private int[] getKey(String[] words){
		
	}
}
