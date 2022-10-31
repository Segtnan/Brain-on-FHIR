package ca.uhn.fhir.example;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type Patient resource provider.
 */
public class PatientResourceProvider  {
/*
    FhirContext ctx;
    FederatedDbHandler dbHandler;

    public PatientResourceProvider(FhirContext ctx, FederatedDbHandler dbHandler){
        this.ctx = ctx;
        this.dbHandler = dbHandler;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }

    @Read()
    public Patient read(RequestDetails theRequestDetails, @IdParam IdType theId) {

        try {
            String query = String.format("select * from new com.ibm.db2j.GaianTable('patient', 'with_provenance') T where uuid='%s'",
                    theId.getIdPart());
            ResultSet rs = dbHandler.connect.createStatement().executeQuery(query);
            while (rs.next()){
                DbConnection dbConnection = dbHandler.getDbMap().get(rs.getString("gdb_node")+rs.getString("gdb_leaf").split("::")[0]);

                if (!theRequestDetails.getTenantId().equals("all") && !(rs.getString("gdb_node").equals(dbConnection.node) && dbConnection.sourceList.equals(theRequestDetails.getTenantId()))){
                    System.out.println("We failed");
                    continue;
                }
                Patient patient = ctx.newJsonParser().parseResource(Patient.class, rs.getString("json"));
                return patient;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
*/
}
