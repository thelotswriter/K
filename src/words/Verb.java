package words;

public class Verb extends Word 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5439395225726445234L;

	public Verb(String word)
	{
		super(word);
	}
	
	public WordType getType() 
	{
		return WordType.VERB;
	}

}
