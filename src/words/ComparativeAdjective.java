package words;

public class ComparativeAdjective extends Adjective
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6300645136894791150L;

	public ComparativeAdjective(String word)
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.COMPARATIVE_ADJECTIVE;
	}
	
}
