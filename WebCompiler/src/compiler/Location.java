package compiler;

public class Location {
	private int segment = 2;
	private int offSet;
	private int wordLength;
	
	Location(int s, int o, int w)
	{
		segment = s;
		offSet = o;
		wordLength = w;
	}
	Location(int o, int w)
	{
		offSet = o;
		wordLength = w;
	}
	Location() {}
	
	public int getSegment() {
		return segment;
	}
	public void setSegment(int segment) {
		this.segment = segment;
	}
	public int getOffSet() {
		return offSet;
	}
	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}
	public int getWordLength() {
		return wordLength;
	}
	public void setWordLength(int wordLength) {
		this.wordLength = wordLength;
	}
	
}
