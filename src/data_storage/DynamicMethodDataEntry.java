package data_storage;


import java.util.ArrayList;
import java.util.List;

public class DynamicMethodDataEntry
{
    private String methodName;
    private int callCount;

    private final List<Long> timesSpentInMethod = new ArrayList<>();


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

    public List<Long> getTimesSpentInMethod()
    {
        return timesSpentInMethod;
    }

    public void addTimeSpentEntry(long entry)
    {
        timesSpentInMethod.add(entry);
    }
}
