package data_storage;

import java.util.HashMap;

public class DynamicClassDataEntry
{
    private String className;
    private final  HashMap<String, DynamicMethodDataEntry> methodStore = new HashMap<>();

    public DynamicClassDataEntry(String className)
    {
        this.className = className;
    }

    public void put(String methodName, DynamicMethodDataEntry dynamicFileDataEntry)
    {
        methodStore.put(methodName, dynamicFileDataEntry);
    }

    public HashMap<String, DynamicMethodDataEntry> getData()
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
