package processTree.toolNodes;

import processTree.ThingNode;
import subModel.Discrete2DSpatialModel;

public class ModelPicker {
    private static ModelPicker ourInstance = new ModelPicker();

    public static ModelPicker getInstance() {
        return ourInstance;
    }

    private ModelPicker()
    {
    }

    public Model getModel(ThingNode world, ThingNode subject, ThingNode directObject, ThingNode indirectObject)
    {
        return new Discrete2DSpatialModel(directObject, world);
    }
}
