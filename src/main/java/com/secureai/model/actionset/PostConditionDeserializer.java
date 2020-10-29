package com.secureai.model.actionset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.secureai.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import com.secureai.model.stateset.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostConditionDeserializer extends StdDeserializer<Action.PostNodeStateFunction> {

    public PostConditionDeserializer() {
        this(null);
    }

    public PostConditionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Action.PostNodeStateFunction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return this.parsePostConditions(jsonParser.getValueAsString());
    }

    private Action.PostNodeStateFunction parsePostConditions(String str) {
        if (str == null || str.equals("~") || str.equals("null"))
            return (state, i) -> {
            };

        List<Action.PostNodeStateFunction> andConditions = new ArrayList<>();
        double rnd = RandomUtils.random.nextDouble();
        if (!str.contains(", "))
            andConditions.add(this.parsePostCondition(str, rnd));
        else {
            for (String andConditionString : str.split(", ")) {
                andConditions.add(this.parsePostCondition(andConditionString, rnd));
            }
        }

        return andConditions.stream().reduce((a, b) -> (state, i) -> {
            a.run(state, i);
            b.run(state, i);
        }).orElse(null);
    }

    private Action.PostNodeStateFunction parsePostCondition(String str, double rnd) {
        String[] components = str.split(" = ");
        State nodeState = State.valueOf(StringUtils.substringBetween(components[0], "[", "]"));
        double threshold = Double.parseDouble(StringUtils.substringBetween(components[1], "rand(", ")"));

        return (state, i) -> state.set(i, nodeState, rnd < threshold);
    }
}
