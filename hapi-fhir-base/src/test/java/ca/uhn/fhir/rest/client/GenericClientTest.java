package ca.uhn.fhir.rest.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.StringReader;
import java.nio.charset.Charset;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu.resource.Organization;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.rest.server.Constants;

public class GenericClientTest {

	private FhirContext myCtx;
	private HttpClient myHttpClient;
	private HttpResponse myHttpResponse;

	@Before
	public void before() {
		myCtx = new FhirContext();

		myHttpClient = mock(HttpClient.class, new ReturnsDeepStubs());
		myCtx.getRestfulClientFactory().setHttpClient(myHttpClient);

		myHttpResponse = mock(HttpResponse.class, new ReturnsDeepStubs());
	}

	private String getPatientFeedWithOneResult() {
		//@formatter:off
		String msg = "<feed xmlns=\"http://www.w3.org/2005/Atom\">\n" + 
				"<title/>\n" + 
				"<id>d039f91a-cc3c-4013-988e-af4d8d0614bd</id>\n" + 
				"<os:totalResults xmlns:os=\"http://a9.com/-/spec/opensearch/1.1/\">1</os:totalResults>\n" + 
				"<published>2014-03-11T16:35:07-04:00</published>\n" + 
				"<author>\n" + 
				"<name>ca.uhn.fhir.rest.server.DummyRestfulServer</name>\n" + 
				"</author>\n" + 
				"<entry>\n" + 
				"<content type=\"text/xml\">" 
				+ "<Patient xmlns=\"http://hl7.org/fhir\">" 
				+ "<text><status value=\"generated\" /><div xmlns=\"http://www.w3.org/1999/xhtml\">John Cardinal:            444333333        </div></text>"
				+ "<identifier><label value=\"SSN\" /><system value=\"http://orionhealth.com/mrn\" /><value value=\"PRP1660\" /></identifier>"
				+ "<name><use value=\"official\" /><family value=\"Cardinal\" /><given value=\"John\" /></name>"
				+ "<name><family value=\"Kramer\" /><given value=\"Doe\" /></name>"
				+ "<telecom><system value=\"phone\" /><value value=\"555-555-2004\" /><use value=\"work\" /></telecom>"
				+ "<gender><coding><system value=\"http://hl7.org/fhir/v3/AdministrativeGender\" /><code value=\"M\" /></coding></gender>"
				+ "<address><use value=\"home\" /><line value=\"2222 Home Street\" /></address><active value=\"true\" />"
				+ "</Patient>"
				+ "</content>\n"  
				+ "   </entry>\n"  
				+ "</feed>";
		//@formatter:on
		return msg;
	}

	@SuppressWarnings("unused")
	@Test
	public void testSearchByString() throws Exception {

		String msg = getPatientFeedWithOneResult();

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(myHttpClient.execute(capt.capture())).thenReturn(myHttpResponse);
		when(myHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(myHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_FHIR_XML + "; charset=UTF-8"));
		when(myHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(msg), Charset.forName("UTF-8")));

		IGenericClient client = myCtx.newRestfulGenericClient("http://example.com/fhir");

		//@formatter:off
		Bundle response = client.search()
				.forResource("Patient")
				.where(Patient.PARAM_NAME.matches().value("james"))
				.execute();
		//@formatter:on

