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
    private String averageMethodTime;
    private String totalMethodTime;

    public ApplicationStat(String methodName, long callCount, String averageMethodTime, String totalMethodTime)
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

    public String getTotalMethodTime()
    {
        return totalMethodTime;
    }

    public String getAverageMethodTime()
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

    public void setAverageMethodTime(String averageMethodTime)
    {
        this.averageMethodTime = averageMethodTime;
    }

    public void setTotalMethodTime(String totalMethodTime)
    {
        this.totalMethodTime = totalMethodTime;
    }

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}
}
