package com.lyh.utils.listener;

import java.io.File;

public class FileRunner {

	public static void main(String[] args) throws Exception {
		FileListener.FileMonitor fileMonitor = new FileListener.FileMonitor(1000);
//		fileMonitor.monitor("D:\\Desktop\\app.log", new FileListener());
		fileMonitor.monitor("./logs/", new FileListener());
		fileMonitor.start();
	}
}
