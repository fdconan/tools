package jm.tools.service.annotations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public final class AnnotatedClassScanner {
	private static final Log LOG = LogFactory.getLog(AnnotatedClassScanner.class);
	private final ClassLoader classloader;
	private final Set<String> annotations;
	private Set<Class<?>> classes;
	private final AnnotatedClassVisitor classVisitor = new AnnotatedClassVisitor();

	public AnnotatedClassScanner(Class<?>[] annotations) {
		this.classloader = Thread.currentThread().getContextClassLoader();
		this.annotations = getAnnotationSet(annotations);
		this.classes = new HashSet<Class<?>>();
	}

	public Set<Class<?>> scan(String[] packages) {
		for (String p : packages) {
			String fileP = p.replace('.', '/');
			try {
				Enumeration<URL> urls = this.classloader.getResources(fileP);
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					try {
						URI uri = url.toURI();
						index(uri, p);
					} catch (URISyntaxException e) {
						LOG.warn("URL, " + url
								+ "cannot be converted to a URI");
					}
				}
			} catch (IOException e) {
				String s = "The jobs for the package " + p
						+ ", could not be obtained";
				LOG.error(s);
				throw new RuntimeException(s, e);
			}
		}
		return this.classes;
	}
	
	public void scan(String[] packages, IAnnotationCallback callback) {
		this.scan(packages);
		for(Class<?> annotatedClass : this.classes){
			if(callback != null){
				callback.processAnnotation(annotatedClass);
			}
		}
	}

	private void index(URI u, String filePackageName) {
		String scheme = u.getScheme();
		if (scheme.equals("file")) {
			File f = new File(u.getPath());
			if (f.isDirectory())
				indexDir(f, false);
			else {
				LOG.warn("URL, " + u + ", is ignored. The path, "
						+ f.getPath() + ", is not a directory");
			}

		}
	}

	private void indexDir(File root, boolean indexJars) {
		for (File child : root.listFiles())
			if (child.isDirectory())
				indexDir(child, indexJars);
			else if ((indexJars) && (child.getName().endsWith(".jar"))) {
				// indexJar(child);
			} else if (child.getName().endsWith(".class")) {
				analyzeClassFile(child.toURI());
			}
	}

	private void analyzeClassFile(URI classFileUri) {
		getClassReader(classFileUri).accept(this.classVisitor, 0);
	}

	private ClassReader getClassReader(URI classFileUri) {
		InputStream is = null;
		try {
			is = classFileUri.toURL().openStream();
			ClassReader cr = new ClassReader(is);
			return cr;
		} catch (IOException ex) {
			String s = "Error accessing input stream of the class file URI, "
					+ classFileUri;

			LOG.error(s);
			throw new RuntimeException(s, ex);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ex) {
				String s = "Error closing input stream of the class file URI, "
						+ classFileUri;

				LOG.error(s);
			}
		}
	}

	private Class<?> getClassForName(String className) {
		String s;
		try {
			return this.classloader.loadClass(className);
		} catch (ClassNotFoundException ex) {
			s = "A (root resource) class file of the class name, " + className
					+ "is identified but the class could not be loaded";

			LOG.error(s);
			throw new RuntimeException(s, ex);
		}

	}

	private Set<String> getAnnotationSet(Class<?>[] annotations) {
		Set<String> a = new HashSet<String>();
		for (Class<?> cls : annotations) {
			a.add("L" + cls.getName().replaceAll("\\.", "/") + ";");
		}
		return a;
	}

	private final class AnnotatedClassVisitor implements ClassVisitor {
		private String className;
		private boolean isScoped;
		private boolean isAnnotated;

		private AnnotatedClassVisitor() {
		}

		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			this.className = name;
			this.isScoped = ((access & 0x1) != 0);
			this.isAnnotated = false;
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			this.isAnnotated |= AnnotatedClassScanner.this.annotations
					.contains(desc);
			
			return null;
		}

		public void visitInnerClass(String name, String outerName,
				String innerName, int access) {
			if (this.className.equals(name)) {
				this.isScoped = ((access & 0x1) != 0);

				this.isScoped &= (access & 0x8) == 8;
			}
		}

		public void visitEnd() {
			if ((this.isScoped) && (this.isAnnotated)) {
				AnnotatedClassScanner.this.classes
						.add(AnnotatedClassScanner.this
								.getClassForName(this.className.replaceAll("/",
										".")));
			}
		}

		public void visitOuterClass(String string, String string0,
				String string1) {
		}

		public FieldVisitor visitField(int i, String string, String string0,
				String string1, Object object) {
			return null;
		}

		public void visitSource(String string, String string0) {
		}

		public void visitAttribute(Attribute attribute) {
		}

		public MethodVisitor visitMethod(int i, String string, String string0,
				String string1, String[] string2) {
			return null;
		}
	}
	/*
	public static void main(String[] args) throws Exception{
		AnnotatedClassScanner scanner = new AnnotatedClassScanner(new Class<?>[]{Service.class});
		scanner.scan(new String[]{"service"}, new IAnnotationCallback(){

			public void processAnnotation(Class<?> annotatedClass) {
				if(annotatedClass.isAnnotationPresent(Service.class)){
					Service service = (Service)annotatedClass.getAnnotation(Service.class);
					ServiceBuilder sm = new ServiceBuilder();
					sm.setService(annotatedClass);
					sm.setValidators(service.validators());
					sm.setSingleton(service.singleton());
					ServiceContainer.registerService(service.id(), sm);
				}
			}
			
		});
		
		IService service = ServiceFactory.createService("TestService");
		service.doService(null,null, null);
		service = ServiceFactory.createService("TestService");
		service.doService(null,null, null);
	}
	*/
}
