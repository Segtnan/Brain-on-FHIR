package io.segtnan.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The type Patient resource provider.
 */
public class PatientResourceProvider implements IResourceProvider {

    FhirContext ctx;
    FederatedDbHandler dbHandler;

    public PatientResourceProvider(FhirContext ctx, FederatedDbHandler dbHandler){
        this.ctx = ctx;
        this.dbHandler = dbHandler;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return PatientWithExtension.class;
    }

    @Read()
    public Patient read(RequestDetails theRequestDetails, @IdParam IdType theId) {
        ArrayList<DbConnection> dbConnections = this.dbHandler.findConnection(theId.getIdPart(), FederatedDbHandler.ResourceType.PATIENT, theRequestDetails.getTenantId(),true);
        if(!theRequestDetails.getTenantId().equals("all") && dbConnections.size() > 1){
            throw new UnprocessableEntityException();
        }
        if(dbConnections.size() == 0){
            throw new ResourceNotFoundException(theId);
        }

        DbConnection dbConn = dbConnections.get(0);
        Patient patient = ctx.newJsonParser().parseResource(Patient.class, dbConn.getResult().get("JSON"));

        Meta meta = new Meta().setSource(dbConn.sourceList);
        if(dbConnections.size()>1){
            meta = meta.addTag("http://terminology.hl7.org/CodeSystem/common-tags","actionable","Actionable");
        }
        patient.setMeta(meta);
        return patient;
    }

    @Update
    public MethodOutcome update(RequestDetails theRequestDetails, @IdParam IdType theId, @ResourceParam Patient sqr, @ResourceParam String theRawBody,
                                @ResourceParam EncodingEnum theEncodingEnum) {
        if(theRequestDetails.getTenantId().equals("all")){
            throw new UnprocessableEntityException("Invalid tenant id");
        }
        ArrayList<DbConnection> dbConnections = dbHandler.findConnection(theId.getIdPart(), FederatedDbHandler.ResourceType.PATIENT, theRequestDetails.getTenantId());
        if(dbConnections.size()>1){
            throw new UnprocessableEntityException();
        }
        if(dbConnections.size() == 0){
            throw new ResourceNotFoundException(theId);
        }
        DbConnection dbConn = dbConnections.get(0);
        String query = String.format("select * from new com.ibm.db2j.GaianQuery('update patient set json=''%s'' where uuid=''%s''', 'with_provenance', 'SOURCELIST=%s') Q",theRawBody,theId.getIdPart(),dbConn.sourceList);
        try{
            dbConn.connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new ResourceNotFoundException(theId);
        }
        return new MethodOutcome();


    }

    @Create
    public MethodOutcome createPatient(RequestDetails theRequestDetails, @ResourceParam Patient thePatient, @ResourceParam String theRawBody,
                                       @ResourceParam EncodingEnum theEncodingEnum) {
        DbConnection dbConn = this.dbHandler.getSourceMap().get(theRequestDetails.getTenantId());
        Patient patient = ctx.newJsonParser().parseResource(Patient.class, theRawBody);
        if(dbConn == null){
            throw new UnprocessableEntityException("Invalid tenant ID");
        }
        String query = String.format("select * from new com.ibm.db2j.GaianQuery('insert into patient values(''%s'',''%s'')', 'with_provenance', 'SOURCELIST=%s') Q", patient.getId().substring(8), theRawBody, dbConn.sourceList);
        try{
            dbConn.connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return new MethodOutcome();
    }

    @Delete()
    public void deletePatient(RequestDetails theRequestDetails, @IdParam IdType theId) {
        DbConnection dbConn = this.dbHandler.getSourceMap().get(theRequestDetails.getTenantId());
        if(dbConn == null){
            throw new UnprocessableEntityException("Invalid tenant ID");
        }
        String query = String.format("select * from new com.ibm.db2j.GaianQuery('delete from patient where uuid=''%s''', 'with_provenance', 'SOURCELIST=%s') Q", theId.getIdPart(), dbConn.sourceList);
        try{
            dbConn.connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new ResourceNotFoundException(theId);
        }

    }

}
