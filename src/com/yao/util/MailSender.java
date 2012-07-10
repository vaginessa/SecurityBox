package com.yao.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.*; 
import android.os.Environment;
import android.widget.Toast;

public class MailSender {
	
	private final static String ACCOUNT_DB_PATH = "/data/data/com.yao.pw/databases/Account0.db";
	private static final String CAT_DB_PATH = "/data/data/com.yao.pw/databases/Category.db";
	private static final String NOTE_DB_PATH= "/data/data/com.yao.pw/databases/Notepad0.db";
	
	// 邮箱服务器
    private String host = "smtp.163.com";
    // 这个是你的邮箱用户名
    private String username = "yaochz@163.com";
    // 你的邮箱密码
    private String password = "6830Yaochz911";
    
    private String mail_head_name = "this is head of this mail";

    private String mail_head_value = "this is head of this mail";

    private String mail_to = "";

    private String mail_from = "yaochz@163.com";

    private String mail_subject = "this is the subject of this test mail";

    private String mail_body = "from SecurityBox";

    private String personalName = "安全盒子";

    public MailSender(String mailTo, String mailBody) {
		// TODO Auto-generated constructor stub
    	this.mail_to = mailTo;
    	this.mail_body = mailBody + "\n\n" + mail_body;
	}

    /**
     * 此段代码用来发送普通电子邮件
     */
    public void send() throws Exception
    {
        try
        {
            Properties props = new Properties(); // 获取系统环境
            Authenticator auth = new Email_Autherticator(); // 进行邮件服务器用户认证
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props, auth);
            // 设置session,和邮件服务器进行通讯。
            MimeMessage message = new MimeMessage(session);
            // message.setContent("foobar, "application/x-foobar"); // 设置邮件格式
            message.setSubject(mail_subject); // 设置邮件主题
            message.setText(mail_body); // 设置邮件正文
            message.setHeader(mail_head_name, mail_head_value); // 设置邮件标题
            message.setSentDate(new Date()); // 设置邮件发送日期
            Address address = new InternetAddress(mail_from, personalName);
            message.setFrom(address); // 设置邮件发送者的地址
            Address toAddress = new InternetAddress(mail_to); // 设置邮件接收方的地址
            message.addRecipient(Message.RecipientType.TO, toAddress);
            
            // 压缩个人数据，用作邮件附件
            zipDataFile();
            
            // create the message part  
            MimeBodyPart messageBodyPart =  
               new MimeBodyPart();  
            //fill message  
            messageBodyPart.setText(mail_body);  
            Multipart multipart = new MimeMultipart();  
            multipart.addBodyPart(messageBodyPart);  
            // Part two is attachment  
            messageBodyPart = new MimeBodyPart();  
            DataSource source =  
            	new FileDataSource(Environment.getExternalStorageDirectory() + "/Security/data.zip");  
            messageBodyPart.setDataHandler(  
               new DataHandler(source));  
            messageBodyPart.setFileName("data.zip");  
            multipart.addBodyPart(messageBodyPart);  
            // Put parts in message  
            message.setContent(multipart);  
            // Send the message  
            Transport.send(message); // 发送邮件
            System.out.println("send ok!");
            
            // 删除压缩文件
            File zipOut = new File(Environment.getExternalStorageDirectory(), "Security/data.zip");
            zipOut.delete();
            
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * 用来进行服务器对用户的认证
     */
    public class Email_Autherticator extends Authenticator
    {
        public Email_Autherticator()
        {
            super();
        }

        public Email_Autherticator(String user, String pwd)
        {
            super();
            username = user;
            password = pwd;
        }

        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(username, password);
        }
    }
    
    
    public void zipDataFile() throws IOException{
    	File cat_DB = new File(CAT_DB_PATH);
		File acc_DB = new File(ACCOUNT_DB_PATH);
		File note_DB = new File(NOTE_DB_PATH);
		File zipOut = new File(Environment.getExternalStorageDirectory(), "Security/data.zip");
		List<File> list = new ArrayList<File>();
		list.add(cat_DB);
		list.add(acc_DB);
		list.add(note_DB);
		ZipUtils.zipFiles(list, zipOut, "19900911");
    }
    
}
