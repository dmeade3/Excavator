package data_storage;


import java.util.HashMap;

import static data_storage.SystemConfig.INITIALSTATICDATASIZE;


public class StaticData
{
    private static StaticData instance = null;

    private final HashMap<String, StaticClassDataEntry> data = new HashMap<>(INITIALSTATICDATASIZE);

    public static StaticData getInstance()
    {
        if (instance == null)
        {
            instance = new StaticData();
        }

        return instance;
    }

    public void put(String className, StaticClassDataEntry staticClassDataEntry)
    {
        getInstance().data.put(className, staticClassDataEntry);
    }

    public StaticClassDataEntry get(String className)
    {
        return getInstance().data.get(className);
    }

    public boolean contains(String className)
    {
        return getInstance().data.containsKey(className);
    }

    public HashMap<String, StaticClassDataEntry> getData()
    {
        return getInstance().data;
    }

    public void clear()
    {
        getInstance().data.clear();
    }
}
