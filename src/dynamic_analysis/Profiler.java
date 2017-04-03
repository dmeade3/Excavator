package dynamic_analysis;

import execute_jar.ExecuteJarUtil;
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import static util.SystemConfig.*;

public class Profiler implements ClassFileTransformer
{
    private Instrumentation instrumentation = null;
    private ClassPool classPool;
    private List<String> filterList = new ArrayList<>(15);

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
        if (ExecuteJarUtil.FILTER_OUT_NON_USER_METHODS)
        {
            filterList.add("java");
            filterList.add("sun");
            filterList.add("com.intellij.");
            filterList.add("com.javafx.");
            filterList.add("com.sun.javafx.");
            filterList.add("com.sun.glass.");
            filterList.add("com.sun.prism.");
            filterList.add("com.sun.scenario.");
        }

        // No method body classes (can cause errors with javassist)
        if (ExecuteJarUtil.FILTER_OUT_ERROR_CAUSING_METHODS)
        {
            filterList.add("java.lang.Shutdown$Lock");
        }

        instrumentation = inst;
        classPool = ClassPool.getDefault();
        instrumentation.addTransformer(this);
    }

	// TODO look at the efficiency for this whole class
    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        try
        {
            /*if (FILTER_OUT_NON_USER_METHODS && loader == null)
            {
                return classfileBuffer;
            }*/

            className = className.replaceAll("/", ".");

            classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
            CtClass cc = classPool.get(className);

            CtMethod[] methods = cc.getDeclaredMethods();
            CtConstructor[] constructors = cc.getConstructors();

            for (CtMethod method : methods)
            {
                // See if you can filter outside this loop
                if (filter(method.getLongName(), className))
                {
                    method.insertBefore("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + ENTERING + " " + method.getLongName() + "\");");

                    method.insertAfter("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + EXITING + " " + method.getLongName() + "\");");
                }
            }

            for (CtConstructor constructor : constructors)
            {
                // See if you can filter outside this loop
                if (filter(constructor.getLongName(), className))
                {
                    constructor.insertBefore("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + ENTERING + " " + constructor.getLongName() + "\");");

                    constructor.insertAfter("long " + TIMESTAMP_VARIABLE + " = System.nanoTime(); System.out.println(" + TIMESTAMP_VARIABLE + " + \" " + EXITING + " " + constructor.getLongName() + "\");");
                }
            }

            // return the new bytecode array:
            return cc.toBytecode();
        }
        catch (IOException | NotFoundException e)
        {
            e.printStackTrace();
        }
        catch (CannotCompileException e)
        {
            System.out.println("Cannot Compile: " + e.toString());
        }

        return classfileBuffer;
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