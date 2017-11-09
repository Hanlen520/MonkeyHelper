package com.shang.monkeyhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtils {
	public static String execCmd(String... string) {
		// TODO Auto-generated method stub
		StringBuilder result = new StringBuilder(); // 线程不安全
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime().exec(string);
			inputStreamReader = new InputStreamReader(process.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				result.append(temp).append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}
}
