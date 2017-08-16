package words;

public class AdverbAdverb extends Adverb
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdverbAdverb(String word)
	{
		super(word);
	}
	
	public WordType getType() 
	{
		return WordType.ADVERB_ADVERB;
	}

}
