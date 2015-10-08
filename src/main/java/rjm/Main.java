
package rjm;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import static spark.Spark.*;

public class Main{

    private static class Reader {
        private static String readFile(String pathname) throws IOException {

            Reader instance = new Reader();
            Scanner scanner = new Scanner(instance.getClass().getClassLoader().getResourceAsStream(pathname));
            try {
                return scanner.useDelimiter("\\A").next();
            } finally {
                scanner.close();
            }
        }
    }

    private static class Writer {
        private static void writeFile(String filename, String contents) throws IOException {
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.write(contents);
            fileWriter.close();
        }
    }

    public static void main(String[] args) {

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath"); 
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        VelocityContext context = new VelocityContext();
        Template t=ve.getTemplate("index.html");
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        String templated = writer.toString();

        get("index.html", (request, response) -> {
            String result = Reader.readFile( "index.html" );
            Writer.writeFile( "/tmp/index.html", result );
            return result;
        });
        
        get("css/:name", (request, response) -> {
            String result = Reader.readFile( "css/"+request.params(":name") );
            Writer.writeFile( "/tmp/"+request.params(":name"), result );
            return result;
        });

    }
}
