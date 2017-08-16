package words;

public class IndirectObject extends Noun 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -121955125961507396L;

	public IndirectObject(String word) 
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.INDIRECT_OBJECT;
	}

}
