package com.lyh.pojo;

/**
 * 比如 [lyh@hadoop102 ~ ]$
 */
public class Title{

   private final static boolean canDel = false;

   String username;  // 用户名
   String host; // 主机名称
   String current_dir; // 当前目录

   public Title(){}

   public Title(String username,String host,String current_dir){

   }

   @Override
   public String toString() {
      return "["+username+"@"+host+" "+current_dir+"]$ ";
   }
}