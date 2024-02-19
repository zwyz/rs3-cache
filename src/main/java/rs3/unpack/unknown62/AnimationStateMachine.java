package rs3.unpack.unknown62;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class AnimationStateMachine extends AnimatorController {
    public String type = "AnimationStateMachine";
    public String initial;
    public List<NamedState> states;
    public List<Transition> transitions;

    public AnimationStateMachine(Packet packet) {
        initial = packet.gjstr();
        var statesCount = packet.g4s();
        states = new ArrayList<>(statesCount);

        for (var i = 0; i < statesCount; i++) {
            states.add(new NamedState(packet));
        }

        var transitionsCount = packet.g4s();
        transitions = new ArrayList<>(transitionsCount);

        for (var i = 0; i < transitionsCount; i++) {
            transitions.add(new Transition(packet));
        }
    }
}
