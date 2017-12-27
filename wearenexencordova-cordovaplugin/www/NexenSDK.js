var exec = require('cordova/exec');

exports.startScanning = function(options, success, error) {
    if (!options) {
        options = {};
    }
    exec(success, error, 'NexenSDK', 'startScanning', [options]);
};

exports.stopScanning = function(success, error) {
    exec(success, error, 'NexenSDK', 'stopScanning', []);
};

exports.onProximityZoneNotification = function(success, error) {
    exec(success, error, 'NexenSDK', 'onProximityZoneNotification', []);
};

exports.onLocationsLoaded = function(success, error) {
    exec(success, error, 'NexenSDK', 'onLocationsLoaded', []);
};

exports.onPushNotificationTappedForZone = function(zoneId, contentTypeId, success, error) {
    exec(success, error, 'NexenSDK', 'onPushNotificationTappedForZone', [zoneId, contentTypeId]);
};

exports.onOpenUrl = function(success, error) {
    exec(success, error, 'NexenSDK', 'onOpenUrl', []);
};
