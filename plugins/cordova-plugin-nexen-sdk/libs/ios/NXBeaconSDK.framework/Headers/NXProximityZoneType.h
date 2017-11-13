
#ifndef ProximityApp_NXProximityZoneType_h
#define ProximityApp_NXProximityZoneType_h

@import Foundation;

typedef enum
{
    NXProximityZoneType_TextPush,
    NXProximityZoneType_Html,
    NXProximityZoneType_FollowTrack,
    NXProximityZoneType_Video,
    NXProximityZoneType_Unknown
} NXProximityZoneType;

NXProximityZoneType NXProximityZoneTypeFromString(NSString * string);

#endif
