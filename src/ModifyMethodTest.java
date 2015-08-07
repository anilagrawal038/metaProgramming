
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import javassist.*;

public class ModifyMethodTest {

    /**
     * Creates a new ModifyMethodTest
     *
     * @param className The internal form class name to modify
     * @param methodName The name of the method to transform
     * @param methodSignature A regular expression to match the method
     * signature. (if null, matches ".*")
     * @param classLoader The intrumentation provided classloader
     * @param byteCode The pre-transform byte code
     * @return the modified byte code if successful, otherwise returns the
     * original unmodified byte code
     */
    public static byte[] instrument(String className, String methodName, String methodSignature, ClassLoader classLoader, byte[] byteCode) {
        String binName = className.replace('/', '.');
        try {
            ClassPool cPool = new ClassPool(true);
            cPool.appendClassPath(new LoaderClassPath(classLoader));
            cPool.appendClassPath(new ByteArrayClassPath(binName, byteCode));
            CtClass ctClazz = cPool.get(binName);
            Pattern sigPattern = Pattern.compile((methodSignature == null || methodSignature.trim().isEmpty()) ? ".*" : methodSignature);
            System.out.println("ctClazz.getName() : " + ctClazz.getName());
            System.out.println("ctClazz.getPackageName() : " + ctClazz.getPackageName());
            System.out.println("ctClazz.getSimpleName() : " + ctClazz.getSimpleName());
            System.out.println("ctClazz.getName() : " + ctClazz.getName());
            Class retransformClass = Class.forName("Retransform" + ctClazz.getSimpleName());
            int modifies = 0;
            for (CtMethod method : ctClazz.getDeclaredMethods()) {
                System.out.println("method.getName() : " + method.getName());
                System.out.println("method.getLongName() : " + method.getLongName());
                System.out.println("method.getSignature() : " + method.getSignature());
                if (method.getName().equals(methodName)) {
                    if (sigPattern.matcher(method.getSignature()).matches()) {
                        for (Field retransforField : retransformClass.getDeclaredFields()) {
                            System.out.println("retransforField.getName() : " + retransforField.getName());
                            if (retransforField.getName().equals(fetchMethodString(method))) {
                                ctClazz.removeMethod(method);
                                String methodBody = (String) retransforField.get(null);
                                System.out.println("new methos body ==> " + methodBody);
                                method.setBody(methodBody);
                                ctClazz.addMethod(method);
                                modifies++;
                            }
                        }
                    }
                }
            }

            System.out.println("[ModifyMethodTest] Intrumented [" + modifies + "] methods");
            return ctClazz.toBytecode();
        } catch (Exception ex) {
            System.err.println("Failed to compile retransform class [" + binName + "] Stack trace follows...");
            ex.printStackTrace(System.err);
            return byteCode;
        }
    }

    static String fetchMethodString(CtMethod method) {
        String methodString = method.getLongName().replaceAll("\\.", "_");
        methodString = methodString.replaceAll("\\(", "_");
        methodString = methodString.replaceAll("\\)", "_");
        methodString = methodString.replaceAll("\\,", "_");
        return methodString;
    }

}
