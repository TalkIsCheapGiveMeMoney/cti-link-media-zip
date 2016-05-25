package com.tinet.ctilink.mediazip.servlet;

import java.io.File;
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
import com.tinet.ctilink.mediazip.inc.MediaZipConst;
import com.tinet.ctilink.mediazip.inc.MediaZipMacro;
import com.tinet.ctilink.util.ContextUtil;

@WebServlet("/interface/upload")
public class Upload extends HttpServlet {

	AwsSQSService sqsService;
	
	@Override
	public void init() throws ServletException {
		AwsSQSService sqsService = ContextUtil.getBean(AwsSQSService.class);
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
        
        String enterpriseId = req.getParameter("enterpriseId");
        String day = req.getParameter("day");
        String ratio = req.getParameter("ratio");
        
        //设置工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        String path = MediaZipConst.LOCAL_UPLOAD_PATH;
        //设置文件存储位置  
        factory.setRepository(new File(path));  
        //设置大小
        factory.setSizeThreshold(MediaZipConst.MAX_FILE_SIZE);  
     
        ServletFileUpload upload = new ServletFileUpload(factory);  
        upload.setHeaderEncoding("utf-8");  
 
        try {  
        	List<FileItem> list = upload.parseRequest(req);  
        	for(FileItem item : list){  
        		if(item.isFormField()){  

        		}   
        		else{  
        			String value = item.getName();  
                    int start = value.lastIndexOf("\\");  
                    String fileName = value.substring(start + 1);  
                    //写文件到path目录，文件名问filename  
                    item.write(new File(path,fileName));
                    StringBuilder sb = new StringBuilder();
                    sb.append("enterpriseId=").append(enterpriseId).append("\r\n");
                    sb.append("day=").append(day).append("\r\n");
                    sb.append("path=").append(path).append("\r\n");
                    sb.append("file=").append(fileName).append("\r\n");
                    sb.append("ratio=").append(ratio).append("\r\n");

                    sqsService.sendMessage(sb.toString(), MediaZipMacro.AWS_MEDIA_ZIP_SQS_URL);
             	}  
        	}  
        }catch (FileUploadException e) {  
        	e.printStackTrace(); 
        } catch (Exception e) {  
        	e.printStackTrace();  
        }  
        out.close();
    }
}