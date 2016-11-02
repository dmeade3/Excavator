package data_storage;


import java.util.HashMap;

import static data_storage.SystemConfig.INITIALDYNAMICDATASIZE;

public class DynamicData
{
    private static DynamicData instance = null;

    private final HashMap<String, DynamicClassDataEntry> data = new HashMap<>(INITIALDYNAMICDATASIZE);

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

    public HashMap<String, DynamicClassDataEntry> getData()
    {
        return getInstance().data;
    }

    public void clear()
    {
        getInstance().data.clear();
    }
}
