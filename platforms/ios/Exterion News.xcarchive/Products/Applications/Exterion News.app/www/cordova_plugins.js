cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
  {
    "id": "cordova-plugin-nexen-sdk.NexenSDK",
    "file": "plugins/cordova-plugin-nexen-sdk/www/NexenSDK.js",
    "pluginId": "cordova-plugin-nexen-sdk",
    "clobbers": [
      "plugins.NexenSDK"
    ]
  },
  {
    "id": "cordova-plugin-dialogs.notification",
    "file": "plugins/cordova-plugin-dialogs/www/notification.js",
    "pluginId": "cordova-plugin-dialogs",
    "merges": [
      "navigator.notification"
    ]
  }
];
module.exports.metadata = 
// TOP OF METADATA
{
  "cordova-plugin-compat": "1.2.0",
  "cordova-plugin-nexen-sdk": "0.0.1",
  "cordova-plugin-whitelist": "1.3.2",
  "cordova-custom-config": "4.0.2",
  "cordova-plugin-dialogs": "1.3.3"
};
// BOTTOM OF METADATA
});