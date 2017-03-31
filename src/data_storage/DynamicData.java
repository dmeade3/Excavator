package data_storage;


import java.util.HashMap;
import java.util.Map;

import static util.SystemConfig.INITIAL_DYNAMIC_DATA_SIZE;

public class DynamicData
{
    private static DynamicData instance = null;

    private final Map<String, DynamicClassDataEntry> data = new HashMap<>(INITIAL_DYNAMIC_DATA_SIZE);

    public static DynamicData getInstance()
    {
        if (instance == null)
        {
            instance = new DynamicData();
        }

        return instance;
    }

    public void put(String className, DynamicClassDataEntry dynamicFileDataEntry)
    {
        getInstance().data.put(className, dynamicFileDataEntry);
    }

    public DynamicClassDataEntry get(String className)
    {
        return getInstance().data.get(className);
    }

    public boolean contains(String className)
    {
        return getInstance().data.containsKey(className);
    }

    public Map<String, DynamicClassDataEntry> getData()
    {
        return getInstance().data;
    }

    public void clear()
    {
        getInstance().data.clear();
    }
}
