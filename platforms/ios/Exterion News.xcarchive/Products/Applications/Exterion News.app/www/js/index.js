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
    personalisation: {
        sex: "male",
        age: "",
        uncheckedItems: [],
        uncheckedItemsNumbers: []
    },

    // Application Constructor
    initialize: function () {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
        document.querySelector('.settings').addEventListener('click', function () {
            document.querySelector('.settings-wrapper').className += " open";
        });

        document.querySelector('.settings.close').addEventListener('click', function () {
            document.querySelector('.settings-wrapper').classList.remove('open')
        });

        this.initEventListeners();
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function () {
        // this.receivedEvent('deviceready');

        this.initBeaconSearch();
    },

    initEventListeners: function () {
        var ageInput = document.querySelector('#age-input');
        var saveBtn = document.querySelector('#save-btn');
        var intrestList = document.querySelectorAll('.interest');
        var radios = document.querySelectorAll('.segment__input');
        var that = this;

        for (var x = 0, max = radios.length; x < max; x++) {
            radios[x].onclick = function () {
                that.personalisation.sex = this.value;
            }
        }

        ageInput.addEventListener("focusout", function () {
            that.personalisation.age = ageInput.value;
        }, false);

        for (var i = 0; i < intrestList.length; i++) {
            document.querySelector('#' + intrestList[i].id).addEventListener('change', function () {
                if (this.checked) {
                    var index = that.personalisation.uncheckedItems.indexOf(this.id);
                    var indexNumber = that.personalisation.uncheckedItemsNumbers.indexOf(Number(this.name));

                    if (index > -1) {
                        that.personalisation.uncheckedItems.splice(index, 1);
                    }

                    if (indexNumber > -1) {
                        that.personalisation.uncheckedItemsNumbers.splice(indexNumber, 1);
                    }
                } else {
                    that.personalisation.uncheckedItems.push(this.id);
                    that.personalisation.uncheckedItemsNumbers.push(Number(this.name));

                    console.log(that.personalisation.uncheckedItemsNumbers);
                }
            });
        }

        saveBtn.addEventListener("click", function () {
            document.querySelector('.settings-wrapper').classList.remove('open');
            that.initBeaconSearch();
        }, false);
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

        if(this.personalisation.age) {
            var tags = [

            ]
        }

        var options = {
            disabledCategories: this.personalisation.uncheckedItemsNumbers,
            tags: {
                sex: this.personalisation.sex,
                age: this.personalisation.age
            },
            allowBackgroundScanning: true,          // Android only
            notifications: {
                showNotifications: true,
                textColor: "#ff9900",               // Android only
                smallIconName: "notification_icon"  // Android only
            }
        };

        plugins.NexenSDK.startScanning(options, function (data) {
            //The scanning of beacons that are defined in the Nexen platform
            this.onLocationsLoaded();
        }, function (err) {
            console.log(err);
        });

        plugins.NexenSDK.onLocationsLoaded(function (data) {
            //When a user is in the beacon zone
            console.log(data);
        });

        plugins.NexenSDK.onProximityZoneNotification(function (data) {
            console.log(data);
        }, function (err) {
            console.log(err);
        });

        plugins.NexenSDK.onPushNotificationTappedForZone(beaconId, contentTypeId, (data) => {
            document.querySelector('h1').innerHTML = data.toJSON();
            logToDom(data);
            logToDom(data.toJSON());
        }, (err) => {
            console.log(err);
            logToDom(err);
        });
    }
};

app.initialize();