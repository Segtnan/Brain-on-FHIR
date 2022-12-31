package io.segtnan.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.QuestionnaireResponse.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.*;


/**
 * The type Segtnan questionnaire response resource provider.
 */
public class SegtnanQuestionnaireResponseResourceProvider implements IResourceProvider {
   /**
    * The Connect.
    */
   private Connection connect = null;
   /**
    * The Db handler.
    */
   private FederatedDbHandler dbHandler;
   /**
    * The Result set.
    */
   private ResultSet resultSet = null;
   /**
    * The Ctx.
    */
   private FhirContext ctx;

   /**
    * Instantiates a new Segtnan questionnaire response resource provider.
    *
    * @param ctx       the ctx
    * @param dbHandler the db handler
    */
   public SegtnanQuestionnaireResponseResourceProvider(FhirContext ctx, FederatedDbHandler dbHandler) {
      this.ctx = ctx;
      try {
         this.connect = dbHandler.connect;
         this.dbHandler = dbHandler;

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Gets resource type.
    *
    * @return the resource type
    */
   @Override
   public Class<? extends IBaseResource> getResourceType() {
      return SegtnanQuestionnaireResponse.class;
   }


   //wget --method=PUT --body-data="{  \"resourceType\": \"QuestionnaireResponse\", \"id\":\"004096ab-606e-4118-879d-dbcf4ac20f56\" , \"extension\": [ {    \"url\": \"http://acme.org/#uuid\",    \"valueId\": \"004096ab-606e-4118-879d-dbcf4ac20f56\"  }, {    \"url\": \"http://acme.org/#group\",    \"valueInteger\": 14  }, {\"url\": \"http://acme.org/#groupid\",\"valueString\": \"ch-TEST-2022-G1\"}, {    \"url\": \"http://acme.org/#diseasestatus\",    \"valueBoolean\": true  }, {    \"url\": \"http://acme.org/#dateoftest\",    \"valueDate\": \"2022-06-06\"  }, {    \"url\": \"http://acme.org/#dateofanswer\",    \"valueDate\": \"2022-06-06\"  }, {    \"url\": \"http://acme.org/#location\",    \"valueString\": \"ODENSE\"  } ]}" --header="Content-Type:application/fhir+json;charset=utf-8" -S -q -O - http://localhost:8080/QuestionnaireResponse/004096ab-606e-4118-879d-dbcf4ac20f5

   /**
    * Update method outcome.
    *
    * @param theRequestDetails the the request details
    * @param theId             the the id
    * @param sqr               the sqr
    * @param theRawBody        the the raw body
    * @param theEncodingEnum   the the encoding enum
    * @return the method outcome
    */
   @Update
   public MethodOutcome update(RequestDetails theRequestDetails, @IdParam IdType theId, @ResourceParam SegtnanQuestionnaireResponse sqr, @ResourceParam String theRawBody,
                               @ResourceParam EncodingEnum theEncodingEnum) {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      ArrayList<DbConnection> dbConnections = this.dbHandler.findConnection(theId.getIdPart(), FederatedDbHandler.ResourceType.SEGTNAN_QUESTIONNAIRE_RESPONSE, theRequestDetails.getTenantId());
      if(dbConnections.size()>1){
         throw new UnprocessableEntityException();
      }
      if(dbConnections.size() == 0){
         throw new ResourceNotFoundException(theId);
      }
      DbConnection conn = dbConnections.get(0);
      JSONObject jsonS = new JSONObject(theRawBody);
      String answers = "";
      if (jsonS.has("item")){
         if(((JSONObject)((JSONArray)jsonS.get("item")).get(0)).has("item")){
            answers = "{\"item\": " + jsonS.get("item").toString() + "}";
         } else {
            answers = jsonS.get("item").toString();
         }
      }

      String query = String.format(
              "select * from new com.ibm.db2j.GaianQuery('update segtnan set " +
                      "zgroup=%d," +
                      "group_id=''%s''," +
                      "zstatus=%d," +
                      "location=''%s''," +
                      "date_of_test=''%s''," +
                      "date_of_answer=''%s''," +
                      "answers=''%s''" +
                      "where uuid=''%s''','with_provenance','SOURCELIST=%s') Q",
              sqr.getGroup().getValue(),
              sqr.getGroupID().getValue(),
              sqr.getDiseaseStatus().getValue() ? 1 : 0,
              sqr.getLocation().getValue(),
              format.format(sqr.getDateOfTest().getValue()),
              format.format(sqr.getDateOfAnswer().getValue()),
              answers,
              sqr.getUUID().getIdPart(),
              conn.sourceList);
      try {
         ResultSet rss = conn.connection.createStatement().executeQuery(query);

      } catch (SQLException e) {
         throw new RuntimeException(e);
      }

      MethodOutcome mo = new MethodOutcome();
      return mo;

/*
         while (rs.next()) {
            // GaianTable appends the table name as well, split to remove
            DbConnection dbConnection = dbHandler.getDbMap().get((rs.getString("gdb_node")+rs.getString("gdb_leaf")).split("::")[0]);

            String tenant;
            if (theRequestDetails.getTenantId().equals("all")){
               tenant = dbConnection.sourceList;
            } else if (!(rs.getString("gdb_node").equals(dbConnection.node) && dbConnection.sourceList.equals(theRequestDetails.getTenantId()))){
               continue;
            } else {
               tenant = dbConnection.sourceList;
            }


         }*/







   }


   /**
    * Read segtnan questionnaire response.
    *
    * @param theRequestDetails the the request details
    * @param theId             the the id
    * @return the segtnan questionnaire response
    */
   @Read()
   public SegtnanQuestionnaireResponse read(RequestDetails theRequestDetails, @IdParam IdType theId) {

      SegtnanQuestionnaireResponse retVal = new SegtnanQuestionnaireResponse();
      try {

         String query = String.format("select * from new com.ibm.db2j.GaianTable('segtnan', 'with_provenance') T where uuid='%s'",
                 theId.getIdPart());

         ResultSet rs = connect.createStatement().executeQuery(query);
         boolean multiple = false;
         while (rs.next()) {
            // GaianTable appends the table name as well, split to remove
            DbConnection dbConnection = dbHandler.getDbMap().get(rs.getString("gdb_node")+rs.getString("gdb_leaf").split("::")[0]);

            String sourceList = dbConnection.sourceList;
            boolean useAll = theRequestDetails.getTenantId().equals("all");
            boolean correctNode = rs.getString("gdb_node").equals(dbConnection.node);
            boolean correctSourceList = dbConnection.sourceList.equals(theRequestDetails.getTenantId());
            if (!useAll && !(correctNode && correctSourceList)){
               continue;
            }

            if(rs.getString("UUID").equals("")){
               throw new ResourceNotFoundException(theId);
            }

            if(multiple){
               Meta meta = retVal.getMeta().addTag("http://terminology.hl7.org/CodeSystem/common-tags","actionable","Actionable");
               retVal.setMeta(meta);
               return retVal;
            }


            retVal.setUUID(new IdType(rs.getString("UUID")));
            retVal.setId(new IdType(rs.getString("UUID")));
            retVal.setGroup(new IntegerType(rs.getInt("ZGROUP")));
            retVal.setGroupID(new StringType(rs.getString("group_id")));
            retVal.setLocation(new StringType(rs.getString("LOCATION")));
            retVal.setDateOfTest(new DateType(rs.getDate("DATE_OF_TEST")));
            retVal.setDateOfAnswer(new DateType(rs.getDate("DATE_OF_ANSWER")));

            JSONObject obj = new JSONObject(rs.getString("answers"));
            if(!obj.has("item")){
               QuestionnaireResponseItemComponent item = retVal.addItem();
               encodeJsonObj(obj,item, "");
            } else {
               String input = rs.getString("answers");
               input = input.substring(0,1) + "\"resourceType\":\"QuestionnaireResponse\"," + input.substring(1);
               retVal.setItem(ctx.newJsonParser().parseResource(QuestionnaireResponse.class, input).getItem());
            }


            BooleanType diseaseStatus = new BooleanType();
            if(Objects.equals(rs.getString("ZSTATUS"), "false")){
               diseaseStatus.setValue(false);
            } else if (Objects.equals(rs.getString("ZSTATUS"), "true")) {
               diseaseStatus.setValue(true);
            }
            retVal.setDiseaseStatus(diseaseStatus);

            Meta meta = new Meta().setSource(sourceList);
            retVal.setMeta(meta);

            multiple = true;


         }
         if(!multiple){
            throw new ResourceNotFoundException(theId);
         }

         return retVal;



      } catch (SQLException e) {
         throw new RuntimeException(e);
      }



   }

   /**
    * Parse json obj.
    *
    * @param obj   the obj
    * @param main  the main
    * @param level the level
    */
   private void encodeJsonObj(JSONObject obj, QuestionnaireResponseItemComponent main, String level){
      Iterator<String> keys = obj.keys();
      int nextLevel = 1;
      while(keys.hasNext()){
         String key = keys.next();
         Object value = obj.get(key);
         if (String.class.equals(value.getClass())) {
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setText(key);
            item.setLinkId(level+(nextLevel++));
            QuestionnaireResponseItemAnswerComponent answer = item.addAnswer();
            answer.setValue(new StringType((String)value));
         } else if (Integer.class.equals(value.getClass())) {
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setText(key);
            item.setLinkId(level+(nextLevel++));
            QuestionnaireResponseItemAnswerComponent answer = item.addAnswer();
            answer.setValue(new IntegerType((Integer)value));
         } else if (JSONArray.class.equals(value.getClass())){
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setText(key);
            item.setLinkId(level+(nextLevel));
            encodeJsonArray((JSONArray)value, item, level+(nextLevel++)+".");
         } else if (JSONObject.class.equals(value.getClass())){
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setText(key);
            item.setLinkId(level+(nextLevel));
            encodeJsonObj((JSONObject) value, item, level+(nextLevel++)+".");
         }
      }
   }

   /**
    * Parse json array.
    *
    * @param arr   the arr
    * @param main  the main
    * @param level the level
    */
   private void encodeJsonArray(JSONArray arr, QuestionnaireResponseItemComponent main, String level){
      int nextLevel = 1;
      Iterator<Object> iter = arr.iterator();
      while(iter.hasNext()){
         Object value = iter.next();
         if (String.class.equals(value.getClass())) {
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setLinkId(level+(nextLevel++));
            QuestionnaireResponseItemAnswerComponent answer = item.addAnswer();
            answer.setValue(new StringType((String)value));
         } else if (Integer.class.equals(value.getClass())) {
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setLinkId(level+(nextLevel++));
            QuestionnaireResponseItemAnswerComponent answer = item.addAnswer();
            answer.setValue(new IntegerType((Integer)value));
         } else if (JSONArray.class.equals(value.getClass())){
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setLinkId(level+(nextLevel));
            encodeJsonArray((JSONArray)value, item, level+(nextLevel++)+".");
         } else if (JSONObject.class.equals(value.getClass())){
            QuestionnaireResponseItemComponent item = main.addItem();
            item.setLinkId(level+(nextLevel));
            encodeJsonObj((JSONObject) value, item, level+(nextLevel++)+".");
         }
      }

   }


   // ITEM AND TEXT == []
   // ITEM NO TEXT == DIG DEEPER
   // TEXT NO ITEM == FIND VALUE IN ANSWER
   public String encodeJsonObj(JSONObject obj){
      Set<String> keys = obj.keySet();
      keys.contains("item");
      return null;
   }



}
