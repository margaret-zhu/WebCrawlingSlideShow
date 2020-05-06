
public class Driver {

	public static void main(String[] args) {
		Thread t = new Thread(new PageScanner("https://cs.umd.edu"));
		//can replace the link above with any other link 
		t.start();
	}

}
