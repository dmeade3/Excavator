package data_storage;


import java.util.ArrayList;
import java.util.List;

public class DynamicMethodDataEntry
{
    private String methodName;
    private int callCount;
    private long averageTime = 0;
    private long totalTime = 0;

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

    public long getAverageTime()
    {
        return averageTime;
    }

    public long getTotalTime()
    {
        return totalTime;
    }
}
