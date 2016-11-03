package gui;


import data_storage.SystemConfig;

public class FormattedNumber implements Comparable
{
    private float number;
    private String units = "";

    public FormattedNumber(float number, String units)
    {
        this.number = number;
        this.units = units;
    }

    ///// Getters /////
    public float getNumber()
    {
        return number;
    }

    public String getUnits()
    {
        return units;
    }

    ///// Setters /////
    public void setNumber(float number)
    {
        this.number = number;
    }

    public void setUnits(String units)
    {
        this.units = units;
    }

    ///// Override Methods /////
    @Override
    public int compareTo(Object o)
    {
        FormattedNumber outsideObj = (FormattedNumber) o;

        return (int) (this.getNumber() - outsideObj.getNumber());
    }

    @Override
    public String toString()
    {
        return SystemConfig.NUMBERFORMATER.format(number) + units;
    }
}
