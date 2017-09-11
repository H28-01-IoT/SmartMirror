
package iot.com.smartmirror.network.json.cloudvision;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.HashMap;
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
public class Position {

    public double x;
    public double y;
    public double z;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Position withX(double x) {
        this.x = x;
        return this;
    }

    public Position withY(double y) {
        this.y = y;
        return this;
    }

    public Position withZ(double z) {
        this.z = z;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Position withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
