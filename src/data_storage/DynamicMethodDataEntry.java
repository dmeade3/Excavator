package data_storage;


public class DynamicMethodDataEntry
{
    private String methodName;
    private int callCount;

    public DynamicMethodDataEntry(String methodName)
    {
        this.methodName = methodName;
    }

    public void incrementCallCount()
    {
        callCount++;
    }

    public int getCallCount() {
        return callCount;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }
}
