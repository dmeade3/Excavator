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
        data.put(className, dynamicFileDataEntry);
    }

    public DynamicClassDataEntry get(String className)
    {
        return data.get(className);
    }

    public boolean contains(String className)
    {
        return data.containsKey(className);
    }

    public HashMap<String, DynamicClassDataEntry> getData()
    {
        return data;
    }
}
