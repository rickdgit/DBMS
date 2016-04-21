package BPlusTreeNode;

public class IntNode implements Comparable<IntNode>{
	private int searchKey;
	private Object SomeThing;
	public IntNode(int searchKey){
		this.setSearchKey(searchKey);
		this.setSomeThing(null);
	}
	public IntNode(int searchKey,Object sth){
		this.setSearchKey(searchKey);
		this.setSomeThing(sth);
	}
	public int getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(int searchKey) {
		this.searchKey = searchKey;
	}
	public Object getSomeThing() {
		return SomeThing;
	}
	public void setSomeThing(Object someThing) {
		SomeThing = someThing;
	}
	@Override
	//Return value for compareTo this> obj => 1 othersie -1, equals => 0
	public int compareTo(IntNode obj) {
		// TODO Auto-generated method stub
		int res = 0;
		//this > obj => 1
		//this = obj => 0
		//this < obj =< -1
		return  res = this.searchKey > obj.getSearchKey()?1:(this.searchKey == obj.getSearchKey()?0:-1);
	}
	public String toString(){
		return this.searchKey+" ";
	}

}
