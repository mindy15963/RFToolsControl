package mcjty.rftoolscontrol.logic.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterDescription {

    private final String name;
    private final List<String> description;
    private final ParameterType type;

    private ParameterDescription(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.description = new ArrayList<>(builder.description);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public ParameterType getType() {
        return type;
    }

    public static class Builder {

        private String name;
        private List<String> description;
        private ParameterType type;

        public Builder type(ParameterType type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String... description) {
            this.description = new ArrayList<>();
            Collections.addAll(this.description, description);
            return this;
        }

        public ParameterDescription build() {
            return new ParameterDescription(this);
        }
    }
}
