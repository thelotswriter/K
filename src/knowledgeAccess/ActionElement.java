package knowledgeAccess;

import java.io.Serializable;
import java.util.List;

import words.Adverb;
import words.DirectObject;
import words.IndirectObject;
import words.Noun;

import javax.security.auth.Subject;

public class ActionElement implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8741156051768476281L;
	
	private String elementName;
	private Noun subject;
	private DirectObject directObject;
	private IndirectObject indirectObject;
	private List<Adverb> adverbs;
	
	/**
	 * Creates a new action element for storing data about action nodes
	 * @param name The name of the action
	 * @param directObject The direct object associated with the action
	 * @param indirectObject The indirect object associated with the action
	 * @param adverbs The adverbs associated with the action
	 */
	public ActionElement(String name, Noun subject, DirectObject directObject, IndirectObject indirectObject, List<Adverb> adverbs)
	{
		this.elementName = name;
		this.subject = subject;
		this.directObject = directObject;
		this.indirectObject = indirectObject;
		this.adverbs = adverbs;
	}
	
	/**
	 * Gets the element's name
	 * @return The element's name
	 */
	public String getName()
	{
		return elementName;
	}

	public Noun getSubject()
    {
        return subject;
    }

	/**
	 * Gets the direct object associated with the action
	 * @return The direct object associated with the action
	 */
	public DirectObject getDirectObject()
	{
		return directObject;
	}
	
	/**
	 * Gets the indirect object associated with the action
	 * @return The indirect object associated with the action
	 */
	public IndirectObject getIndirectObject()
	{
		return indirectObject;
	}
	
	/**
	 * Gets the adverbs associated with the action
	 * @return The adverbs associated with the action
	 */
	public List<Adverb> getAdverbs()
	{
		return adverbs;
	}

}
