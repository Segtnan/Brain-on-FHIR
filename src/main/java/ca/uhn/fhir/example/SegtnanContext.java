package ca.uhn.fhir.example;

import ca.uhn.fhir.context.FhirContext;

/**
 * The type Segtnan context.
 */
public class SegtnanContext {
   /**
    * Get context fhir context.
    *
    * @return the fhir context
    */
   public static FhirContext getContext(){
      FhirContext ctx = FhirContext.forR4();
      return ctx;
   }
}
