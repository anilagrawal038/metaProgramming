public interface TransformerServiceMBean {
  /**
	 * Transforms the target class name
	 * @param className The binary name of the target class
	 * @param methodName  The name of the method to transform
	 * @param methodSignature A regular expression to match the method signature. (if null, matches ".*")
	 */
	public void transformClass(String className, String methodName, String methodSignature);
}