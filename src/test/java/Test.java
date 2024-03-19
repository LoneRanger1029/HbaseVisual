import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Test {
	public static void main(String[] args) throws IOException{

//		String[] namespace = new String[]{"lyh","nosql","bigdata","student"};
//		String[][] tableData = new String[namespace.length][1];
//		System.out.println(tableData.length);
//		tableData[0][0] = namespace[0];
//		tableData[1][0] = namespace[1];
//		tableData[2][0] = namespace[2];
//		tableData[3][0] = namespace[3];
//		for (String[] tableDatum : tableData) {
//			for (String s : tableDatum) {
//				System.out.println(s);
//			}
//		}

		Scanner in = new Scanner(System.in);
		int t = in.nextInt();
		for (int i = 0; i < t; i++) {
			int n = in.nextInt();
			int k = in.nextInt();
			Integer[] tmp = new Integer[n];
			int sum=0;
			for (int j = 0; j < n; j++) {
				tmp[j] = in.nextInt();
				sum+=tmp[j];
			}
			System.out.println(sum-minSub(tmp,k));
		}
		in.close();
	}
	public static int minSub(Integer[] arr,int k) {

		List<Integer> tt = Arrays.asList(arr);
		List<Integer> list = new ArrayList<>(tt);
		Collections.sort(list);
		System.out.println(list.toString());
		int res=0;
		for (int i = 0; i < k; i++) {
			int sumOfPreSum=list.get(0)+list.get(1);
			int last = list.get(list.size()-1);
			System.out.println("sumof="+sumOfPreSum);
			System.out.println("last="+last);
			if(sumOfPreSum<last) {
				list.remove(1);
				list.remove(0);
			}else {
				list.remove(list.size()-1);
			}
			res+=Math.min(sumOfPreSum, last);
		}
		return res;
	}
}
