package data_storage;

import java.util.HashMap;
import java.util.Map;

public class DynamicClassDataEntry
{
    ///// Fields /////
    private String className;
    private final Map<String, DynamicMethodDataEntry> methodStore = new HashMap<>();

    ///// Methods /////
    public DynamicClassDataEntry(String className)
    {
        this.className = className;
    }

    public void put(String methodName, DynamicMethodDataEntry dynamicFileDataEntry)
    {
        methodStore.put(methodName, dynamicFileDataEntry);
    }

    public Map<String, DynamicMethodDataEntry> getData()
    {
        return methodStore;
    }

    public DynamicMethodDataEntry get(String methodName)
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
