package data_storage;


import java.util.HashMap;

public class StaticClassDataEntry
{
    ///// Fields /////
    private String className;
    private final HashMap<String, StaticMethodDataEntry> methodStore = new HashMap<>();

    ///// Methods /////
    public StaticClassDataEntry(String className)
    {
        this.className = className;
    }

    public void put(String methodName, StaticMethodDataEntry staticMethodDataEntry)
    {
        methodStore.put(methodName, staticMethodDataEntry);
    }

    public HashMap<String, StaticMethodDataEntry> getData()
    {
        return methodStore;
    }

    public StaticMethodDataEntry get(String methodName)
    {
        return methodStore.get(methodName);
    }

    public boolean contains(String methodName)
    {
        return methodStore.containsKey(methodName);
    }

    public String getClassName()
    {
        return className;
    }
}
