package data_storage;

public class StaticMethodDataEntry
{
    ///// Fields /////
    private String methodName;

    // TODO make a problem object that holds methodname, classname, line number and problem description
    // TODO Have array of objects for every method
    // TODO Utility class just for problem descriptions
    // TODO display the problems in addition results

    private int forLoopCount = 0;
    private int whileLoopCount = 0;



    // TODO
    // Complexity score is determined by methods called
    //
    // method called not in loop +1
    //
    // method called inside one layer of looping +10
    //
    // method called inside 2 layers of looping +100
    //
    // ....
    //
    // layer of loop is either in a for loop or in a while loop
    //
    // TODO
    private int complexityScore;



    ///// Methods /////
    public StaticMethodDataEntry(String methodName)
    {
        this.methodName = methodName;
    }




    ///// Getters /////


    public String getMethodName()
    {
        return methodName;
    }


}
