package Util;

/**
 * Created by dcmeade on 3/30/2017.
 */
public enum Time
{

    //TODO have time divisor here too based off the default nano seconds as the base time

     NANOSECOND("ns",         1),
    MILLISECOND("ms",   NANOSECOND.divisor  * 1000000),
         SECOND("secs", MILLISECOND.divisor * 1000),
           HOUR("hrs",       SECOND.divisor * 6),
            DAY("days",        HOUR.divisor * 24);


    public String abbreviation;
    public long divisor; // TODO needs to be a big integer, also have an easier function for doing the division of the big ints

    Time(String abreviation, long divisor)
    {
        this.abbreviation = abreviation;
        this.divisor = divisor;
    }
}
