/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        // this.receivedEvent('deviceready');


        function onPrompt(results) {
            alert("You selected button number " + results.buttonIndex + " and entered " + results.input1);
        }

        navigator.notification.prompt(
            'Please enter your name',  // message
            onPrompt,                  // callback to invoke
            'Registration',            // title
            ['Ok','Exit'],             // buttonLabels
            'Jane Doe'                 // defaultText
        );

        this.initBeaconSearch();
    },

    initBeaconSearch: function () {
        var logToDom = function (message) {
            var e = document.createElement('label');
            e.innerText = message;

            var br = document.createElement('br');
            var br2 = document.createElement('br');
            document.body.appendChild(e);
            document.body.appendChild(br);
            document.body.appendChild(br2);

            window.scrollTo(0, window.document.height);
        };

        var options = {
            disabledCategories: [],
            tags: [],
            allowBackgroundScanning: true,          // Android only
            notifications: {
                showNotifications: true,
                textColor: "#ff9900",               // Android only
                smallIconName: "notification_icon"  // Android only
            }
        };

        plugins.NexenSDK.startScanning(options, function (data) {
            console.log(data);
            logToDom(JSON.stringify(data));
            this.onLocationsLoaded();
        }, function (err) {
            console.log(err);
        });

        plugins.NexenSDK.onLocationsLoaded(function (data) {
            logToDom(data.toJSON());
            console.log(data);
        });

        plugins.NexenSDK.onProximityZoneNotification(function (data) {
            console.log(data);
        }, function (err) {
            console.log(err);
        })
    }
};

app.initialize();