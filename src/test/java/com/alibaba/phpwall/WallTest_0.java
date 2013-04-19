package com.alibaba.phpwall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.caucho.quercus.QuercusContext;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.quercus.statement.Statement;
import com.caucho.vfs.FilePath;

public class WallTest_0 extends TestCase {
	public void test_0() throws Exception {
		QuercusContext context = new QuercusContext();
		context.init();
		context.start();
		
		Env env = new Env(context);
		env.start();
		
		QuercusParser parser = new QuercusParser(context);
		
		String str = "/home/wenshao/Desktop/2013-04/php_samples/onst.php";
		FilePath path = new FilePath(str);
		String encoding = "UTF-8";
		QuercusProgram program = parser.parse(context, path, encoding);
		program.init(env);
		
		Statement stmt = program.getStatement();
		
		WallIterator iterator = new WallIterator(context, env);
		
		iterator.checkStatement(stmt);
		
		
		System.out.println(stmt);
	}
}
