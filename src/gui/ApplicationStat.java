package gui;

import javafx.beans.property.SimpleStringProperty;

/*
*
* Whenever a new column is added there needs to be a new field in here to represent it
*
* */

public class ApplicationStat
{

    // TODO organize so is easily added to

    private SimpleStringProperty methodName;
    private long callCount;


    public ApplicationStat(String methodName, int callCount)
    {
        this.methodName = new SimpleStringProperty(methodName);
        this.callCount = callCount;
    }


    // Getters
    public String getmethodName()
    {
        return methodName.get();
    }

    public long getCallCount()
    {
        return callCount;
    }

    // Setters
    public void setStat(String name)
    {
        methodName.set(name);
    }

    public void setCallCount(long callCount)
    {
        this.callCount = callCount;
    }
}
