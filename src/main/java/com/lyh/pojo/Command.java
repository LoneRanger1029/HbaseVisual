package com.lyh.pojo;

/**
 * 命令行窗口的每条消息都是一个 Command 对象
 * 如果是多行命令用 / 分隔开来
 */
public class Command {
   private final static boolean canDel = true;
   private String command;

   public Command(String command){
      this.command = command;
   }

   public Command(){}

}