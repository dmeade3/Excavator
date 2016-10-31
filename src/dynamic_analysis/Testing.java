package dynamic_analysis;

import java.lang.reflect.*;

public class Testing {
    private int f1(Object p, int x) throws NullPointerException
    {
        if (p == null)
            throw new NullPointerException();
        return x;
    }

    public static void main(String args[])
    {
        try {
            Class cls = Class.forName("dynamic_analysis.Testing");

            Method methlist[] = cls.getDeclaredMethods();
            for (int i = 0; i < methlist.length; i++)
            {
                Method m = methlist[i];
                System.out.println("method name  = " + m.getName());
                System.out.println("declared in  = " + m.getDeclaringClass());

                Class parameterTypes[] = m.getParameterTypes();
                for (int j = 0; j < parameterTypes.length; j++)
                {
                    System.out.println("param #" + j + "     = " + parameterTypes[j]);
                }

                Class exceptionTypes[] = m.getExceptionTypes();
                for (int j = 0; j < exceptionTypes.length; j++)
                {
                    System.out.println("exception #" + j + " = " + exceptionTypes[j]);
                }

                System.out.println("return type  = " + m.getReturnType());
                System.out.println("-----");
            }
        }
        catch (Throwable e) {
            System.err.println(e);
        }
    }
}