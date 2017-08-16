package words;

public class Adjective extends Word 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3878130252368343798L;

	public Adjective(String word)
	{
		super(word);
	}
	
	public WordType getType()
	{
		return WordType.ADJECTIVE;
	}
	
}
