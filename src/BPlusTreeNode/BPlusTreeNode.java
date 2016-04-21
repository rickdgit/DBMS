package BPlusTreeNode;

import java.util.Arrays;
import java.util.List;

//Using Order as the minimal number of data in a bucket
public class BPlusTreeNode {

	private BPlusTreeNode parents,next,prev;//nodes connections
	private IntNode[] elements ; //used for leaf - store <SearchKey,Address> pair data
	private int[] indexs; //used for store the index
	private BPlusTreeNode[] nextlevels; //used for connect to next level leaf
	// nodeNum is total number of elements or index inside of this TreeNode
	// nodePosn is the current posn of this node in parents
	private int order,nodeNum,nodePosn;
	//root - for init use
	public BPlusTreeNode(int order){
		this.setOrder(order);
		this.setNodeNum(0);
		this.setNodePosn(0);
		this.setParents(null);
		this.setNext(null);
		this.setPrev(null);
		this.setElements(new IntNode[2*order]);
		// this.setNextlevels(new BPlusTreeNode[order*2+1]);
		// this.setIndexs(new int[order*2]);
		this.setNextlevels(null);
		this.setIndexs(null);

	}
	//middle node
	public BPlusTreeNode(int order,int nodePosn,BPlusTreeNode parents){
		this.order = order;
		this.nodePosn = nodePosn;
		this.parents = parents;
		this.nodeNum = 0;
		this.next = null;
		this.prev = null;
		this.nextlevels = new BPlusTreeNode[order*2+1];
		this.elements = null;
		this.setIndexs(new int[order*2]);
	}
	//leaf
	public BPlusTreeNode(int order,int nodePosn,BPlusTreeNode parents,BPlusTreeNode next,BPlusTreeNode prev){
		this.setIndexs(null);
		this.nextlevels = null;
		this.elements = new IntNode[order*2];
		this.next = next;
		this.prev = prev;
		this.parents = parents;
		this.order = order;
		this.nodeNum = 0;
		this.nodePosn = nodePosn;
	}
	//Over All insertion method  - always return root
	public BPlusTreeNode overAllInsert(IntNode obj){
		BPlusTreeNode res = this;
		BPlusTreeNode temp = null;
		//if this node is leaf - insert directly
		if(this.elements != null){
			res = this.insert(obj);
		}
		//if this node is non - leaf
		else{
			temp = this.search(obj);
			temp = temp.insert(obj);
			//compare reference
			if(temp!=res){
				res = temp;
			}
		}
		return res;
	}
	public BPlusTreeNode insert(IntNode obj){
		//The return that need to return
		BPlusTreeNode res = null;
		res = this.parents;
		//Base case
			//Element or index could be inserted successfully
		if(this.nodeNum<(2*order)){
			this.insertElement(obj);
			res = this;
		}
		//Recursive Case
			//No space for insert, need to be split then recursively call parents insert
		else{
			//In this case, all elements are full, we need to expand one node to two node
			//by making a new node with two child
			IntNode[] tempInt = mergeTwoIntNode(this.elements,obj);
			int meadianPosn = (2*order+1)/2;
			IntNode medianObj = tempInt[meadianPosn];

			//FIrst part - 0 => medianPosn -1
			IntNode[] first = new IntNode[2*order];
			//Second part - medianPosn => end
			IntNode[] second = new IntNode[2*order];

			int count = 0;
			int firstlength = 0, secondlength = 0;
			//Fill the list
			for(IntNode i : tempInt){
				if(count<meadianPosn){
					//Fisrt part
					first[count] = i;
					firstlength++;
				}
				else{
					second[count - meadianPosn] = i;
					secondlength++;
				}
				count++;
			}
			//Start make new node - Root
			BPlusTreeNode  temp = new BPlusTreeNode(this.order,this.nodePosn,this.parents);
			//Declare right child
			BPlusTreeNode tempRightLeaf = new BPlusTreeNode(this.order,1,this.parents,null,this);
			//make left child - modify current node
			this.setElements(first);
			this.setNext(tempRightLeaf);
			this.setParents(temp);
			this.setNodeNum(firstlength);
			//Make right child - updatetempRightLeaf;
			tempRightLeaf.setElements(second);
			tempRightLeaf.setNodeNum(secondlength);
			tempRightLeaf.setParents(temp);

			//Start setup root
			int[] tempIndexs = new int[order*2];
			//make median as first index
			tempIndexs[0] = tempInt[meadianPosn].getSearchKey();
			//merge tempIndexs into index
			temp.setIndexs(tempIndexs);
			//Set nextlevels
			BPlusTreeNode[] tempNextLevel = new BPlusTreeNode[2*order+1];
			//Assign left
			tempNextLevel[0] = this;
			//Assign Right
			tempNextLevel[1] = tempRightLeaf;
			temp.setNextlevels(tempNextLevel);
			temp.setNodeNum(1);
			//Check parents avaliable - if not, this ndoe is the new one
			if(res != null){
				res = this.parents.parents.insert(temp);
			}
			else{
				res = temp;
			}
//			return res;
		}
		return res;
	}
	//Insert the element(an IntNode) to current leaf(space avaliable)
	public void insertElement(IntNode obj){
		int posn = binarySearch(obj);
		//if elemetns is not empty ,need to move back
		if( this.elements[0]!=null){
			this.moveBack(posn,obj);
		}
		this.elements[posn] = obj;
		this.nodeNum++;
	}
	// Add obj to IntNode[] arr and return the new array
	public IntNode[] mergeTwoIntNode(IntNode[] arr, IntNode obj){
		IntNode[] temp = new IntNode[arr.length+1];
		int i = 0,posn = binarySearch(obj);
		while(i < temp.length){
			if(i == posn){
				temp[i] = obj;
				i++;
			}
			else if(i < posn){
				temp[i] = this.elements[i];
				i++;
			}
			else{
				temp[i] = this.elements[i-1];
				i++;

			}
		}
		arr = temp;
		return arr;
	}
	//Used for search the corrent or appoimate location for an IntNode object
	//return value should be the one just bigger than target
	//Which means return value should be moved back
	//if duplicate, return the next posn of duplication, eg {1,2,4,5} => return 3 if search 4
	public int binarySearch(IntNode obj){
		int res = 0;
		int toBeInserted = obj.getSearchKey();
		//Using loop instead of recursion to write a BSearch
		//If empty, insert directy
//		System.out.println(this.elements[0]);
		if(this.elements[0] == null){
			// this.elements[0] = obj;
			res = 0;
		}
		//First iteration : use linear serch instead of BSearch
		else{
			// int start = 0;
			// int end = this.elements.length;
			// int mid = 0;
			// //Stop condition
			// //arr[mid] = obj => find the target
			// while(arr[mid]!= obj || start != end){
			//
			// }
			if(this.elements[0].compareTo(obj) == 1){
				res = 0;
			}
			else{
				for(int i = 0; i < this.elements.length; i++){
					//aescding order in this array
					if(this.elements[i]!=null &&
					(this.elements[i].compareTo(obj) == -1
					|| this.elements[i].compareTo(obj) == 0)){
						//loop all the smaller than obj elements,keep the last index of them
						//res as the one in front of obj
						res = i;
					}
				}
				res++;
			}
		}
		return res;
	}
	//Insertation methond for insert the non-leaf node
	//IF spece avaliable - use base case otherwise use recursice case
	public BPlusTreeNode insert(BPlusTreeNode obj){
		BPlusTreeNode res = this.parents;
		//Basic case: if this node's indexs is not empty, insert directly by calling insertBPNode methond
		if(this.nodeNum < order*2){
			res = insertBPNode(obj);
		}
		//Recursive case: if this node's indexs is full,build a new subtree and insert for parents
		else{
			//This non-leaf node is full - split and make a new node and insert to parents
			//First, Get Ready for obj

			//+1 means the obj that to be inserted need to be consindered
			int midPosn = (2*order+1)/2;
			//Does obj should be insert ahead of mid
			boolean front=obj.getNodePosn()<midPosn;

			//Making new Root
			BPlusTreeNode temp = new BPlusTreeNode(this.order,this.nodePosn,this.parents);

			//lfet child - nextlevels 0 -> midPosn ; indexs 0->midPosn -1
			BPlusTreeNode left = new BPlusTreeNode(this.order,0,temp);

			// right child - nextLevel midPosn -> end ; indexs midPosn+1 -> end
			BPlusTreeNode right = new BPlusTreeNode(this.order,1,temp);

			//Setup left
			//for left next level
			BPlusTreeNode[] leftNlevel = new BPlusTreeNode[2*order+1];
			//for left indexs
			int[] leftIndex = new int[2*order];
			int i = 0; //used for this.nextNextlevel index
			int j = 0; //Used for left / right index
			while(i<midPosn){
				if(front){
					//nextlevel
					leftNlevel[j]=this.getNextlevels()[i];
					//indexs
					leftIndex[j] = this.getIndexs()[i];
					//Obj need to be consindered
					if( j == (obj.getNodePosn()+1)){
						j++;
						leftNlevel[j] =obj.getNextlevels()[1];
					}
				}else{
					//Don't consinder it
					//nextlevel
					leftNlevel[i]=this.getNextlevels()[i];
					//indexs
					leftIndex[i] = this.getIndexs()[i];
				}
				i++;
				j++;
			}
			if(!front){
				leftNlevel[midPosn] = this.getNextlevels()[midPosn];
			}
			//left nodeNum
			left.setNodeNum(i);


			//Setup right
			//for right next level
			BPlusTreeNode[] rightNlevel = new BPlusTreeNode[2*order+1];
			//for right indexs
			int[] rightIndex = new int[2*order];
			i = midPosn+1;
			j = 0;
			//If it is in front,which means the nextlevel[midPosn should in right]
			if(front){
				rightNlevel[0] = this.getNextlevels()[midPosn];
				j++;
			}

			while(i<2*order){
				if(front){
					//in front part , not in this part
					//for right level
					rightNlevel[i-midPosn-1] = this.getNextlevels()[i];
					//for right indexs
					rightIndex[i] = this.getIndexs()[i];
				}
				else{
					//for right level
					rightNlevel[j] = this.getNextlevels()[i];
					//for right indexs
					rightIndex[j] = this.getIndexs()[i];
					if(j == (obj.getNodePosn()+1)){
						j++;
						rightNlevel[i] = obj.getNextlevels()[1];
					}
				}
				i++;
				j++;
			}
			//right nodeNum
			right.setNodeNum(2*order - midPosn);


			//Setup temp
			BPlusTreeNode[] tempNextLevel = new BPlusTreeNode[2*order];
			tempNextLevel[0] = left;
			tempNextLevel[1] = right;

			//Setup Indexs
			int[] tempInt = new int[2*order];
			tempInt[0] = obj.getNodePosn()==midPosn?obj.getIndexs()[0]:this.getIndexs()[midPosn];
			temp.setIndexs(tempInt);

			//Setup nodeNum
			temp.nodeNum = 2;

			//Finally Update "this" to left
			// this = left;
			this.setNodeNum(left.getNodeNum());
			this.setNodePosn(left.getNodePosn());
			this.setParents(left.getParents());
			this.setNextlevels(left.getNextlevels());
			this.setIndexs(left.getIndexs());

			if(res != null){
				res = this.parents.parents.insert(temp);
			}
			else{
				res = temp;
			}
		}
		return res;
	}
	//Used for non-leaf node's base case insertation
	public BPlusTreeNode insertBPNode(BPlusTreeNode subtree){
		//put the subtree into current non-leaf node
		//subtree's indexs[0] should be in this indexs's subtree.posn - we need to call moveBack
		//subtree's left child stays where it is, add rightchild to posn+1's location
		int posn = subtree.getNodePosn();
		//Make spot avaliable for indexs and node
		this.moveBack(posn,this.indexs[0]);
		this.moveBack(posn+1,this.nextlevels[0]);
		this.nodeNum++;
		//assign the new node
		//For indexs
		this.indexs[posn] = subtree.getIndexs()[0];
		//For right child
		this.nextlevels[posn+1] = subtree.getNextlevels()[1];
		//correct posn
		this.nextlevels[posn+1].setNodePosn(posn+1);
		// Update right child's next
		if(this.nodeNum!= posn){
			//right isn't last element of nodes
			this.nextlevels[posn+1].setNext(this.nextlevels[posn+2]);
		}
		//Otheerwise is null
		//Left child stays where it was
		this.nextlevels[posn].setNodePosn(posn);
		return this;
	}
	//Make the empty pson avaliable
	// Object[] - input array which need to be moved

