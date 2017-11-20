package processTree;

import java.util.List;
import java.util.Map;

public class ThingsNode extends ThingNode
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2206892380173621814L;

	public ThingsNode(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
	{
		super(parent, elements, categories, attributes, confidence);
	}

	public boolean isPlural()
	{
		return true;
	}

	public void setAttributes(Map<String, String[]> attributes)
    {
        for(String key : attributes.keySet())
        {
            String[] values = attributes.get(key);
            for(int i = 0; i < Math.min(values.length, getThingElements().size()); i++)
            {
                getThingElements().get(i).setAttribute(key, values[i]);
				((ThingNode) getElements().get(i)).setAttribute(key, values[i]);
            }
        }
    }
	
}
