package com.lyh.utils.listener;

import com.lyh.panes.controller.LogTabPaneController;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;

public class FileListener extends FileAlterationListenerAdaptor {

	@Override
	public void onStart(FileAlterationObserver observer) {
		super.onStart(observer);
		System.out.println("onStart");
	}

	@Override
	public void onDirectoryCreate(File directory) {
		System.out.println("新建：" + directory.getAbsolutePath());
	}
	public static class FileMonitor {

		private FileAlterationMonitor monitor;

		public FileMonitor(long interval) {
			monitor = new FileAlterationMonitor(interval);
		}

		/**
		 * 给文件添加监听
		 *
		 * @param path     文件路径
		 * @param listener 文件监听器
		 */
		public void monitor(String path, FileAlterationListener listener) {
			File file = new File(path);
			if (!file.exists()){
				System.out.println("文件不存在");
			}
			FileAlterationObserver observer = new FileAlterationObserver(new File(path));
			monitor.addObserver(observer);
			observer.addListener(listener);
		}

		public void stop() throws Exception {
			monitor.stop();
		}

		public void start() throws Exception {
			monitor.start();

		}
	}

	@Override
	public void onDirectoryChange(File directory) {
		System.out.println("修改：" + directory.getAbsolutePath());
	}

	@Override
	public void onDirectoryDelete(File directory) {
		System.out.println("删除：" + directory.getAbsolutePath());
	}

	@Override
	public void onFileCreate(File file) {
		String compressedPath = file.getAbsolutePath();
		System.out.println("新建：" + compressedPath);
		if (file.canRead()) {
			// TODO 读取或重新加载文件内容
			System.out.println("文件变更，进行处理");
		}
	}

	@Override
	public void onFileChange(File file) {
		String compressedPath = file.getAbsolutePath();
		System.out.println("修改：" + compressedPath);
		try {
			LogTabPaneController.updateLog(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFileDelete(File file) {
		System.out.println("删除：" + file.getAbsolutePath());
	}

	@Override
	public void onStop(FileAlterationObserver observer) {
		super.onStop(observer);
		System.out.println("onStop");
	}
}
