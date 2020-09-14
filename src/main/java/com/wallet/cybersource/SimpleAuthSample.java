package com.wallet.cybersource;
import java.util.*;
//import com.cybersource.ws.client.*;
public class SimpleAuthSample
{
	public static void main( String[] args )
	{
		//Properties props = Utility.readProperties( args );
		Properties props = new Properties();
		props.put("merchantID", "novopay1");
		props.put("keysDirectory", "D:\\Development_Avecto\\Novo_Cybersource_key");
		props.put("sendToProduction","false");
		props.put("keyFilename","novopay1.p12");
		props.put("enableLog","false");
		props.put("useHttpClient", "false");
		props.put("namespaceURI", "urn:schemas-cybersource-com:transaction-data-1.18");
		props.put("serverURL", " https://ics2wstest.ic3.com/commerce/1.x/transactionProcessor");
		props.put("targetAPIVersion","CyberSourceTransaction_1.169.wsdl");
		props.put("useHttpClient", "false");
		props.put("keyPassword", "novopay1");
		HashMap request = new HashMap();
		// In this sample, we are processing a credit card authorization.
		request.put( "ccAuthService_run", "true" );
		// Add required fields
		request.put( "merchantReferenceCode", "MRC-14344" );
		request.put( "billTo_firstName", "Jane" );
		request.put( "billTo_lastName", "Smith" );
		request.put( "billTo_street1", "1295 Charleston Road" );
		request.put( "billTo_city", "Mountain View" );
		request.put( "billTo_state", "CA" );
		request.put( "billTo_postalCode", "94043" );
		request.put( "billTo_country", "US" );
		request.put( "billTo_email", "jsmith@example.com" );
		request.put( "card_accountNumber", "4111111111111111" );
		request.put( "card_expirationMonth", "12" );
		request.put( "card_expirationYear", "2010" );
		request.put( "purchaseTotals_currency", "USD" );
		
		// This sample order contains two line items.
		request.put( "item_0_unitPrice", "12.34" );
		request.put( "item_1_unitPrice", "56.78" );		
	}
}