		assertEquals("http://example.com/fhir/Patient?name=james", capt.getValue().getURI().toString());

	}

	
	@SuppressWarnings("unused")
	@Test
	public void testSearchByStringExact() throws Exception {

		String msg = getPatientFeedWithOneResult();

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(myHttpClient.execute(capt.capture())).thenReturn(myHttpResponse);
		when(myHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(myHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_FHIR_XML + "; charset=UTF-8"));
		when(myHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(msg), Charset.forName("UTF-8")));

		IGenericClient client = myCtx.newRestfulGenericClient("http://example.com/fhir");

		//@formatter:off
		Bundle response = client.search()
				.forResource("Patient")
				.where(Patient.PARAM_NAME.matchesExactly().value("james"))
				.execute();
		//@formatter:on

		assertEquals("http://example.com/fhir/Patient?name:exact=james", capt.getValue().getURI().toString());

	}
	@SuppressWarnings("unused")
	@Test
	public void testSearchByToken() throws Exception {

		String msg = getPatientFeedWithOneResult();

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(myHttpClient.execute(capt.capture())).thenReturn(myHttpResponse);
		when(myHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(myHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_FHIR_XML + "; charset=UTF-8"));
		when(myHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(msg), Charset.forName("UTF-8")));

		IGenericClient client = myCtx.newRestfulGenericClient("http://example.com/fhir");

		//@formatter:off
		Bundle response = client.search()
				.forResource("Patient")
				.where(Patient.PARAM_IDENTIFIER.exactly().systemAndCode("http://example.com/fhir", "ZZZ"))
				.execute();
		//@formatter:on

		assertEquals("http://example.com/fhir/Patient?identifier=http%3A%2F%2Ffoo%7CZZZ", capt.getValue().getURI().toString());

	}

	@SuppressWarnings("unused")
	@Test
	public void testSearchByReferenceSimple() throws Exception {

		String msg = getPatientFeedWithOneResult();

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(myHttpClient.execute(capt.capture())).thenReturn(myHttpResponse);
		when(myHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(myHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_FHIR_XML + "; charset=UTF-8"));
		when(myHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(msg), Charset.forName("UTF-8")));

		IGenericClient client = myCtx.newRestfulGenericClient("http://example.com/fhir");

		//@formatter:off
		Bundle response = client.search()
				.forResource("Patient")
				.where(Patient.PARAM_PROVIDER.hasId("123"))
				.execute();
		//@formatter:on

		assertEquals("http://example.com/fhir/Patient?provider=123", capt.getValue().getURI().toString());

	}

	@SuppressWarnings("unused")
	@Test
	public void testSearchByReferenceProperty() throws Exception {

		String msg = getPatientFeedWithOneResult();

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(myHttpClient.execute(capt.capture())).thenReturn(myHttpResponse);
		when(myHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(myHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_FHIR_XML + "; charset=UTF-8"));
		when(myHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(msg), Charset.forName("UTF-8")));

		//@formatter:off
		IGenericClient client = myCtx.newRestfulGenericClient("http://example.com/fhir");

		Bundle response = client.search()
				.forResource(Patient.class)
				.where(Patient.PARAM_PROVIDER.hasChainedProperty(Organization.NAME.matches().value("ORG0")))
				.execute();

		assertEquals("http://example.com/fhir/Patient?provider.name=ORG0", capt.getValue().getURI().toString());
		//@formatter:on

	}

	
	@SuppressWarnings("unused")
	@Test
	public void testSearchByDate() throws Exception {

		String msg = getPatientFeedWithOneResult();

		ArgumentCaptor<HttpUriRequest> capt = ArgumentCaptor.forClass(HttpUriRequest.class);
		when(myHttpClient.execute(capt.capture())).thenReturn(myHttpResponse);
		when(myHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
		when(myHttpResponse.getEntity().getContentType()).thenReturn(new BasicHeader("content-type", Constants.CT_FHIR_XML + "; charset=UTF-8"));
		when(myHttpResponse.getEntity().getContent()).thenReturn(new ReaderInputStream(new StringReader(msg), Charset.forName("UTF-8")));

		IGenericClient client = myCtx.newRestfulGenericClient("http://example.com/fhir");

		//@formatter:off
		Bundle response = client.search()
				.forResource(Patient.class)
				.encodedJson()
				.where(Patient.PARAM_BIRTHDATE.beforeOrEquals().day("2012-01-22"))
				.and(Patient.PARAM_BIRTHDATE.after().day("2011-01-01"))
				.include(Patient.INCLUDE_MANAGINGORGANIZATION)
				.sort().ascending(Patient.PARAM_BIRTHDATE)
				.sort().descending(Patient.PARAM_NAME)
				.limitTo(123)
				.execute();
		//@formatter:on

		assertEquals("http://example.com/fhir/Patient?birthdate=%3C%3D2012-01-22&birthdate=%3E2011-01-01&_include=Patient.managingOrganization&_sort%3Aasc=birthdate&_sort%3Adesc=name&_format=json&_count=123", capt.getValue().getURI().toString());

	}

}