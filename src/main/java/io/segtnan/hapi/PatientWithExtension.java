package io.segtnan.hapi;


import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

@ResourceDef(name="Patient")
public class PatientWithExtension extends Patient {

    @Child(name = "MyExtensionValue")
    @Extension(url="http://acme.org/#MyExtensionValue", definedLocally = true, isModifier = false)
    private StringType myExtensionValue;


    public StringType getMyExtensionValue() {
        return myExtensionValue;
    }

    public PatientWithExtension setMyExtensionValue(StringType myExtensionValue) {
        this.myExtensionValue = myExtensionValue;
        return this;
    }
}
