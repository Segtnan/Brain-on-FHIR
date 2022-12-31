package io.segtnan.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.IdType;
import org.junit.jupiter.api.*;



import static org.mockito.Mockito.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientResourceProviderTest {
    FhirContext ctx;
    FederatedDbHandler dbHandler;
    PatientResourceProvider provider;
    public PatientResourceProviderTest(){
        ctx = FhirContext.forR4();
        dbHandler = new FederatedDbHandler();
        provider = new PatientResourceProvider(ctx,dbHandler);
    }

    @Test
    public void Create_GivenPatientOnAll_Failure(){
        RequestDetails requestDetails = mock(RequestDetails.class);
        when(requestDetails.getTenantId()).thenReturn("all");

        String raw = "{\"resourceType\": \"Patient\",\"id\": \"xcda\",\"text\": {\"status\": \"generated\"},\"identifier\": [{\"use\": \"usual\",\"type\": {\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\": \"MR\"}]},\"system\": \"urn:oid:2.16.840.1.113883.19.5\",\"value\": \"12345\"}],\"active\": true,\"name\": [{\"family\": \"Kevin\",\"given\": [\"Henry\"]}],\"gender\": \"male\",\"birthDate\": \"1932-09-24\",\"managingOrganization\": {\"reference\": \"Organization/2.16.840.1.113883.19.5\",\"display\": \"Good Health Clinic\"}}";
        PatientWithExtension patient = ctx.newJsonParser().parseResource(PatientWithExtension.class, raw);

        Assertions.assertThrows(UnprocessableEntityException.class, () -> provider.createPatient(requestDetails, patient, raw, mock(EncodingEnum.class)));
    }




    @Test
    @Order(1)
    public void Create_GivenPatientOnRDBMS_ReadSuccess(){
        RequestDetails requestDetails = mock(RequestDetails.class);
        when(requestDetails.getTenantId()).thenReturn("rdbms");

        String raw = "{\"resourceType\": \"Patient\",\"id\": \"test_very_unique\",\"text\": {\"status\": \"generated\"},\"identifier\": [{\"use\": \"usual\",\"type\": {\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\": \"MR\"}]},\"system\": \"urn:oid:2.16.840.1.113883.19.5\",\"value\": \"12345\"}],\"active\": true,\"name\": [{\"family\": \"Kevin\",\"given\": [\"Henry\"]}],\"gender\": \"male\",\"birthDate\": \"1932-09-24\",\"managingOrganization\": {\"reference\": \"Organization/2.16.840.1.113883.19.5\",\"display\": \"Good Health Clinic\"}}";
        PatientWithExtension patient = ctx.newJsonParser().parseResource(PatientWithExtension.class, raw);

        provider.createPatient(requestDetails, patient, raw, mock(EncodingEnum.class));
        Assertions.assertAll(() -> provider.read(requestDetails, new IdType("test_very_unique")));
    }

    @Test
    @Order(2)
    public void Update_GivenPatientOnAll_Failure(){
        RequestDetails requestDetails = mock(RequestDetails.class);
        when(requestDetails.getTenantId()).thenReturn("all");

        String raw = "{\"resourceType\": \"Patient\",\"id\": \"test_very_unique\",\"text\": {\"status\": \"generated\"},\"identifier\": [{\"use\": \"usual\",\"type\": {\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\": \"MR\"}]},\"system\": \"urn:oid:2.16.840.1.113883.19.5\",\"value\": \"12345\"}],\"active\": true,\"name\": [{\"family\": \"NotKevin\",\"given\": [\"Henry\"]}],\"gender\": \"male\",\"birthDate\": \"1932-09-24\",\"managingOrganization\": {\"reference\": \"Organization/2.16.840.1.113883.19.5\",\"display\": \"Good Health Clinic\"}}";
        PatientWithExtension patient = ctx.newJsonParser().parseResource(PatientWithExtension.class, raw);

        IdType id = new IdType("test_very_unique");

        Assertions.assertThrows(UnprocessableEntityException.class, () -> provider.update(
                requestDetails,
                id,
                patient,
                raw,
                mock(EncodingEnum.class)));

    }

    @Test
    @Order(3)
    public void Update_GivenPatientOnRDBMS_ReadWithChangeSuccess(){
        RequestDetails requestDetails = mock(RequestDetails.class);
        when(requestDetails.getTenantId()).thenReturn("rdbms");

        String raw = "{\"resourceType\": \"Patient\",\"id\": \"test_very_unique\",\"text\": {\"status\": \"generated\"},\"identifier\": [{\"use\": \"usual\",\"type\": {\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\": \"MR\"}]},\"system\": \"urn:oid:2.16.840.1.113883.19.5\",\"value\": \"12345\"}],\"active\": true,\"name\": [{\"family\": \"NotKevin\",\"given\": [\"Henry\"]}],\"gender\": \"male\",\"birthDate\": \"1932-09-24\",\"managingOrganization\": {\"reference\": \"Organization/2.16.840.1.113883.19.5\",\"display\": \"Good Health Clinic\"}}";
        PatientWithExtension patient = ctx.newJsonParser().parseResource(PatientWithExtension.class, raw);

        IdType id = new IdType("test_very_unique");

        provider.update(
                requestDetails,
                id,
                patient,
                raw,
                mock(EncodingEnum.class));
        Assertions.assertEquals(provider.read(requestDetails,id).getNameFirstRep().getFamily(),"NotKevin");

    }


    @Test
    @Order(4)
    public void Delete_GivenIdOnRDBMS_ReadFailure(){
        RequestDetails requestDetails = mock(RequestDetails.class);
        when(requestDetails.getTenantId()).thenReturn("rdbms");
        IdType id = new IdType("test_very_unique");

        provider.deletePatient(requestDetails, id);
        Assertions.assertThrows(ResourceNotFoundException.class,() -> provider.read(requestDetails, id));
    }

}
