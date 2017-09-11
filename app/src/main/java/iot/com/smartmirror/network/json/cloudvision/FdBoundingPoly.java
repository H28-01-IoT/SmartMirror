
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
public class FdBoundingPoly {

    public List<Vertex_> vertices = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public FdBoundingPoly withVertices(List<Vertex_> vertices) {
        this.vertices = vertices;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public FdBoundingPoly withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}