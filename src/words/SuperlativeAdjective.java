package words;

public class SuperlativeAdjective extends Adjective
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4190814594338777062L;

	public SuperlativeAdjective(String word)
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.SUPERLATIVE_ADJECTIVE;
	}
	
}
