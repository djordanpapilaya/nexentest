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
        uncheckedItems: [],
        uncheckedItemsNumbers: [4, 5, 8, 9, 13, 15]
    },

    that: this,

    // Application Constructor
    initialize: function () {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
        document.querySelector('.settings').addEventListener('click', function () {
            document.querySelector('.settings-wrapper').className += " open";
        });

        document.querySelector('.settings.close').addEventListener('click', function () {
            document.querySelector('.settings-wrapper').classList.remove('open')
        });

        document.querySelector('#takeover-close').addEventListener('click', function () {
            document.querySelector('.takeover').classList.remove('open')
        });

        this.initEventListeners();
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function () {
        var that = this;
        // this.receivedEvent('deviceready');
        console.log('ONDEVICE READY GET ITEM');
        NativeStorage.getItem("settings", this.getSuccess, getError);

        function getSuccess(obj) {
            console.log('succes', obj);
            if(obj.age) { that.personalisation.age = obj.age; }
            if(obj.sex) { that.personalisation.sex = obj.sex; }

            that.personalisation.uncheckedItemsNumbers = obj.uncheckedItemsNumbers;

            that.initBeaconSearch();
        }

        function getError() {
            console.log('ONDEVICE READY ERROR');
            that.initBeaconSearch();
        }
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
            NativeStorage.setItem("settings", that.personalisation, that.setSuccess, that.setError);
            document.querySelector('.settings-wrapper').classList.remove('open');

            plugins.NexenSDK.stopScanning((data) => {
                console.log('START SCANNING AGAIN');
                that.initBeaconSearch();
            }, (err) => {
                // Handle error
            });
        }, false);
    },

    setSuccess: function (obj) {
        console.log('success ', obj.toJSON());
    },

    setError: function (error) {
        console.log('error ', error.toJSON());
    },

    initBeaconSearch: function () {
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

        console.log('options', options.disabledCategories);
        console.log('options', options.tags.sex);
        console.log('options', options.tags.age);
        var that = this;

        plugins.NexenSDK.startScanning(options, (data) => {
            // Subscribe to the plugin callbacks
            console.log('startScanning', data);
            console.log('startScanning', data.toJSON());
            this.onLocationsLoaded();
            this.onProximityZoneNotification();
        }, (err) => {
            console.log('error startScanning ', err);
        });

        plugins.NexenSDK.onLocationsLoaded((data) => {
            console.log('onLocationsLoaded');
            //When a user is in the beacon zone
            console.log(data);
            console.log(data.toJSON());
        });

        plugins.NexenSDK.onProximityZoneNotification((data) => {
            console.log('onProximityZoneNotification', data.toJSON());
            console.log(data);
        }, function (err) {
            console.log(err);
        });

        plugins.NexenSDK.onOpenUrl((url) => {
            var takeover = document.querySelector('.takeover');
            var takeoverCanvas = document.querySelector('#takeover-canvas');
            console.log('onOpenUrl', url);


            switch(url) {
                case 'http://a.nl':
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/staatsloterij.JPG')";
                    break;
                case 'http://b.nl':
                    console.log('Senseo');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/senseo.JPG')";
                    break;
                case 'http://c.nl':
                    console.log('PMA - zorgverzekering');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/pma.JPG')";
                    break;
                case 'http://d.nl':
                    console.log('Eurosport');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/eurosport.JPG')";
                    break;
                case 'http://e.nl':
                    console.log('Aveen - Kerst');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/aveen.JPG')";
                    break;
                case 'http://f.nl':
                    console.log('Loreal lippestift');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/loreal.JPG')";
                    break;
                case 'http://g.nl':
                    console.log('Aveen - Gratis parkeren');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/aveen1.JPG')";
                    break;
                case 'http://h.nl':
                    console.log('Andalucia - Reizen');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/andalucia.JPG')";
                    break;
                case 'http://i.nl':
                    console.log('Aveen - Extra koopavond');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/aveen2.JPG')";
                    break;
                case 'http://j.nl':
                    console.log('Disney Coco film');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/disney-coco.JPG')";
                    break;
                case 'http://k.nl':
                    console.log('Royal club');
                    takeover.className += " open";
                    takeoverCanvas.style.backgroundImage = "url('img/royalclub.JPG')";
                    break;

            }

        }, (err) => {
            console.log('onOpenUrl', err)
        });

        plugins.NexenSDK.onPushNotificationTappedForZone(beaconId, contentTypeId, (data) => {
            console.log('onPushNotificationTappedForZone');
            console.log(data);
            console.log('tap-notification',data.toJSON());
        }, (err) => {
            console.log(err);
        });
    }
};

app.initialize();