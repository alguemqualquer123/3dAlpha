package game.core;

import java.util.UUID;

public class TriggerEvent {
   // AddEventListener eventListener = new AddEventListener(); // apenas altilizada quando for registrar um evento

    public TriggerEvent(String event, UUID uuid, Object object) {
        System.out.println("Event: " + event + " uuid: " + uuid + " object: " + object);
    }
}
