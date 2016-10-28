package gui;

/*
*
* Whenever a new column is added there needs to be a new field in here to represent it
*
* */

public class ApplicationStat
{

    private String methodName;
    private long callCount;
    private long averageMethodTime;
    private long totalMethodTime;

    public ApplicationStat(String methodName, long callCount, long averageMethodTime, long totalMethodTime)
    {
        this.methodName = methodName;
        this.callCount = callCount;
        this.averageMethodTime = averageMethodTime;
        this.totalMethodTime = totalMethodTime;

    }

    // Getters
    public String getmethodName()
    {
        return methodName;
    }

    public long getCallCount()
    {
        return callCount;
    }

    public long getTotalMethodTime()
    {
        return totalMethodTime;
    }

    public long getAverageMethodTime()
    {
        return averageMethodTime;
    }

    // Setters
    public void setStat(String methodName)
    {
        this.methodName = methodName;
    }

    public void setCallCount(long callCount)
    {
        this.callCount = callCount;
    }

    public void setAverageMethodTime(long averageMethodTime)
    {
        this.averageMethodTime = averageMethodTime;
    }

    public void setTotalMethodTime(long totalMethodTime)
    {
        this.totalMethodTime = totalMethodTime;
    }
}
