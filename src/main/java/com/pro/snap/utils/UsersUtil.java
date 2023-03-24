package com.pro.snap.utils;

import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pro.snap.pojo.Users;
import com.pro.snap.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class UsersUtil {
    private static void createUsers(int count) throws Exception {
        List<Users> users = new ArrayList<>(count);
        //生成用户
        for(int i = 0; i < count; i++){
            Users user = new Users();
            user.setId(10000000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5util.inputPassToDBPass("123456", user.getSalt()));
            user.setCountLogin(1);
            user.setDateRegister(new Date());
//            user.setHead();
//            user.setDateLastLogin();
            users.add(user);
        }
        System.out.println("Create users");
        //插入数据库
        Connection conn = getConn();
        String sql = "insert into t_users(count_login, nickname, date_register, salt, password, id) values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i = 0; i < users.size(); i++){
            Users user = users.get(i);
            pstmt.setInt(1, user.getCountLogin());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getDateRegister().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.clearParameters();
        conn.close();
        System.out.println("Insert into DB");
        //登录，生成UsersTicket
        String urlString = "http://localhost:8081/login/doLogin";
        File file = new File("E:\\Project\\IDEA_project\\snap\\apache-jmeter-5.4.3\\config\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for(int i = 0; i < users.size(); i++){
            Users user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co  = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream outputStream = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5util.inputPassToFormPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0){
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String usersTicket = (String) respBean.getObj();
            System.out.println("Create usersTicket: " + user.getId());
            String row = user.getId() + "," + usersTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("Write to file: " + user.getId());
        }
        raf.close();
        System.out.println("Over");
    }

    private static Connection getConn() throws Exception {
//        String url = "192.168.77.138:3306/snapdb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=US/Pacific";
//        String username = "xxxx";
//        String password = "1234";
//        String url = "jdbc:mysql://localhost/snapdb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=US/Pacific";
//        String username = "xxxx";
//        String password = "1234";
        String url = "jdbc:mysql://localhost:3306/snapdb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=US/Pacific";
        String username = "root";
        String password = "root";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUsers((5000));
    }
}
