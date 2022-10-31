package ca.uhn.fhir.example;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.StructureDefinition;

/**
 * The type Segtnan questionnaire response.
 */
@ResourceDef(name="QuestionnaireResponse")
public class SegtnanQuestionnaireResponse extends QuestionnaireResponse {


   /**
    * The Uuid.
    */
   @Child(name = "UUID")
   @Extension(url="http://acme.org/#uuid", definedLocally = true, isModifier = false)
   private IdType UUID;

   /**
    * The Group.
    */
   @Child(name = "Group")
   @Extension(url="http://acme.org/#group", definedLocally = true, isModifier = false)
   private IntegerType Group;

   /**
    * The Group id.
    */
   @Child(name = "GroupID")
   @Extension(url="http://acme.org/#groupid", definedLocally = true, isModifier = false)
   private StringType GroupID;

   /**
    * The Disease status.
    */
   @Child(name = "DiseaseStatus")
   @Extension(url="http://acme.org/#diseasestatus", definedLocally = true, isModifier = false)
   private BooleanType DiseaseStatus;

   /**
    * The Date of test.
    */
   @Child(name = "DateOfTest")
   @Extension(url="http://acme.org/#dateoftest", definedLocally = true, isModifier = false)
   private DateType DateOfTest;

   /**
    * The Date of answer.
    */
   @Child(name = "DateOfAnswer")
   @Extension(url="http://acme.org/#dateofanswer", definedLocally = true, isModifier = false)
   private DateType DateOfAnswer;

   /**
    * The Location.
    */
   @Child(name = "Location")
   @Extension(url="http://acme.org/#location", definedLocally = true, isModifier = false)
   private StringType Location;


   /**
    * Gets uuid.
    *
    * @return the uuid
    */
   public IdType getUUID() {
      return UUID;
   }

   /**
    * Sets uuid.
    *
    * @param UUID the uuid
    */
   public void setUUID(IdType UUID) {
      this.UUID = UUID;
   }

   /**
    * Gets group.
    *
    * @return the group
    */
   public IntegerType getGroup() {
      return Group;
   }

   /**
    * Sets group.
    *
    * @param group the group
    */
   public void setGroup(IntegerType group) {
      Group = group;
   }

   /**
    * Gets group id.
    *
    * @return the group id
    */
   public StringType getGroupID() {
      return GroupID;
   }

   /**
    * Sets group id.
    *
    * @param groupID the group id
    */
   public void setGroupID(StringType groupID) {
      GroupID = groupID;
   }

   /**
    * Gets disease status.
    *
    * @return the disease status
    */
   public BooleanType getDiseaseStatus() {
      return DiseaseStatus;
   }

   /**
    * Sets disease status.
    *
    * @param diseaseStatus the disease status
    */
   public void setDiseaseStatus(BooleanType diseaseStatus) {
      DiseaseStatus = diseaseStatus;
   }

   /**
    * Gets date of test.
    *
    * @return the date of test
    */
   public DateType getDateOfTest() {
      return DateOfTest;
   }

   /**
    * Sets date of test.
    *
    * @param dateOfTest the date of test
    */
   public void setDateOfTest(DateType dateOfTest) {
      DateOfTest = dateOfTest;
   }

   /**
    * Gets date of answer.
    *
    * @return the date of answer
    */
   public DateType getDateOfAnswer() {
      return DateOfAnswer;
   }

   /**
    * Sets date of answer.
    *
    * @param dateOfAnswer the date of answer
    */
   public void setDateOfAnswer(DateType dateOfAnswer) {
      DateOfAnswer = dateOfAnswer;
   }

   /**
    * Gets location.
    *
    * @return the location
    */
   public StringType getLocation() {
      return Location;
   }

   /**
    * Sets location.
    *
    * @param location the location
    */
   public void setLocation(StringType location) {
      Location = location;
   }
}
