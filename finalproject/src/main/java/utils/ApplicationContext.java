package utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContext {
	
	public final static String PATH = "conf/application-context.xml";
	
	public static Object getBean(String id){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(PATH);
		Object o = context.getBean(id);
		return o;
	}

}
