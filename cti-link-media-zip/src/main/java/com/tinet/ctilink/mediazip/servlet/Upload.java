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
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.mediazip.inc.Const;
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
        String ratio = req.getParameter("ratio");
        
        //设置工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        String path = Const.LOCAL_UPLOAD_PATH;
        //设置文件存储位置  
        factory.setRepository(new File(path));  
        //设置大小，如果文件小于设置大小的话，放入内存中，如果大于的话则放入磁盘中  
        factory.setSizeThreshold(1024*1024*100);  
     
        ServletFileUpload upload = new ServletFileUpload(factory);  
        //这里就是中文文件名处理的代码，其实只有一行，serheaderencoding就可以了  
        upload.setHeaderEncoding("utf-8");  
 
        try {  
        	List<FileItem> list = upload.parseRequest(req);  
        	for(FileItem item : list){  
        		//判断是不是上传的文件，如果不是得到值，并设置到request域中  
        		//这里的item.getfieldname是得到上传页面上的input上的name  
        		if(item.isFormField()){  
        			String name = item.getFieldName();  
        			String value =item.getString("utf-8");  
        			req.setAttribute(name, value);  
        		}  
        		//如果是上传的文件，则取出文件名，  
        		else{  
        			String name = item.getFieldName();  
        			String value = item.getName();  
                    int start = value.lastIndexOf("\\");  
                    String fileName = value.substring(start + 1);  
                    req.setAttribute(name, fileName);  
                    //写文件到path目录，文件名问filename  
                  item.write(new File(path,fileName));  
             	}  
        	}  
        }catch (FileUploadException e) {  
        	e.printStackTrace(); 
        } catch (Exception e) {  
        	e.printStackTrace();  
        }  
    }
}