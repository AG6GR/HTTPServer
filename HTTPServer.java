import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class HTTPServer
{
	private static final String HEADER_HTTP = "HTTP/1.1 ";
	private static final String HEADER_SERVER = "Server: Sunny's Java Server";
	private static final String HEADER_DATE = "Date: ";
	private static final String HEADER_CONNECTION = "Connection: close";
	private static final String HEADER_TYPE = "Content-Type: ";
	private static final String HEADER_LENGTH = "Content-Length: ";
	private static final String HEADER_LASTMODIFIED = "Last-Modified: ";
	
	private static final int PORT = 8088;
	private static final byte[] buffer = new byte[1024];
	private static HashMap<String, String> MIME_TYPES = null;
	
	static
	{
		MIME_TYPES = new HashMap<String, String>();
		MIME_TYPES.put("html", "text/html");
		MIME_TYPES.put("htm", "text/html");
		MIME_TYPES.put("txt", "text/plain");
		MIME_TYPES.put("text", "text/plain");
		MIME_TYPES.put("csv", "text/csv");
		MIME_TYPES.put("jpeg", "image/jpeg");
		MIME_TYPES.put("jpg", "image/jpeg");
		MIME_TYPES.put("png", "image/png");
		MIME_TYPES.put("gif", "image/gif");
		MIME_TYPES.put("js", "application/javascript");
		MIME_TYPES.put("pdf", "application/pdf");
		MIME_TYPES.put("zip", "application/zip");
	}
	
	
	private static String getMIME(String filename)
	{
		System.out.println(filename);
		String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
		String ans = MIME_TYPES.get(ext);
		if (ans != null)
		{
			System.out.println(ans);
			return ans;
		}
		else
		{
			System.out.println("default");
			return "application/octet-stream";	
		}
		
	}
	private static String listDirectory(File directory, String rootDir)
	{
		File[] fileList = directory.listFiles();
		SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy HH:mm:ss");
		String date = "";
		
		String text = 
		"<HTML>\n" +
		     "<HEAD>\n" +
		       "<TITLE>" +
			 "Index of " + directory.getPath().substring(rootDir.length()) + "\n" +
		       "</TITLE>\n" +
		     "</HEAD>\n" +
		     "<BODY>\n" +
		     "<TT>\n" +
		       "<TABLE>\n" +
			 "<TR>\n" +
			   "<TD>Name</TD>\n" +
			   "<TD>Size</TD>\n" +
			   "<TD>Type</TD>\n" +
			   "<TD>Last Modified</TD>\n" +
			 "</TR>\n";
		for (File file : fileList)
		{
			
			date = dateFormat.format(new Date(file.lastModified()));
			text += "<TR>\n" +
			   "<TD>\n" +
			     "<A HREF=\"" + file.getPath().substring(rootDir.length()).replaceAll("\\\\", "/") + "\">\n" +
			       file.getName() +
			     "</A>\n" +
			   "</TD>\n" +
			   "<TD> " + file.length() + " </TD>\n" +
			   "<TD> \n";
			   if (file.isFile())
			   {
			   	text += "File";
			   }
			   else
			   {
			   	text += "Directory";   
			   }
			   text += " </TD>\n" +
			   "<TD> " + date  + "</TD>\n" +
			"</TR>\n";
			
		}
		text +=	"</TABLE>\n" +
		"</TT>\n" +
		"</BODY>\n" +
		"</HTML>";
		
		return text;
				
	}
	
	public static void main(String[] args)
	{
		String DOC_ROOT = System.getProperty("user.dir");
		System.out.println("DOC_ROOT: " + DOC_ROOT);
		try
		{
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("HTTPServer running on port " + PORT);
			while (true)
			{
				Socket clientSocket = serverSocket.accept();
				OutputStream outStream = clientSocket.getOutputStream();
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outStream));
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String line = in.readLine();
				System.out.println(line);
				if (line == null)
				{
					out.close();
					in.close();
					clientSocket.close();
					continue;
				}
				else if(!line.startsWith("GET"))
				{
					out.close();
					in.close();
					clientSocket.close();
					continue;
				}
				String[] input = line.split(" ");
				
				String date = "";
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
				File requestFile = new File(DOC_ROOT + input[1]);
				if (requestFile.exists())
				{
					
					//String date = "";
					//SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
					if (requestFile.isFile())
					{
						out.write(HEADER_HTTP + "200 OK\r\n");
						out.write(HEADER_SERVER + "\r\n");
						date = dateFormat.format(new Date());
						out.write(HEADER_DATE + date + "\r\n");
						out.write(HEADER_CONNECTION + "\r\n");
						out.write(HEADER_TYPE + getMIME(input[1]) + "\r\n");
						out.write(HEADER_LENGTH + requestFile.length() + "\r\n");
						date = dateFormat.format(new Date(requestFile.lastModified()));
						out.write(HEADER_LASTMODIFIED + date +"\r\n");
						
						out.write("\r\n");
						out.flush();
						FileInputStream fis = new FileInputStream(requestFile);
						int length;
						while ((length = fis.read(buffer)) > 0)
						{
							outStream.write(buffer, 0, length);
						}
						outStream.flush();
					}
					else
					{
						out.write(HEADER_HTTP + "200 OK\r\n");
						out.write(HEADER_SERVER + "\r\n");
						date = dateFormat.format(new Date());
						out.write(HEADER_DATE + date + "\r\n");
						out.write(HEADER_CONNECTION + "\r\n");
						out.write(HEADER_TYPE + "text/html" + "\r\n");
						//out.write(HEADER_LENGTH + requestFile.length() + "\r\n");
						//date = dateFormat.format(new Date(requestFile.lastModified()));
						//out.write(HEADER_LASTMODIFIED + date +"\r\n");
						
						out.write("\r\n");
						out.flush();
						out.write(listDirectory(requestFile, DOC_ROOT));
						outStream.flush();
					}
				}
				else
				{
					out.write(HEADER_HTTP + "404 Not Found\r\n");
					out.write(HEADER_SERVER + "\r\n");
					date = dateFormat.format(new Date());
					out.write(HEADER_DATE + date + "\r\n");
					out.write(HEADER_TYPE + "text/plain\r\n");
					out.write("\r\n");
					out.write("File " + input[1] + " not found");
					out.flush();
				}
				
				out.close();
				in.close();
				clientSocket.close();
			}
			//serverSocket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}