package dynamic_analysis;


import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

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
        // TODO come up with clever way to do this, maybe have these filters in another file
        filterList.add("java");
        filterList.add("sun");
        filterList.add("com.intellij.");

        instrumentation = inst;
        classPool = ClassPool.getDefault();
        instrumentation.addTransformer(this);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        try
        {
            className = className.replaceAll("/",".");

            classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
            CtClass cc = classPool.get(className);
            CtMethod[] methods = cc.getMethods();

            for (int k=0; k < methods.length; k++)
            {
                if (filter(methods[k].getLongName(), className))
                {

                    methods[k].insertBefore("long timeStampzzzz = System.nanoTime(); System.out.println(timeStampzzzz + \" Entering " + methods[k].getLongName() + "\");");

                    methods[k].insertAfter( "long timeStampzzzz = System.nanoTime(); System.out.println(timeStampzzzz + \" Exiting  " + methods[k].getLongName() + "\");");
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