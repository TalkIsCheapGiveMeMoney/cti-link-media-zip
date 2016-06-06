package com.tinet.ctilink.mediazip.servlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.tinet.ctilink.aws.AwsSQSService;
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.mediazip.inc.MediaZipConst;
import com.tinet.ctilink.mediazip.inc.MediaZipMacro;
import com.tinet.ctilink.util.ContextUtil;

@WebServlet("/interface/upload")
public class Upload extends HttpServlet {

	//AwsSQSService sqsService;
	
	@Override
	public void init() throws ServletException {
		//sqsService = ContextUtil.getBean(AwsSQSService.class);
	}
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        

        
        //设置工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        String path = MediaZipConst.LOCAL_UPLOAD_PATH;
        //设置文件存储位置  
        factory.setRepository(new File(path));  
        //设置大小
        factory.setSizeThreshold(MediaZipConst.MAX_FILE_SIZE);  
     
        ServletFileUpload upload = new ServletFileUpload(factory);  
        upload.setHeaderEncoding("utf-8");  
        boolean uploaded = false;
        String fileName = null;
        try {  
        	List<FileItem> list = upload.parseRequest(req);  
        	for(FileItem item : list){  
        		if(item.isFormField()){  
        			String name = item.getFieldName();  
        			String value =item.getString("utf-8");  
        			req.setAttribute(name, value);  
        		}   
        		else{  
        			String value = item.getName();  
                    int start = value.lastIndexOf("\\");  
                    fileName = value.substring(start + 1);  
                    //写文件到path目录，文件名问filename  
                    item.write(new File(path,fileName));
                    uploaded = true;
                    break;
                    
             	} 
        	} 
        	if(uploaded){
        		String enterpriseId = req.getAttribute("enterpriseId").toString();
     	        String day = req.getAttribute("day").toString();
     	        String ratio = req.getAttribute("ratio").toString();
     	        String type = req.getAttribute("type").toString();
     	        
	        	StringBuilder sb = new StringBuilder();
	            sb.append("enterprise_id=").append(enterpriseId).append("\n");
	            sb.append("day=").append(day).append("\n");
	            sb.append("wav_path=").append(path).append("\n");
	            sb.append("wav_file=").append(fileName).append("\n");
	            sb.append("ratio=").append(ratio).append("\n");
	            sb.append("type=").append(type).append("\n");
	            sb.append("aws_s3_path=").append(MediaZipMacro.AWS_MEDIA_ZIP_S3_BUCKET).append("\n");
	            
	            File file =new File(MediaZipConst.MONITOR_PATH + "/" + fileName.substring(0, fileName.indexOf(".wav")));
	            try{
	    			if(file.exists()){
	    				JSONObject object = new JSONObject();
	    				object.put("res", -1);
	    				out.print(object);
	    		        out.close();
	    				return;
	    			}else{
	    				file.createNewFile();	
	    			}
	    			FileWriter fileWriter=new FileWriter(file, true);
	    			fileWriter.write(sb.toString());
	    			fileWriter.close();
	    			JSONObject object = new JSONObject();
	    			object.put("res", 0);
	    			out.print(object);
	    			out.close();
	    			return;
	    		}catch (Exception e) {
	    			e.printStackTrace();
	    		}		
	            //sqsService.sendMessage(sb.toString(), MediaZipMacro.AWS_MEDIA_ZIP_SQS_URL);
        	}
        }catch (FileUploadException e) {  
        	e.printStackTrace(); 
        } catch (Exception e) {  
        	e.printStackTrace();  
        }  
		JSONObject object = new JSONObject();
		object.put("res", -1);
		out.print(object);
        out.close();
    }
    public static void main(String[] argv){
    	String fileName = "default.wav";
    	System.out.println(fileName.substring(0, fileName.indexOf(".wav")));
    	
    }
}