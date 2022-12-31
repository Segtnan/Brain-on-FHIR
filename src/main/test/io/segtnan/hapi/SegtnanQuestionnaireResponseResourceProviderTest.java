package io.segtnan.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SegtnanQuestionnaireResponseResourceProviderTest {
    FhirContext ctx;
    FederatedDbHandler dbHandler;
    SegtnanQuestionnaireResponseResourceProvider provider;
    public SegtnanQuestionnaireResponseResourceProviderTest(){
        ctx = FhirContext.forR4();
        dbHandler = new FederatedDbHandler();
        provider = new SegtnanQuestionnaireResponseResourceProvider(ctx,dbHandler);
    }

    @Test
    public void Update_GivenPatientOnRDBMS_ReadAndRollbackSuccessful(){
        RequestDetails requestDetails = mock(RequestDetails.class);
        when(requestDetails.getTenantId()).thenReturn("rdbms");
        IdType id = new IdType("test_very_unique2");

        SegtnanQuestionnaireResponse resp1 = provider.read(requestDetails,id);
        String resp1_location = resp1.getLocation().toString();
        resp1.setLocation(new StringType("test_unique" + System.currentTimeMillis()));

        provider.update(requestDetails,
                id,
                resp1,
                ctx.newJsonParser().encodeResourceToString(resp1),
                mock(EncodingEnum.class));
        SegtnanQuestionnaireResponse resp2 = provider.read(requestDetails,id);
        String resp2_location = resp2.getLocation().toString();

        resp1.setLocation(new StringType(resp1_location));
        provider.update(requestDetails,
                id,
                resp1,
                ctx.newJsonParser().encodeResourceToString(resp1),
                mock(EncodingEnum.class));
        Assertions.assertNotEquals(resp1_location,resp2_location);

    }

    @Test
    public void Read_GivenFakeIdOnRDBMS_Failure(){
        RequestDetails requestDetails = mock(RequestDetails.class);

        IdType id = new IdType("i_dont_exist");

        Assertions.assertThrows(ResourceNotFoundException.class, () -> provider.read(requestDetails,id));
    }

}
