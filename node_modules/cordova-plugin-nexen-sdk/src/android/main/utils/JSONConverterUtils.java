package be.wearenexen.cordova.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import be.wearenexen.network.response.beacon.BaseNexenZone;
import be.wearenexen.network.response.beacon.Location;
import be.wearenexen.network.response.beacon.NexenBeaconZone;
import be.wearenexen.network.response.beacon.Schedule;
import be.wearenexen.network.response.beacon.content.Content;


public class JSONConverterUtils {

    public static JSONArray convertNexenBeaconZoneCollection(Collection<NexenBeaconZone> beaconZones) throws JSONException {
        JSONArray result = new JSONArray();

        for (NexenBeaconZone beaconZone : beaconZones) {
            result.put(convertNexenBeaconZone(beaconZone));
        }

        return result;
    }

    public static JSONObject convertNexenBeaconZone(NexenBeaconZone beaconZone) throws JSONException {

        if (beaconZone == null) {
            return new JSONObject();
        }

        JSONObject result = new JSONObject();
        result.put("major", beaconZone.getMajor());
        result.put("minor", beaconZone.getMinor());
        result.put("key", beaconZone.getKey());
        result.put("manufacturer", beaconZone.getManufacturer());
        result.put("manufacturerId", beaconZone.getManufacturerId());
        result.put("uuid", beaconZone.getUuid());
        result.put("id", beaconZone.getId());
        result.put("name", beaconZone.getName());
        result.put("content", convertContent(beaconZone.getContent()));
        result.put("schedule", convertSchedule(beaconZone.getSchedule()));
        return result;
    }

    public static JSONObject convertBaseNexenZone(BaseNexenZone zone) throws JSONException {

        if (zone == null) {
            return new JSONObject();
        }

        JSONObject result = new JSONObject();
        result.put("id", zone.getId());
        result.put("name", zone.getName());
        result.put("validUntil", zone.getValidUntil());
        result.put("schedule", convertSchedule(zone.getSchedule()));
        result.put("content", convertContent(zone.getContent()));
        return result;
    }

    public static JSONObject convertSchedule(Schedule schedule) throws JSONException {

        if (schedule == null) {
            return new JSONObject();
        }

        JSONObject result = new JSONObject();
        result.put("id", schedule.getId());
        result.put("name", schedule.getName());
        return result;
    }

    public static JSONObject convertContent(Content content) throws JSONException {

        if (content == null) {
            return new JSONObject();
        }

        JSONObject result = new JSONObject();
        result.put("id", content.getId());
        result.put("name", content.getName());
        result.put("categoryId", content.getCategoryId());
        result.put("dwellingTimeSeconds", content.getDwellingTimeSeconds());
        result.put("language", content.getLanguage());
        result.put("message", content.getMessage());
        result.put("maxIntervalSeconds", content.getMaxIntervalSeconds());
        result.put("messageIconUrl", content.getMessageIconUrl());
        result.put("notificationId", content.getNotificationId());
        result.put("notifyAfterXEvents", content.getNotifyAfterXEvents());
        result.put("repeatDelaySeconds", content.getRepeatDelaySeconds());
        result.put("richMediaUrl", content.getRichMediaUrl());
        result.put("typeContent", content.getTypeContent());
        return result;
    }

    public static JSONObject convertLocation(Location location) throws JSONException {

        if (location == null) {
            return new JSONObject();
        }

        JSONObject result = new JSONObject();
        result.put("name", location.getName());
        result.put("id", location.getId());
        result.put("latitude", location.getLatitude());
        result.put("longitude", location.getLongitude());
        return result;
    }

    public static JSONArray convertLocationCollection(List<Location> locations) throws JSONException {
        JSONArray result = new JSONArray();

        for (Location location : locations) {
            result.put(convertLocation(location));
        }

        return result;
    }
}
