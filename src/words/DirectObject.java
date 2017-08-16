package words;

public class DirectObject extends Noun {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8025871754275250283L;

	public DirectObject(String word) 
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.DIRECT_OBJECT;
	}

}
