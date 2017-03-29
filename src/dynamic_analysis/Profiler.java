package dynamic_analysis;


import Util.SystemConfig;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import static Util.SystemConfig.ENTERING;
import static Util.SystemConfig.EXITING;
import static Util.SystemConfig.TIMESTAMP_VARIABLE;

public class Profiler implements ClassFileTransformer
{
    private Instrumentation instrumentation = null;
    private ClassPool classPool;
    private List<String> filterList = new ArrayList<>(10);

    public static void premain(String agentArgs, Instrumentation inst)
    {
        System.out.println("Profile Agent Started...");

        // Start the profiler
        Profiler.run(inst);
    }

    private static void run(Instrumentation inst)
    {
        new Profiler(inst);
    }

    public Profiler(Instrumentation inst)
    {
        if (SystemConfig.FILTER_OUT_NON_USER_METHODS)
        {
            filterList.add("java");
            filterList.add("sun");
            filterList.add("com.intellij.");
        }

        instrumentation = inst;
        classPool = ClassPool.getDefault();
        instrumentation.addTransformer(this);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        try
        {
            className = className.replaceAll("/", ".");

            classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
            CtClass cc = classPool.get(className);

            CtMethod[] methods = cc.getDeclaredMethods();
            CtConstructor[] constructors = cc.getConstructors();

            for (CtMethod method : methods)
            {
                if (filter(method.getLongName(), className))
                {
                    method.insertBefore("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + ENTERING + " " + method.getLongName() + "\");");

                    method.insertAfter("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + EXITING + " " + method.getLongName() + "\");");
                }
            }

            for (CtConstructor constructor : constructors)
            {
                if (filter(constructor.getLongName(), className))
                {
                    constructor.insertBefore("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + ENTERING + " " + constructor.getLongName() + "\");");

                    constructor.insertAfter("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + EXITING + " " + constructor.getLongName() + "\");");
                }
            }

            // return the new bytecode array:
            byte[] newClassfileBuffer = cc.toBytecode();
            return newClassfileBuffer;
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        return null;
    }

    private boolean filter(String longName, String className)
    {
        boolean result = longName.startsWith(className);

        // To skip the loop
        if (!result)
        {
            return false;
        }

        for (String filterItem : filterList)
        {
            if (longName.startsWith(filterItem))
            {
                result = false;
                break;
            }
        }

        return result;
    }
}