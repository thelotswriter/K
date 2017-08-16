package words;

public class Noun extends Word {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9165603495957538497L;

	public Noun(String theWord) 
	{
		super(theWord);
	}

	public WordType getType() 
	{
		return WordType.NOUN;
	}

}
