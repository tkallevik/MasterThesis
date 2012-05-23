package testutilities;

import java.io.File;

public class FileOperations {
	public static boolean emptyFolder(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = emptyFolder(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		} else {
			dir.delete();
		}
		
		return true;
	}
	
	public static String getPathOfFirstFileInFolder(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				File file = new File(dir, children[i]);
				if (!file.isDirectory()) {
					return file.getAbsolutePath();
				}
			}
		}
		
		return null;
	}
}
