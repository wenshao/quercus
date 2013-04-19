package com.alibaba.phpwall;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import com.caucho.quercus.QuercusContext;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.page.InterpretedPage;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.quercus.statement.Statement;
import com.caucho.vfs.FilePath;
import com.caucho.vfs.WriteStream;
import com.caucho.vfs.WriterStreamImpl;

public class WallTest_0 extends TestCase {
	public void test_0() throws Exception {
		QuercusContext context = new QuercusContext();
		context.init();
		context.start();
		
		Writer writer = new StringWriter(); 
		WriterStreamImpl s = new WriterStreamImpl();
        s.setWriter(writer);
        WriteStream out = new WriteStream(s);
		
		QuercusParser parser = new QuercusParser(context);
		
		String str = "/home/wenshao/Desktop/2013-04/php_samples/onst.php";
		FilePath path = new FilePath(str);
		String encoding = "UTF-8";
		QuercusProgram program = parser.parse(context, path, encoding);
		
		QuercusPage page = new InterpretedPage(program);
		Env env = new Env(context, page, out, null, null);
		
		Statement stmt = program.getStatement();
		
		WallIterator iterator = new WallIterator(context, env);
		
		iterator.checkStatement(stmt);
		
		
		System.out.println(stmt);
	}
}
