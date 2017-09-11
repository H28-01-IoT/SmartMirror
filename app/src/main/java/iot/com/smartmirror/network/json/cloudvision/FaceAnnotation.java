
package iot.com.smartmirror.network.json.cloudvision;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class FaceAnnotation {

    public BoundingPoly boundingPoly;
    public FdBoundingPoly fdBoundingPoly;
    public List<Landmark> landmarks = null;
    public double rollAngle;
    public double panAngle;
    public double tiltAngle;
    public double detectionConfidence;
    public double landmarkingConfidence;
    public String joyLikelihood;
    public String sorrowLikelihood;
    public String angerLikelihood;
    public String surpriseLikelihood;
    public String underExposedLikelihood;
    public String blurredLikelihood;
    public String headwearLikelihood;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public FaceAnnotation withBoundingPoly(BoundingPoly boundingPoly) {
        this.boundingPoly = boundingPoly;
        return this;
    }

    public FaceAnnotation withFdBoundingPoly(FdBoundingPoly fdBoundingPoly) {
        this.fdBoundingPoly = fdBoundingPoly;
        return this;
    }

    public FaceAnnotation withLandmarks(List<Landmark> landmarks) {
        this.landmarks = landmarks;
        return this;
    }

    public FaceAnnotation withRollAngle(double rollAngle) {
        this.rollAngle = rollAngle;
        return this;
    }

    public FaceAnnotation withPanAngle(double panAngle) {
        this.panAngle = panAngle;
        return this;
    }

    public FaceAnnotation withTiltAngle(double tiltAngle) {
        this.tiltAngle = tiltAngle;
        return this;
    }

    public FaceAnnotation withDetectionConfidence(double detectionConfidence) {
        this.detectionConfidence = detectionConfidence;
        return this;
    }

    public FaceAnnotation withLandmarkingConfidence(double landmarkingConfidence) {
        this.landmarkingConfidence = landmarkingConfidence;
        return this;
    }

    public FaceAnnotation withJoyLikelihood(String joyLikelihood) {
        this.joyLikelihood = joyLikelihood;
        return this;
    }

    public FaceAnnotation withSorrowLikelihood(String sorrowLikelihood) {
        this.sorrowLikelihood = sorrowLikelihood;
        return this;
    }

    public FaceAnnotation withAngerLikelihood(String angerLikelihood) {
        this.angerLikelihood = angerLikelihood;
        return this;
    }

    public FaceAnnotation withSurpriseLikelihood(String surpriseLikelihood) {
        this.surpriseLikelihood = surpriseLikelihood;
        return this;
    }

    public FaceAnnotation withUnderExposedLikelihood(String underExposedLikelihood) {
        this.underExposedLikelihood = underExposedLikelihood;
        return this;
    }

    public FaceAnnotation withBlurredLikelihood(String blurredLikelihood) {
        this.blurredLikelihood = blurredLikelihood;
        return this;
    }

    public FaceAnnotation withHeadwearLikelihood(String headwearLikelihood) {
        this.headwearLikelihood = headwearLikelihood;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public FaceAnnotation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
