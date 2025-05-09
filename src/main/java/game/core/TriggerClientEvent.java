package game.core;

import java.util.UUID;

public class TriggerClientEvent {

    public TriggerClientEvent(String event, UUID uuid, Object object) {
        System.out.println("Event: " + event + " uuid: " + uuid + " object: " + object);
    }
}
