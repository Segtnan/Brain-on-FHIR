package ca.uhn.fhir.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import ca.uhn.fhir.rest.server.tenant.UrlBaseTenantIdentificationStrategy;


// segtnanpassword
/**
 * The type Segtnan restful server.
 */
@WebServlet("/*")
public class SegtnanRestfulServer extends RestfulServer {

	/**
	 * Initialize.
	 *
	 * @throws ServletException the servlet exception
	 */
	@Override
	protected void initialize() throws ServletException {
		// Create a context for the appropriate version
      FhirContext ctx = FhirContext.forR4();
		setFhirContext(ctx);
      setTenantIdentificationStrategy(new UrlBaseTenantIdentificationStrategy());
		FederatedDbHandler dbHandler = new FederatedDbHandler();
		// Register resource providers
		registerProvider(new SegtnanQuestionnaireResponseResourceProvider(ctx, dbHandler));

		// Format the responses in nice HTML
		registerInterceptor(new ResponseHighlighterInterceptor());
	}
}
