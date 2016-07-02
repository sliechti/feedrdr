package feedreader.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

public class FormUploadHelper 
{
    /**
     * Field value is declared as array because of multi select
     */
    HashMap<String, ArrayList<String>> fields = new HashMap<>();
    HashMap<String, ByteArrayOutputStream> files = new HashMap<>();
    
    public static boolean isMultiPartContent(HttpServletRequest request)
    {
        return ServletFileUpload.isMultipartContent(request);
    }

    public FormUploadHelper(HttpServletRequest request) throws FileUploadException, IOException
    {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter = upload.getItemIterator(request);
        
        while (iter.hasNext())
        {
            FileItemStream item = iter.next();
                            
            if (!item.isFormField())
            {
                InputStream is = item.openStream();
                
                ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());

                int c;
                while ((c = is.read()) != -1) {
                    bos.write(c);
                }
                
                files.put(item.getFieldName(), bos);
            }
            else
            {
                StringBuilder sb = TextUtils.toStringBuilder(item.openStream(), new StringBuilder(), true);
                ArrayList<String> a = fields.get(item.getFieldName());
                if (a == null) {
                    a = new ArrayList<>();
                }
                a.add(sb.toString().trim());
                fields.put(item.getFieldName(), a);
            }
        }
    }
    
    public String asString(String fieldName, String def) 
    {
        ArrayList<String> a = fields.get(fieldName);
        if (a == null || a.isEmpty()) return def;
        return a.get(0);
    }
    
    public ArrayList<String> asString(String fieldName)
    {
        return fields.get(fieldName);
    }
    
    public boolean asBoolean(String fieldName, boolean def) 
    {    
        String val = asString(fieldName, "");
        if (val == null || val.isEmpty()) return def;
        return true; // checkbox selected. It has no value.
    }
    
    public long asLong(String fieldName, long def) 
    {    
        String val = asString(fieldName, "");
        if (val == null || val.isEmpty()) return def;
        
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
    public ByteArrayOutputStream asStream(String fieldName) {
        return files.get(fieldName);
    }
    
}
