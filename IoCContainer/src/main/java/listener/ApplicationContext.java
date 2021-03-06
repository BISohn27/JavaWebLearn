package listener;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.reflections.Reflections;

import controller.Component;

//ContextLoaderListener로부터 properties가 있는 주소를 얻어와 properties 내 key 값으로 매핑된 값을 바탕으로 객체를 생성하여
//hashtable에 key값으로 저장하는 IoC container 역할
public class ApplicationContext {
	Hashtable<String, Object> objTable = new Hashtable<>();
	
	public Object getBean(String key) {
		return objTable.get(key);
	}
	//생성자에서 매개변수로 properties의 실제 경로를 입력 받아
	//properties의 키 값으로부터 실제 값을 읽어 객체를 생성하는 작업을 한다.
	public ApplicationContext(String propertiesPath) throws Exception{
		Properties props = new Properties();
		props.load(new FileReader(propertiesPath));
		//메소드를 통해 IoC container에 저장되어할 객체들을 만들어낸다.
		prepareObjects(props);
		prepareAnnotationObjects();
		injectDependency();
	}
	//실제로 properties에서 값을 얻어와 사용되는 controller에 대한 객체들을 키값을 통해 생성한다.
	public void prepareObjects(Properties props) throws Exception{
		Context ctx = new InitialContext();
		String key = null;
		String value = null;
		for(Object item: props.keySet()) {
			key = (String)item;
			value=props.getProperty(key);
			if(key.startsWith("jndi.")) {
				objTable.put(key, ctx.lookup(value));
			}else {
				objTable.put(key, Class.forName(value).newInstance());
			}
		}
	}
	
	private void injectDependency() throws Exception{
		for(String key : objTable.keySet()) {
			if(!key.startsWith("jndi.")) {
				callSetter(objTable.get(key));
			}
		}
	}
	
	private void callSetter(Object obj) throws Exception{
		Object dependency = null;
		
		//reflection 기능, obj.getClass().getMethods() 생성된 객체의 모든 메소드를 배열로 가져오는 메소드
		for(Method m : obj.getClass().getMethods()) {
			if(m.getName().startsWith("set")) {
				dependency = findObjectByType(m.getParameterTypes()[0]);
				if(dependency != null) {
					//메소드를 찾아서 메소드를 호출해주는 역할.
					m.invoke(obj, dependency);
				}
			}
		}
	}
	
	private Object findObjectByType(Class<?> type) {
		for(Object obj: objTable.values()) {
			if(type.isInstance(obj)) {
				return obj;
			}
		}
		return null;
	}
	
	private void prepareAnnotationObjects() throws Exception{
		Reflections reflector = new Reflections("");
		Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
		for(Class<?> clazz : list) {
			String key = clazz.getAnnotation(Component.class).value();
			objTable.put(key, clazz.newInstance());
		}
	}
}
