package words;

public class ProperNoun extends Noun
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7705271274416283056L;

	public ProperNoun(String word)
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.PROPER_NOUN;
	}
	
}
