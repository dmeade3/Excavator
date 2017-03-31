package Util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcmeade on 3/30/2017.
 */
public enum Time
{
     NANOSECOND("ns",   Arrays.asList(1)),
    MILLISECOND("ms",   Arrays.asList(1000000)),
         SECOND("secs", Arrays.asList(1000000, 1000)),
           HOUR("hrs",  Arrays.asList(1000000, 1000, 60)),
            DAY("days", Arrays.asList(1000000, 1000, 60, 24));


    public String abbreviation;
    private List<Integer> divisorList;

    Time(String abreviation, List<Integer> divisorList)
    {
        this.abbreviation = abreviation;
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
