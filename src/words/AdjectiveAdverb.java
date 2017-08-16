package words;

public class AdjectiveAdverb extends Adjective 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2187244292247248588L;

	public AdjectiveAdverb(String word)
	{
		super(word);
	}
	
	public WordType getType() 
	{
		return WordType.ADJECTIVE_ADVERB;
	}

}
