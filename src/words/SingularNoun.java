package words;

public class SingularNoun extends Noun
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3899679390161374843L;

	public SingularNoun(String word)
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.SINGULAR_NOUN;
	}
	
}
