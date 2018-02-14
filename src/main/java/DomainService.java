import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DomainService {


	private static final Integer numberOfDomains = 10;
	private static final List<String> domainList = new ArrayList<String>();
	public static void main(String args[]) throws IOException {
		initializeDomainList();//
		callDomains();//
		getHistoricalData("ebay.com");
		getAverageMetric();
	}

	private static void initializeDomainList() {
		domainList.add("amazon.com");
		domainList.add("ebay.com");
		domainList.add("target.com");
		domainList.add("kohls.com");
		domainList.add("sears.com");
		domainList.add("walmart.com");
		domainList.add("apple.com");
		domainList.add("costco.com");
		domainList.add("coldstone.com");
		domainList.add("dominos.com");
	}

	private static void getAverageMetric() {

		Response laptopAmazonReport = RestAssured.get("http://api.semrush.com/reports/v1/projects/1238683/tracking/?key=27d2fde07fbcfd52c18c50abf8f53757&action=report&type=tracking_overview_organic&url=*.amazon.com/*");
		Response tvAmazonReport = RestAssured.get("http://api.semrush.com/reports/v1/projects/1238669/tracking/?key=27d2fde07fbcfd52c18c50abf8f53757&action=report&type=tracking_overview_organic&url=*.amazon.com/*");

		// we are comparing visibility metrics and averaging it out
		float visibilityOfLaptop = laptopAmazonReport.jsonPath().get("visibility");
		float visibilityOfTv = tvAmazonReport.jsonPath().get("visibility"); 

		System.out.println("visbility metric of Laptop = "+visibilityOfLaptop);
		System.out.println("visbility metric of Tv = "+visibilityOfTv);
		System.out.println("averaging out above two values");
		System.out.println(" averaged  value = "+(visibilityOfLaptop+visibilityOfTv)/2.0f);
	}


	private static void getHistoricalData(String domain) throws IOException {
		Response resp = RestAssured.get("https://api.semrush.com/?type=domain_rank_history&key=27d2fde07fbcfd52c18c50abf8f53757&display_limit=12&export_columns=Rk,Or,Ot,Oc,Ad,At,Ac,Dt&domain="+domain+"&database=us");
		BufferedReader br = new BufferedReader(new StringReader(resp.asString()));
		String currentLine;
		int i = 0;
		long maxTraffic = 0;
		List<String> lineList;
		String monthWithMaxTraffic = null;
		while((currentLine =br.readLine())!=null) {
			i++;
			if(i==1)
				continue;
			lineList = Arrays.asList(currentLine.split(";"));
			if(Long.valueOf(lineList.get(2))> maxTraffic)
			{
				maxTraffic = Long.valueOf(lineList.get(2));
				monthWithMaxTraffic = lineList.get(7);
			}			
		}
		System.out.println("max traffic = "+maxTraffic+ " month with max traffic = "+monthWithMaxTraffic);
		System.out.println("***calculating percentage of current month with peak month***");

		br = new BufferedReader(new StringReader(resp.asString()));
		br.readLine();
		while((currentLine =br.readLine())!=null) {
			String trafficOfCurrentMonth = Arrays.asList(currentLine.split(";")).get(2);
			Long currentMonthTraffic = Long.valueOf(trafficOfCurrentMonth);
			Long percentageChange = ((maxTraffic-currentMonthTraffic)*100/maxTraffic);
			System.out.println("percentageChange = "+percentageChange);
		}

	}



	private static void callDomains() {
		for(int i = 0 ; i < domainList.size(); i++) {
			Response resp = RestAssured.get("http://api.semrush.com/?key=27d2fde07fbcfd52c18c50abf8f53757&type=domain_rank&export_columns=Dn,Om,Rk,Tm&domain="+domainList.get(i)+"&database=us&display_limit=10");
			System.out.println("data for "+ domainList.get(i)+" ="+resp.prettyPrint());
		}
	}
}