	public void moveBack(int posn,Object obj){
		//Used for IntNode(Elements) / int(indexs) / BPlusTreeNode(nextlevels);
		if(obj instanceof IntNode){
			//In the condition that need to move IntNode element - insertation basic case
			//add the object in to array

			for(int i = this.nodeNum ; i > posn ;i--){
				this.elements[i] = this.elements[i-1];
			}
		}
		else if(obj instanceof BPlusTreeNode){
			//Used for moveback basic non-leaf insert case
			//move nextlevels with an empty avaliable spot

			//starts from nodeNum+1 since nextlevels is 2*order+1
			for(int i = this.nodeNum+1;i>posn;i--){
				this.nextlevels[i] = this.nextlevels[i-1];
			}
		}
		else if(obj instanceof Integer){
			//Used for moveback basic non-leaf case
			//move indexs with an empty spot
			for(int i = this.nodeNum ; i > posn ;i--){
				this.indexs[i] = this.indexs[i-1];
			}
		}
		else{

		}
	}
	public String toString(){
		String res = "";
		if(this.elements != null){
			res += "This is a leaf node \n";
			for(int i = 0; i < this.nodeNum ; i++){
				res += this.elements[i].toString();
			}
		}
		else{
			res += "This is a non - leaf node\n";
			for ( int i=0; i<this.nodeNum;i++){
				res+= this.nextlevels[i].toString()+"\n";
				res += "Index :"+ i+"\n";
			}
			res += "The last node\n";
			res += this.nextlevels[nodeNum]+"\n";
		}
		return res;
	}
	public String nodeToString(){
		String rs = "";
		//Means it is leaf
		if(this.elements != null){
			//Loop and print out all the elements
			rs += "This is an leaf \n";
			for(int i = 0; i<this.elements.length && this.elements[i] != null;i++){
				rs+=this.elements[i].toString()+" ";
			}
		}else{
		//Means it is non - leaf node
		//Print out INT
			rs += "This is an non-leaf";
			for(int i : this.indexs){
				rs += i+"";
			}
		}
		return rs;
	}

