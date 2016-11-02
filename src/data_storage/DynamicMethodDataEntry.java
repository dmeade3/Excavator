package data_storage;


import java.util.ArrayList;
import java.util.List;

public class DynamicMethodDataEntry
{
    ///// Fields /////
    private String methodName;
    private int callCount;
    private long averageTime = 0;
    private long totalTime = 0;
    private final List<Long> timesSpentInMethod = new ArrayList<>();

    ///// Methods /////
    public DynamicMethodDataEntry(String methodName)
    {
        this.methodName = methodName;
    }

    public void incrementCallCount()
    {
        callCount++;
    }

    public void addTimeSpentEntry(long entry)
    {
        if (averageTime == 0)
        {
            averageTime = entry;
        }
        else
        {
            averageTime += entry;
            averageTime /= 2;
        }

        totalTime += entry;
        timesSpentInMethod.add(entry);
    }

    ///// Getters /////
    public long getAverageTime()
    {
        return averageTime;
    }

    public long getTotalTime()
    {
        return totalTime;
    }

    public int getCallCount() {
        return callCount;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public List<Long> getTimesSpentInMethod()
    {
        return timesSpentInMethod;
    }
}
