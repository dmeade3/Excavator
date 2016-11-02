package dynamic_analysis;


import data_storage.SystemConfig;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class Profiler implements ClassFileTransformer
{
    private Instrumentation instrumentation = null;
    private ClassPool classPool;
    private List<String> filterList = new ArrayList<>(10);

    public static void premain(String agentArgs, Instrumentation inst)
    {
        System.out.println("Profile Agent Started...");
        Profiler profiler = new Profiler(inst);
    }

    public Profiler(Instrumentation inst)
    {
        if (SystemConfig.FILTEROUTNONUSERMETHODS)
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
                    method.insertBefore("long timeStampzzzz = System.nanoTime(); System.out.println(timeStampzzzz + \" Entering " + method.getLongName() + "\");");

                    method.insertAfter("long timeStampzzzz = System.nanoTime(); System.out.println(timeStampzzzz + \" Exiting  " + method.getLongName() + "\");");
                }
            }

            for (CtConstructor constructor : constructors)
            {
                if (filter(constructor.getLongName(), className))
                {
                    constructor.insertBefore("long timeStampzzzz = System.nanoTime(); System.out.println(timeStampzzzz + \" Entering " + constructor.getLongName() + "\");");

                    constructor.insertAfter("long timeStampzzzz = System.nanoTime(); System.out.println(timeStampzzzz + \" Exiting  " + constructor.getLongName() + "\");");
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