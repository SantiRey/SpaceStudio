package de.spaceStudio.client.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.spaceStudio.server.model.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

public final class RequestUtils {

    private final static Logger LOG = Logger.getLogger(RequestUtils.class.getName());

    /**
     * Prepares the headers and other configurations
     *
     * @param url
     * @param payload
     * @param httpMethod
     * @return
     */
    public static Net.HttpRequest setupRequest(String url, String payload, String httpMethod) {
        Net.HttpRequest request = new Net.HttpRequest(httpMethod);
        request.setTimeOut(0);
        request.setUrl(url);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setContent(payload);
        return request;
    }

    public static String genericRequest(String url, boolean shipRequest, Integer id, String method, Object payload) {
                ObjectMapper objectMapper = new ObjectMapper();
        Net.HttpRequest r = null;
        try {
            if (!payload.equals("")) {
                r = setupRequest(url, objectMapper.writeValueAsString(payload), method);
            }
            else {
                r = setupRequest(url, "", method);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        final String[] responseString = {null};

        LOG.info("Sending get Request to: " + url);
        Gdx.net.sendHttpRequest(r, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                LOG.info("statusCode: " + statusCode);
                responseString[0] = httpResponse.getResultAsString();

                if (url.contains("sections")) {
                    try {
                        Global.combatSections.put(id, objectMapper.readValue(responseString[0], new TypeReference<List<Section>>() {
                        }));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else if (url.contains("weapon")) {
                    try {
                        Global.combatWeapons.put(id, objectMapper.readValue(responseString[0], new TypeReference<List<Weapon>>() {
                        }));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else if (url.contains("crewMembers")) {
                    try {
                        Global.combatCrew.put(id, objectMapper.readValue(responseString[0], new TypeReference<List<CrewMember>>() {
                        }));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else  if (url.contains("canLand")) {
                    try {
                        Global.allReady = objectMapper.readValue(responseString[0], new TypeReference<Boolean>() {});
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else if (shipRequest) {
                    try {
                        Global.currentShipPlayer = objectMapper.readValue(responseString[0], new TypeReference<Ship>() {});
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else if (url.contains("fightState")) {
                    try {
                        Global.fightState.put(id,  objectMapper.readValue(responseString[0], new TypeReference<FightState>() {}));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void failed(Throwable t) {
                LOG.severe("Request Failed");
            }

            @Override
            public void cancelled() {
                LOG.severe("Request Canceled");
            }
        });

        return responseString[0];
    }

    public static void weaponsByShip(Ship ship) {
        genericRequest(Global.SERVER_URL + Global.ASK_FOR_SHIP + "/" + ship.getId() + "/" + Global.WEAPONS,
                false, ship.getId(), Net.HttpMethods.GET, "");
    }

    public static void sectionsByShip(Ship ship) {
        genericRequest(Global.SERVER_URL + Global.ASK_FOR_SHIP + "/" + ship.getId() + "/" + Global.SECTIONS,
                false, ship.getId(), Net.HttpMethods.GET, "");
    }

    public static void crewMemeberByShip(Ship ship) {
        genericRequest(Global.SERVER_URL + Global.ASK_FOR_SHIP + "/" + ship.getId() + "/" + Global.CREWMEMBERS,
                false, ship.getId(), Net.HttpMethods.GET, "");
    }

    public static void hasLanded(Player player) {
        genericRequest(Global.SERVER_URL + Global.HAS_LANDED,
                false,0,   Net.HttpMethods.POST, player);
    }

    public static void canJump(Player player) {
        genericRequest(Global.SERVER_URL + Global.CAN_LAND,
                false, 0, Net.HttpMethods.GET, player);
    }

    public static void getShip(Ship ship) {
        genericRequest(Global.SERVER_URL + Global.ASK_FOR_SHIP + "/" + ship.getId(),
                true, ship.getId(), Net.HttpMethods.GET, "");
    }

    public static void getFightState(Actor actor) {
        genericRequest(Global.SERVER_URL + Global.GAME + Global.FIGHT_STATE, false, actor.getId(), Net.HttpMethods.GET, actor);
    }

    public static void setFightState(Actor actor) {
        genericRequest(Global.SERVER_URL + Global.GAME + Global.FIGHT_STATE, false, actor.getId(), Net.HttpMethods.POST, actor);
    }
}
