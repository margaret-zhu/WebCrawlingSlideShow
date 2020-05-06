import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Scans a web page looking for links and images.  Each link 
 * that is found is explored by launching another PageScanner
 * thread. Images that are found are displayed in a common
 * PictureViewer shared by all threads in the application.
 */
public class PageScanner implements Runnable {

	private String URLString = null;

	private static HashSet<String> pagesProcessed = new HashSet<>();
	private static HashSet<String> imagesProcessed = new HashSet<>();

	public PageScanner(String urlString) {
		this.URLString = urlString;
	}

	@Override
	public void run() {

		/* If there are already 100 threads, kill this one so that the CPU isn't
		 * overwhelmed.  
		 */
		if(Thread.activeCount() > 100) {
			return;
		}
		
		try {

			URL url = new URL(URLString);
			Scanner s = new Scanner(url.openStream());
			
			while (s.hasNext()) {
				
				String line = s.nextLine();
				ArrayList<String> links = extractLinks(line);
				
				for(String link: links) {
					if(!pagesProcessed.contains(link)) {
						pagesProcessed.add(link);
						Thread thread = new Thread(new PageScanner(link));
						thread.start();
					}

				}
				
				ArrayList<String> images = extractImageNames(line);
				for (String image : images) {
					if (!imagesProcessed.contains(image)) {
						imagesProcessed.add(image);
						PictureViewer.showImage(image);
					}
				}

			}

			s.close();

		} catch (Exception e) { //not very professional, but an easy fix
			return; // Something bad happened, just kill this thread
		}
		
		
	}

	/* A "regular expression" that defines legitimate HTML links */
	private static final Pattern LINK_PATTERN = Pattern.compile("href=(\"|\')([^\"\']+)\\1");

	/* Given a line of HTML text, returns a list of links that appear
	 * on that line -- doesn't always work -- sometimes links are missed,
	 * and sometimes Strings are returned that aren't legitimate links.
	 */
	private ArrayList<String> extractLinks(String line) {
		ArrayList<String> list = new ArrayList<>();
		Matcher matcher = LINK_PATTERN.matcher(line);
		while(matcher.find()) {
			String link = matcher.group(2);
			if (link != null) {
				list.add(link);
			}
		}
		return list;
	}

	/* A "regular expression" that defines legitimate HTML image tags */
	private static final Pattern IMAGE_PATTERN = Pattern.compile("<img\\s+[^>]*?src=(\"|\')([^\"\']+)\\1");

	/* Given a line of HTML text, returns names of images that appear
	 * on that line -- doesn't always work -- sometimes images are missed,
	 * and sometimes Strings are returned that aren't legitimate image names.
	 */
	private ArrayList<String> extractImageNames(String line) {
		ArrayList<String> list = new ArrayList<>();
		Matcher matcher = IMAGE_PATTERN.matcher(line);
		while(matcher.find()) {
			String found = matcher.group(2);
			if (!found.contains("http")) {   // relative URL
				found = URLString + "/" + found;
			}
			list.add(found);
		}
		return list;
	}
}
