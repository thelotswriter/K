package words;

public class PluralNoun extends Noun
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4144280474185912779L;

	public PluralNoun(String word)
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.PLURAL_NOUN;
	}
	
}
