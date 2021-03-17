package pe.eclipse.neon.yeo._2020;

public class Test {

	public static void main(String[] args) {

		String abc = "1200";
		System.out.println(Long.valueOf(abc));
		try{
			System.out.println(Long.parseLong(abc, 10));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
