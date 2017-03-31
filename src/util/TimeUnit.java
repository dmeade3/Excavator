package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcmeade on 3/30/2017.
 */
public enum TimeUnit
{
     NANOSECOND("ns",   Arrays.asList(1)),
	MICROSECOND("si",   Arrays.asList(1000)),
    MILLISECOND("ms",   Arrays.asList(1000, 1000)),
         SECOND("secs", Arrays.asList(1000, 1000, 1000)),
		 MINUTE("secs", Arrays.asList(1000, 1000, 1000, 60)),
           HOUR("hrs",  Arrays.asList(1000, 1000, 1000, 60, 60)),
            DAY("days", Arrays.asList(1000, 1000, 1000, 60, 60, 24));


    public String abbreviation;
    private List<Integer> divisorList;

    // System times can go here as a util feature
	public static float OUTSIDE_PROGRAM_DYNAMIC_EXECUTION_TIME = -1;

    TimeUnit(String abbreviation, List<Integer> divisorList)
    {
        this.abbreviation = abbreviation;
        this.divisorList = divisorList;
    }

	public BigDecimal convertTimeBigDecimal(BigDecimal in)
	{
		BigDecimal out = in;

		for (Integer divisor : divisorList)
		{
			out = out.divide(new BigDecimal(divisor), SystemConfig.TIME_PRECISION_PLACES, RoundingMode.HALF_UP);
		}

		return out;
	}

	public String convertTimeLongToString(float in)
	{
		BigDecimal out = new BigDecimal(in);

		for (Integer divisor : divisorList)
		{
			out = out.divide(new BigDecimal(divisor), SystemConfig.TIME_PRECISION_PLACES, RoundingMode.HALF_UP);
		}

		return out.toString();
	}
}
