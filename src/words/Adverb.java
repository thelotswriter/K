package words;

public class Adverb extends Word 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Adverb(String word)
	{
		super(word);
	}
	
	public WordType getType() 
	{
		return WordType.ADVERB;
	}

}