	public boolean contains(int searchKey){
		//contains is checking if the node contains the search key
		for(int i=0;i<this.elements.length;i++){
			if(this.elements[i].getSearchKey() == searchKey){
				return true;
			}
		}

		return false;
	}

	//remove elements in array
	public void removeElements(Object[] myArray,int pos){
		System.arraycopy(myArray, pos+1, myArray, pos, myArray.length-1-pos);
	}
	//remove node
	public void removeKey(int searchkey){

		for(int i = 0; i < elements.length;i++ ){
			if(elements[i].getSearchKey() == searchkey){
				removeElements(elements,i);
			}
		}
	}

	public void insertFront(IntNode[] myNode,IntNode node){
		for(int i = myNode.length-1;i>0;i--){
			myNode[i] = myNode[i-1];
		}
		myNode[0] = node;
	}

	// insert elemet into first null position of array
	public void insertArray(IntNode[] myNode,IntNode node){
		for(int i = 0;i<myNode.length;i++){
			if(myNode[i]==null){
				myNode[i] = node;
				break;
			}
		}
	}



	public void delete(int searchKey,BPlusTreeNode tree){
//		BPlusTreeNode res = null;
//		res = this.parents;

		//if this is leaf node
		if(this.nextlevels == null){

			//if not contains search key,just return
			if(!contains(searchKey)){
				return;
			}

			//if it's leaf node and also root node, just delete
			if(this.getParents() == null){
				removeKey(searchKey);
			}else{
				//if the node is more than half full, just delete
				if(this.nodeNum>order && this.nodeNum >2){
					removeKey(searchKey);
				}else{
					//if current node's nodenum <= order and its previous node's nodenum > order,
					//then we can borrow from previous node(borrow form sibling)
					if(this.prev !=null
					   &&this.prev.getElements().length>order
					   &&this.prev.getElements().length>2
					   &&this.prev.getParents()==this.parents){
//						int pos = this.prev.getElements().length;
//						IntNode current  = null;
//						current = this.prev.getElements()[pos-1];//get the borrow intNode;
//						//insert the borrow node to the current node.
//						this.insert(current);

						//get the last element from the previous node,insert it into elements,
						//then delete it from previous node after borrow);
						IntNode temp = null;
						for(int i=0;i<prev.getElements().length;i++){
							if(prev.getElements()[i]==null){
								temp = prev.getElements()[i-1];
								insertFront(elements,temp);
								prev.getElements()[i-1]=null;
								break;
							}
						}

						//change the parent node index value
						parents.indexs[prev.nodePosn] = elements[0].getSearchKey();

						//then delete the key
						removeKey(searchKey);
					}//if current node's nodenum < order and its next node's nodenum> order,
					 //then we can borrow form next node(borrow form sibling).
					else if(this.next !=null
							 &&this.next.getElements().length>order
							 &&this.next.getElements().length>2
							 &&this.next.getParents()==this.parents){
						//get the first element of next node
						IntNode temp = null;
						temp = next.getElements()[0];
						insertArray(elements,temp);
						removeElements(next.getElements(),0);
//						IntNode current = null;
//						current = this.next.getElements()[0];
//						this.insert(current);
						//then delete the key
						removeKey(searchKey);

						//change the parent node index value
						parents.indexs[nodePosn] = next.getElements()[0].getSearchKey();
					}//or we need to merge the node
					else{
						//two case:
						//
						//

						//case1:merge with the previous node
						if(this.prev != null
						   &&this.prev.getElements().length <= order
						   &&this.prev.getParents() == this.parents){
							//need insert front there

							for(int i =0;i<this.prev.getElements().length;i++){
								insert(this.prev.getElements()[i]);
							}
							removeKey(searchKey);
						    //prev.setParents(null);
							//prev.setElements(null);
							//should remove previous node form this.parent
							for(int j = 0; j<parents.getNextlevels().length;j++){
								if(this.prev == parents.getNextlevels()[j]){
									removeElements(parents.getNextlevels(),j);
								}
							}

							//update the connection
							if(prev.getPrev()!=null){
								BPlusTreeNode temp = prev;
								temp.getPrev().setNext(this);
								prev = temp.getPrev();
								temp.setNext(null);
								temp.setPrev(null);
							}else{

							}
						}//case2:merge with the next node
						else if(next!=null
								&&next.getElements().length<=order
								&&next.getParents() == parents){
							for(int i =0;i<this.next.getElements().length;i++){
								insert(this.next.getElements()[i]);
							}
							removeKey(searchKey);
							for(int k = 0; k<parents.getNextlevels().length;k++){
								if(this.next == parents.getNextlevels()[k]){
									removeElements(parents.getNextlevels(),k);
								}
							}

							//update the connection
							if(next.getNext()!=null){
								BPlusTreeNode temp = next;
								temp.getNext().setPrev(this);
								next = temp.getNext();
								temp.setNext(null);
								temp.setPrev(null);
							}else{
								next.setPrev(null);
								next = null;
							}
						}
					}



				}
			}

		}//if not leaf node
		else{
			//if the searchKey < the most left key of the node, search the first child node
			if(searchKey < indexs[0]){
				nextlevels[0].delete(searchKey, tree);
			}//if the searchKey > the most right key of the node, search the last child node
			else if(searchKey > indexs[indexs.length-1]){
				nextlevels[nextlevels.length-1].delete(searchKey, tree);
			}//or keep search the previous child node of which is > key
			else{
				for(int i=0;i<indexs.length;i++){
					if(indexs[i] <= searchKey&& indexs[i+1]>searchKey){
						nextlevels[i].delete(searchKey, tree);
					}
				}
			}
		}


	}
	public BPlusTreeNode search(IntNode searchKey){
		BPlusTreeNode result = null;
		//check this node is leaf or non - leaf
		//non - leaf
		if(this.elements == null){
			//linear search
			int res = -1;
			int i = 0;
			//FOr the case that goes to first element
			if(this.indexs[0]>searchKey.getSearchKey()){
				res = 0;
			}
			//for the case between first element and last element
			while(i<2*this.order){
				if(this.indexs[i]!= 0 && this.indexs[i]<=searchKey.getSearchKey()){
					res = i+1;
				}
				i++;
			}
//			System.out.println(res+"   "+this.getNextlevels()[res]+"  "+this.getIndexs()[res]);
			result = this.getNextlevels()[res].search(searchKey);
		}
		else{
			result = this;
		}
		return result;
	}
	public BPlusTreeNode getPrev() {
		return prev;
	}
	public void setPrev(BPlusTreeNode prev) {
		this.prev = prev;
	}
	public BPlusTreeNode getNext() {
		return next;
	}
	public void setNext(BPlusTreeNode next) {
		this.next = next;
	}
	public BPlusTreeNode getParents() {
		return parents;
	}
	public void setParents(BPlusTreeNode parents) {
		this.parents = parents;
	}
	public IntNode[] getElements() {
		return elements;
	}
	public void setElements(IntNode[] elements) {
		this.elements = elements;
	}
	public BPlusTreeNode[] getNextlevels() {
		return nextlevels;
	}
	public void setNextlevels(BPlusTreeNode[] nextlevels) {
		this.nextlevels = nextlevels;
	}
	public int getNodePosn() {
		return nodePosn;
	}
	public void setNodePosn(int nodePosn) {
		this.nodePosn = nodePosn;
	}
	public int getNodeNum() {
		return nodeNum;
	}
	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int[] getIndexs() {
		return indexs;
	}
	public void setIndexs(int[] indexs) {
		this.indexs = indexs;
	}

}
